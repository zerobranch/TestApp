package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission

class ActivityPermissionsDelegate(
    private val activity: AppCompatActivity,
    private val permission: String
) {
    companion object {
        fun checkPermission(context: Context, permission: String): Boolean =
            checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    var onGranted: (() -> Unit)? = null
    var onDenied: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onGranted?.invoke()
            } else {
                val showRationale = activity.shouldShowRequestPermissionRationale(permission)
                onDenied?.invoke(showRationale)
            }
        }

    fun launch() {
        if (checkSelfPermission(activity.applicationContext, permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted?.invoke()
        } else {
            requestPermission()
        }
    }

    fun repeatRequestPermission() = requestPermission()

    private fun requestPermission() = requestPermissionLauncher.launch(permission)
}