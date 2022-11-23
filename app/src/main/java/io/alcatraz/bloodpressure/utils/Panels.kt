package io.alcatraz.bloodpressure.utils

import androidx.appcompat.app.AlertDialog
import io.alcatraz.bloodpressure.AsyncInterface
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import java.util.*

object Panels {
    fun showClearHistoryClearPanel(activity: CompatWithPipeActivity, asyncInterface: AsyncInterface<String>) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.history_menu_1)
            .setMessage(R.string.history_clear_confirm)
            .setNegativeButton(R.string.ad_nb, null)
            .setPositiveButton(R.string.ad_pb
            ) { _, _ ->
                val historyFilePath = activity.filesDir.absolutePath + "/history.dat"
                IOUtils.okioWrite(historyFilePath, "")
                asyncInterface.onDone("")
            }.show()
    }
}
