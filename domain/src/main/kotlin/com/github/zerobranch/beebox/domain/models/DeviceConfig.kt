package com.github.zerobranch.beebox.domain.models

import java.io.Serializable

data class DeviceConfig(
    val androidVersion: String,
    val androidAPILevel: Int,
    val displayedVersion: String,
    val deviceBrand: String,
    val deviceModel: String,
    val device: String,
    val cPUABI: String,
    val manufacturer: String,
) : Serializable