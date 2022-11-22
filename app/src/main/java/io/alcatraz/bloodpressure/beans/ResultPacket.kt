package io.alcatraz.bloodpressure.beans

data class ResultPacket(
    val arrhythmia: String,
    val arteriosclerosis: String,
    val sbp: Int,
    val dbp: Int,
    val heartRate: Int
)
