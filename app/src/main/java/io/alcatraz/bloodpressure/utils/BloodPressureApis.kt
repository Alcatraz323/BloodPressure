package io.alcatraz.bloodpressure.utils

import android.content.Context
import android.content.Intent
import android.hardware.SerialManager
import io.alcatraz.bloodpressure.LogBuff
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.beans.DeviceInfo
import io.alcatraz.bloodpressure.beans.ProcessData
import io.alcatraz.bloodpressure.beans.ResultPacket
import java.util.*

class BloodPressureApis(
    val context: Context,
    var responseInterface: BloodPressureResponseInterface
) : SerialManager.SerialCallback {
    private val OPEN_SERIALPORT = "sprocomm.open.serialport"
    private val BLOODPRESSUREINFO_CUS = byteArrayOf(90, 10, 0, 12, 1, 1, 8, 0, 33, -99)
    private lateinit var mSerialManager: SerialManager

    fun openSerial() {
        mSerialManager = SerialManager.getInstance(context)
        mSerialManager.setOnListener(this)
        val intent2 = Intent()
        intent2.setAction(OPEN_SERIALPORT)
        intent2.putExtra("sericalport", 2)
        context.sendBroadcast(intent2)
    }

    fun closeSerial() {
        mSerialManager.closeSerialPort(2)
    }

    fun requestReadDeviceInfoPrimary() {
        mSerialManager.writeBytes(this.BLOODPRESSUREINFO_CUS, 2)
    }

    fun requestReadDeviceInfoSecondary() {
        mSerialManager.writeBytes(generateCommand(0), 2)
    }

    fun startCalibrationMode() {
        mSerialManager.writeBytes(generateCommand(19), 2)
    }

    fun confirmCalibration() {
        mSerialManager.writeBytes(generateCommand(20), 2)
    }

    fun enterStaticMode() {
        mSerialManager.writeBytes(generateCommand(18), 2)
    }

    fun startMeasure() {
        mSerialManager.writeBytes(generateCommand(1), 2)
    }

    fun stopMeasure() {
        mSerialManager.writeBytes(generateCommand(2), 2)
    }

    private fun generateCommand(commandType: Int): ByteArray {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(1) % 2000
        val month: Int = calendar.get(2) + 1
        val day: Int = calendar.get(5)
        val hour: Int = calendar.get(11)
        val minute: Int = calendar.get(12)
        val second: Int = calendar.get(13)
        val command = byteArrayOf(
            90,
            10,
            commandType.toByte(),
            year.toByte(),
            month.toByte(),
            day.toByte(),
            hour.toByte(),
            minute.toByte(),
            second.toByte(),
            (commandType + 100 + year + month + day + hour + minute + second + 2).toByte()
        )
        return command
    }

    override fun reportData(data: String, type: Int) {
        if (type == 2) {
            val origin = data.split(",")
            val hexStringBuilder = StringBuilder()
            for (i2 in origin.indices) {
                hexStringBuilder.append(Integer.toHexString(origin[i2].toInt()))
                if (i2 < origin.size - 1) {
                    hexStringBuilder.append(",")
                }
            }
            if (origin.size > 3) {
                val packageType = origin[2].toInt()
                if (packageType == 0 && origin.size == 18) /* Device Info */ {
                    LogBuff.I(hexStringBuilder.toString())
                    val versionCode = origin[3].toInt()
                    val customerCode = origin[4].toInt()
                    val typeCode = origin[6].toInt()
                    val modelCode = origin[7].toInt()
                    val serialCodeBuild = StringBuilder()
                    for (i in 8..16) {
                        serialCodeBuild.append(Integer.toHexString(origin[i].toInt()))
                    }
                    val serialCode = serialCodeBuild.toString()
                    serialCodeBuild.setLength(0)
                    val customerStr: String = if (customerCode in 0..2) {
                        context.resources.getStringArray(R.array.entries_for_customer_code)[customerCode]
                    } else {
                        context.resources.getString(R.string.error_unknown)
                    }
                    val deviceInfo = DeviceInfo(
                        versionCode,
                        customerCode,
                        typeCode,
                        modelCode,
                        serialCode,
                        customerStr
                    )

                    responseInterface.onDeviceInfo(deviceInfo)
                } else if (packageType == 2 && origin.size == 8) /* Process report */ {
                    LogBuff.I(hexStringBuilder.toString())
                    val heartbeat = if (origin[3].toInt() == 0) {
                        context.getString(R.string.ad_nb2)
                    } else {
                        context.getString(R.string.ad_pb2)
                    }
                    val pressure = origin[5].toInt() + origin[6].toInt() * 255

                    val processData = ProcessData(heartbeat, pressure)
                    responseInterface.onProcessData(processData)
                } else if (packageType == 3 && origin.size == 14) /* Result report */ {
                    LogBuff.I(hexStringBuilder.toString())
                    var arrhythmia = false
                    var arteriosclerosis = false
                    val binary = Integer.toBinaryString(origin[10].toInt())

                    if (binary.length == 8) {
                        if (binary[7] == '1') {
                            arrhythmia = true
                        } else if (binary[7] == '0') {
                            arrhythmia = false
                        }
                        if (binary[6] == '1') {
                            arteriosclerosis = true
                        } else if (binary[6] == '0') {
                            arteriosclerosis = false
                        }
                    } else if (binary.length == 7) {
                        arrhythmia = false
                        if (binary[6] == '1') {
                            arteriosclerosis = true
                        } else if (binary[6] == '0') {
                            arteriosclerosis = false
                        }
                    } else {
                        arrhythmia = false
                        arteriosclerosis = false
                    }

                    val sbp = (origin[10].toInt() and 127 shl 8) + origin[9].toInt()
                    val dbp = origin[11].toInt()
                    val heartRate = origin[12].toInt()
                    val arrhythmiaStr = if (arrhythmia) {
                        context.getString(R.string.ad_pb2)
                    } else {
                        context.getString(R.string.ad_nb2)
                    }

                    val arteriosclerosisStr = if (arteriosclerosis) {
                        context.getString(R.string.ad_pb2)
                    } else {
                        context.getString(R.string.ad_nb2)
                    }

                    val resultPacket =
                        ResultPacket(arrhythmiaStr, arteriosclerosisStr, sbp, dbp, heartRate)
                    responseInterface.onResultPacket(resultPacket)
                } else if (packageType == 238 && origin.size == 5) /* Error report */ {
                    LogBuff.E(hexStringBuilder.toString())
                    val errCode = origin[3].toInt()
                    val reason = if (errCode in 1..6) {
                        context.resources.getStringArray(R.array.entries_for_error_code)[errCode]
                    } else {
                        context.getString(R.string.error_unknown)
                    }
                    responseInterface.onError(reason)
                } else if (packageType == 5 && origin.size == 5) /* EOF report */ {
                    LogBuff.D("EOF Packet: $hexStringBuilder")
                }
            }
        }
    }

    interface BloodPressureResponseInterface {
        fun onDeviceInfo(deviceInfo: DeviceInfo)
        fun onProcessData(processData: ProcessData)
        fun onResultPacket(resultPacket: ResultPacket)
        fun onError(error: String)
    }
}