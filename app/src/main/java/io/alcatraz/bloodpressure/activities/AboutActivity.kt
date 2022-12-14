package io.alcatraz.bloodpressure.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.widget.AdapterView

import java.util.ArrayList

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import io.alcatraz.bloodpressure.Constants
import io.alcatraz.bloodpressure.Easter
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.adapters.AuthorAdapter
import io.alcatraz.bloodpressure.adapters.QueryElementAdapter
import io.alcatraz.bloodpressure.beans.AuthorElement
import io.alcatraz.bloodpressure.databinding.DialogOpsBinding
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import io.alcatraz.bloodpressure.utils.PackageCtlUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : CompatWithPipeActivity() {
    private var imgs: MutableList<Int> = ArrayList()
    private lateinit var data: MutableList<AuthorElement>
    private lateinit var vibrator: Vibrator
    private lateinit var easter: Easter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initialize()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initialize() {
        easter = Easter(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        initData()
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(about_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val authorAdapter = AuthorAdapter(this, data, imgs)
        about_listview.adapter = authorAdapter
        about_listview.onItemClickListener = AdapterView.OnItemClickListener { _, view, i, _ ->
            when (i) {
                0 -> {
                    if (vibrator.hasVibrator()) {
                        view.post {
                            vibrator.cancel()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        100,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(100)
                            }
                        }
                    }
                    easter.shortClick()
                }
                1 -> showDetailDev()
                2 -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(Constants.OPEN_SOURCE_URL)
                    )
                )
                3 -> showOpenSourceProjectsDialog()
            }
        }

        about_listview.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, view, i, _ ->
                if (i == 0) {
                    view.post {
                        vibrator.cancel()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    200,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
                            vibrator.vibrate(200)
                        }
                    }
                }
                true
            }
    }

    private fun showOpenSourceProjectsDialog() {
        val ospBindings: DialogOpsBinding = DialogOpsBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle(R.string.au_osp)
            .setView(ospBindings.root)
            .setNegativeButton(R.string.ad_nb3, null).show()
        val data = Constants.openSourceProjects
        val adapter = QueryElementAdapter(this, data)
        ospBindings.opsRecycler.layoutManager = LinearLayoutManager(this)
        ospBindings.opsRecycler.itemAnimator = DefaultItemAnimator()
        ospBindings.opsRecycler.adapter = adapter
    }

    private fun showDetailDev() {
        AlertDialog.Builder(this)
            .setTitle(R.string.au_l_2)
            .setMessage("Main Coder: Alcatraz\n")
            .setPositiveButton(R.string.ad_pb, null)
            .show()
    }

    private fun initData() {
        imgs.add(R.drawable.ic_info_outline_black_24dp)
        imgs.add(R.drawable.ic_account_circle_black_24dp)
        imgs.add(R.drawable.ic_code_black_24dp)
        imgs.add(R.drawable.ic_open_in_new_black_24dp)
        data = mutableListOf(
            AuthorElement(getString(R.string.au_l_1), PackageCtlUtils.getVersionName(this)!!),
            AuthorElement(getString(R.string.au_l_2), getString(R.string.au_l_2_1)),
            AuthorElement(getString(R.string.au_l_3), ""),
            AuthorElement(getString(R.string.au_l_4), getString(R.string.au_l_4_1))
        )
    }
}