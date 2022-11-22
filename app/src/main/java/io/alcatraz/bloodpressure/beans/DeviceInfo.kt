package io.alcatraz.bloodpressure.beans

data class DeviceInfo(
    val versionCode: Int,
    val customerCode: Int,
    val typeCode: Int,
    val modelCode: Int,
    val serialCode: String,
    val customerStr: String
)
