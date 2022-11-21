package io.alcatraz.bloodpressure.activities

import android.os.Bundle
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import kotlinx.android.synthetic.main.activity_measure.*

class MeasureActivity : CompatWithPipeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(measure_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {

    }
}