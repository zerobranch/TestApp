package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

@RequiresApi(Build.VERSION_CODES.R)
fun getBiometricSettingsIntent() =
    Intent(Settings.ACTION_BIOMETRIC_ENROLL)
        .apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }

fun Context.checkBiometricAuth(
    onBiometricSuccess: () -> Unit,
    onBiometricNonEnrolled: () -> Unit,
    onBiometricError: () -> Unit,
) {
    val biometricManager = BiometricManager.from(this)
    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> onBiometricNonEnrolled.invoke()
        BiometricManager.BIOMETRIC_SUCCESS -> onBiometricSuccess.invoke()
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> onBiometricError.invoke()
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> onBiometricError.invoke()
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> onBiometricError.invoke()
        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> onBiometricError.invoke()
        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> onBiometricError.invoke()
    }
}

fun FragmentActivity.launchStrongBiometricAuth(
    title: String,
    negativeButtonText: String,
    description: String,
    onCancelClick: () -> Unit,
    onAuthenticationSucceeded: () -> Unit
) {
    val biometricPrompt = BiometricPrompt(
        this,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthenticationSucceeded.invoke()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (BiometricPrompt.ERROR_NEGATIVE_BUTTON == errorCode) {
                    onCancelClick.invoke()
                }
            }
        }
    )

    biometricPrompt.authenticate(
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setNegativeButtonText(negativeButtonText)
            .setDescription(description)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    )
}