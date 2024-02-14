package com.github.zerobranch.beebox.domain.models

import java.io.Serializable

data class BeeboxConfig(
    val appName: String,
    val packageName: String,
    val baseUrl: String,
    val applicationId: String,
    val databaseName: String,
    val versionCode: Int,
    val versionName: String,
    val attachArchivePassword: String,
    val beeboxLogFileName: String,
    val buildType: String,
    val isScreenReaderEnabled: Boolean,
    val installerPackageName: String,
    val webViewVersion: String,
    val telegramPackage: String,
    val databaseVersion: Int,
) : Serializable {

    fun websiteUrl(word: String) = "${baseUrl}word/$word"
}