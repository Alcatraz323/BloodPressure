package io.alcatraz.bloodpressure.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import io.alcatraz.bloodpressure.AsyncInterface
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.adapters.HistoryAdapter
import io.alcatraz.bloodpressure.beans.ResultPacket
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import io.alcatraz.bloodpressure.utils.IOUtils
import io.alcatraz.bloodpressure.utils.Panels
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : CompatWithPipeActivity() {
    private lateinit var historyFilePath: String
    private val historyListData: ArrayList<ResultPacket> = arrayListOf()
    lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        historyFilePath = filesDir.absolutePath + "/history.dat"
        initViews()
        initData()
    }

    private fun initViews() {
        setSupportActionBar(history_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        historyAdapter = HistoryAdapter(this, historyListData)
        history_recycler.layoutManager = LinearLayoutManager(this)
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        history_recycler.layoutAnimation = controller
        history_recycler.adapter = historyAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        historyListData.clear()
        val historyStr = IOUtils.okioRead(historyFilePath)
        val processSplit = historyStr.split(";")
        for (i in processSplit) {
            if (i != "")
                historyListData.add(ResultPacket.getFromPlainText(i, this))
        }
        historyAdapter.notifyDataSetChanged()
        history_recycler.scheduleLayoutAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.activity_history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.item_hisyory_clear -> Panels.showClearHistoryClearPanel(
                this,
                object : AsyncInterface<String> {
                    override fun onDone(result: String) {
                        initData()
                    }
                })
        }
        return super.onOptionsItemSelected(item)
    }
}