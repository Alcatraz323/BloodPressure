package io.alcatraz.bloodpressure.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import io.alcatraz.bloodpressure.BloodPressureApplication
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.beans.DeviceInfo
import io.alcatraz.bloodpressure.beans.ProcessData
import io.alcatraz.bloodpressure.beans.ResultPacket
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import io.alcatraz.bloodpressure.utils.BloodPressureApis
import io.alcatraz.bloodpressure.utils.IOUtils
import kotlinx.android.synthetic.main.activity_measure.*
import kotlinx.android.synthetic.main.layout_done.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_processing.*

class MeasureActivity : CompatWithPipeActivity() {
    private lateinit var mApplication: BloodPressureApplication
    private lateinit var mApis: BloodPressureApis
    private lateinit var mProcessData: ProcessData
    private lateinit var mResultPacket: ResultPacket
    private lateinit var mErrorMsg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        initViews()
        setup()
    }

    private fun setup() {
        mApplication = application as BloodPressureApplication
        mApis = mApplication.api
        mApis.responseInterface = object : BloodPressureApis.BloodPressureResponseInterface {
            override fun onDeviceInfo(deviceInfo: DeviceInfo) {
                // Not Used
            }

            override fun onProcessData(processData: ProcessData) {
                mProcessData = processData
                runOnUiThread {
                    updateProcessingPanel()
                }
            }

            override fun onResultPacket(resultPacket: ResultPacket) {
                mResultPacket = resultPacket
                val historyFilePath = filesDir.absolutePath + "/history.dat"
                var historyStr = IOUtils.okioRead(historyFilePath)
                historyStr += ";" + mResultPacket.toStorageString()
                IOUtils.okioWrite(historyFilePath, historyStr)
                runOnUiThread {
                    disableStopButton()
                    showIcon()
                    showDonePanel()
                    updateDonePanel()
                }
            }

            override fun onError(error: String) {
                mErrorMsg = error
                runOnUiThread {
                    disableStopButton()
                    showIcon()
                    showErrorPanel()
                    updateErrorPanel()
                }
            }
        }
    }

    private fun startMeasure() {
        mApis.startMeasure()
        measure_card_processing_indicator_image.setImageResource(R.drawable.ic_check_green_24dp)
        disableStartButton()
        showProcessingPanel()
        showProgressBar()
    }

    private fun stopMeasure() {
        mApis.stopMeasure()
        disableStopButton()
        showReadyPanel()
        showIcon()
    }

    @SuppressLint("SetTextI18n")
    private fun updateProcessingPanel() {
        if(mProcessData.heartBeat) {
            heart_beat_detect_indicator_active.visibility = View.VISIBLE
        } else {
            heart_beat_detect_indicator_active.visibility = View.GONE
        }
        pressure_status.text = getString(R.string.process_pressure) + mProcessData.pressure
        pressure_progress.progress = mProcessData.pressure
    }

    private fun updateErrorPanel() {
        measure_card_processing_indicator_image.setImageResource(R.drawable.ic_close)
        error_msg.text = mErrorMsg
    }

    @SuppressLint("SetTextI18n")
    private fun updateDonePanel() {
        measure_card_processing_indicator_image.setImageResource(R.drawable.ic_check_green_24dp)
        arrhythmia.text = getString(R.string.result_arrhythmia) + mResultPacket.arrhythmiaStr
        arteriosclerosis.text = getString(R.string.result_arteriosclerosis) + mResultPacket.arteriosclerosisStr
        sbp.text = getString(R.string.result_sbp) + mResultPacket.sbp
        dbp.text = getString(R.string.result_dbp) + mResultPacket.dbp
        heart_rate.text = getString(R.string.result_heart_rate) + mResultPacket.heartRate
    }

    private fun disableStartButton() {
        measure_start.isClickable = false
        measure_start.setImageResource(R.drawable.ic_baseline_play_arrow_24_invalid)
        measure_stop.isClickable = true
        measure_stop.setImageResource(R.drawable.ic_baseline_stop_24)
    }

    private fun disableStopButton() {
        measure_start.isClickable = true
        measure_start.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        measure_stop.isClickable = false
        measure_stop.setImageResource(R.drawable.ic_baseline_stop_24_invalid)
    }

    private fun showProgressBar() {
        measure_card_processing_indicator_progress_bar.visibility = View.VISIBLE
        measure_card_processing_indicator_image.visibility = View.GONE
    }

    private fun showIcon() {
        measure_card_processing_indicator_progress_bar.visibility = View.GONE
        measure_card_processing_indicator_image.visibility = View.VISIBLE
    }

    private fun showReadyPanel() {
        measure_panel_ready.visibility = View.VISIBLE
        measure_panel_processing.visibility = View.GONE
        measure_panel_done.visibility = View.GONE
        measure_panel_error.visibility = View.GONE
    }

    private fun showProcessingPanel() {
        measure_panel_ready.visibility = View.GONE
        measure_panel_processing.visibility = View.VISIBLE
        measure_panel_done.visibility = View.GONE
        measure_panel_error.visibility = View.GONE
    }

    private fun showDonePanel() {
        measure_panel_ready.visibility = View.GONE
        measure_panel_processing.visibility = View.GONE
        measure_panel_done.visibility = View.VISIBLE
        measure_panel_error.visibility = View.GONE
    }

    private fun showErrorPanel() {
        measure_panel_ready.visibility = View.GONE
        measure_panel_processing.visibility = View.GONE
        measure_panel_done.visibility = View.GONE
        measure_panel_error.visibility = View.VISIBLE
    }

    private fun initViews() {
        setSupportActionBar(measure_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        measure_start.setOnClickListener {
            startMeasure()
        }
        measure_stop.setOnClickListener {
            stopMeasure()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}