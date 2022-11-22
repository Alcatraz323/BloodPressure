package io.alcatraz.bloodpressure.activities

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import io.alcatraz.bloodpressure.BloodPressureApplication
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.beans.DeviceInfo
import io.alcatraz.bloodpressure.beans.ProcessData
import io.alcatraz.bloodpressure.beans.ResultPacket
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import io.alcatraz.bloodpressure.utils.BloodPressureApis
import io.alcatraz.bloodpressure.utils.Utils
import kotlinx.android.synthetic.main.activity_device_info.*

class DeviceInfoActivity : CompatWithPipeActivity() {
    lateinit var mDeviceInfo: DeviceInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(device_info_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        val mApplication = application as BloodPressureApplication
        mApplication.api.responseInterface =
            object : BloodPressureApis.BloodPressureResponseInterface {
                override fun onDeviceInfo(deviceInfo: DeviceInfo) {
                    mDeviceInfo = deviceInfo
                    updateViews()
                }

                override fun onProcessData(processData: ProcessData) {
                    // Not Used
                }

                override fun onResultPacket(resultPacket: ResultPacket) {
                    // Not Used
                }

                override fun onError(error: String) {
                    toast(error)
                }
            }
        mApplication.api.requestReadDeviceInfoPrimary()
    }

    private fun updateViews() {
        hardware_version.text = mDeviceInfo.versionCode.toString()
        hardware_vendor.text = mDeviceInfo.customerStr
        hardware_model.text = mDeviceInfo.modelCode.toString()
        hardware_type.text = mDeviceInfo.typeCode.toString()
        hardware_serial.text = mDeviceInfo.serialCode
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_log_refresh -> initData()
        }
        return super.onOptionsItemSelected(item)
    }
}