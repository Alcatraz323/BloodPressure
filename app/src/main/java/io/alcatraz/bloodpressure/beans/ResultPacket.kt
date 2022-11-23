package io.alcatraz.bloodpressure.beans

import android.content.Context
import io.alcatraz.bloodpressure.R

data class ResultPacket(
    var timeStamp: Long,
    var arrhythmia: Int,
    var arrhythmiaStr: String,
    var arteriosclerosis: Int,
    var arteriosclerosisStr: String,
    var sbp: Int,
    var dbp: Int,
    var heartRate: Int
) {
    fun toStorageString(): String {
        return "$timeStamp,$arrhythmia,$arteriosclerosis,$sbp,$dbp,$heartRate"
    }

    companion object {
        fun getFromPlainText(str: String, context: Context): ResultPacket {
            val processSplit = str.split(",")
            return ResultPacket(
                processSplit[0].toLong(),
                processSplit[1].toInt(),
                if (processSplit[1].toInt() == 0)
                    context.getString(R.string.ad_nb2)
                else
                    context.getString(R.string.ad_pb2),
                processSplit[2].toInt(),
                if (processSplit[2].toInt() == 0)
                    context.getString(R.string.ad_nb2)
                else
                    context.getString(R.string.ad_pb2),
                processSplit[3].toInt(),
                processSplit[4].toInt(),
                processSplit[5].toInt()
            )
        }
    }
}
