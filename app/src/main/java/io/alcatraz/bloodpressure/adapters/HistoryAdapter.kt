package io.alcatraz.bloodpressure.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.alcatraz.bloodpressure.R
import io.alcatraz.bloodpressure.beans.ResultPacket
import io.alcatraz.bloodpressure.extended.CompatWithPipeActivity
import java.text.SimpleDateFormat

class HistoryAdapter(
    private val activity: CompatWithPipeActivity,
    private val data: ArrayList<ResultPacket>
) : RecyclerView.Adapter<HistoryHolder>() {
    private val inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val root = inflater.inflate(R.layout.item_history_recycler, parent, false)
        return HistoryHolder(root)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val packet = data[position]
        holder.timeStampTxv.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(packet.timeStamp)
        val dataPrimaryStr = String.format(
            activity.getString(R.string.history_data_1),
            packet.sbp,
            packet.dbp,
            packet.heartRate
        )
        val dataSecondaryStr = String.format(
            activity.getString(R.string.history_data_2),
            packet.arrhythmiaStr,
            packet.arteriosclerosisStr
        )
        holder.dataPrimaryTxv.text = dataPrimaryStr
        holder.dataSecondaryTxv.text = dataSecondaryStr
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class HistoryHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var timeStampTxv: TextView = itemView.findViewById(R.id.history_item_timestamp)
    var dataPrimaryTxv: TextView = itemView.findViewById(R.id.history_item_data_1)
    var dataSecondaryTxv: TextView = itemView.findViewById(R.id.history_item_data_2)
}