package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment

class PermissionsDelegate(
    private val fragment: Fragment,
    private val permission: String
) {
    companion object {
        fun checkPermission(context: Context, permission: String): Boolean =
            checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    var onGranted: ((withPermission: Boolean) -> Unit)? = null
    var onDenied: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onGranted?.invoke(true)
            } else {
                val showRationale = fragment.shouldShowRequestPermissionRationale(permission)
                onDenied?.invoke(showRationale)
            }
        }

    fun launch() {
        if (checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted?.invoke(false)
        } else {
            requestPermission()
        }
    }

    fun repeatRequestPermission() = requestPermission()

    private fun requestPermission() = requestPermissionLauncher.launch(permission)
}