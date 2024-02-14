package com.github.zerobranch.beebox.auth

import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.github.zerobranch.beebox.auth.databinding.ActivityAuthBinding
import com.github.zerobranch.beebox.commons_android.utils.ext.checkBiometricAuth
import com.github.zerobranch.beebox.commons_android.utils.ext.getBiometricSettingsIntent
import com.github.zerobranch.beebox.commons_android.utils.ext.imageTint
import com.github.zerobranch.beebox.commons_android.utils.ext.launchStrongBiometricAuth
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.softPostDelayed
import com.github.zerobranch.beebox.commons_app.base.BaseMainActivity
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class AuthActivity : BaseMainActivity() {
    @Inject
    internal lateinit var navProvider: AuthNavProvider

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        launchBiometricAuth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeOnStates()
        initListeners()

        val uri = intent.data
        if (uri.isNotNull()) {
            viewModel.onCreate(uri)
        }
    }

    private fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is AuthAction.GoToMain -> {
                        navigate(navProvider.toMain(action.backupInfo), withFinish = true)
                    }
                    AuthAction.LaunchBiometricAuth -> launchBiometricFlow()
                    AuthAction.Error -> {}
                    AuthAction.Success -> {}
                    AuthAction.Reset -> {}
                }
            }
            .launchWhenCreated()
    }

    private fun initListeners() = with(binding) {
        tvLaunchApp.setOnClickListener { viewModel.onLaunchAppClick() }
        tvFingerprintTest.setOnClickListener { viewModel.onFingerprintClick() }
    }

    private fun launchBiometricFlow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return
        }

        checkBiometricAuth(
            onBiometricSuccess = { launchBiometricAuth() },
            onBiometricNonEnrolled = { resultLauncher.launch(getBiometricSettingsIntent()) },
            onBiometricError = {},
        )
    }

    private fun launchBiometricAuth() {
        launchStrongBiometricAuth(
            title = getString(CommonR.string.common_login_to_biometrics),
            negativeButtonText = getString(CommonR.string.common_cancel),
            description = getString(CommonR.string.common_scan_your_fingerprint),
            onCancelClick = { },
            onAuthenticationSucceeded = { viewModel.onBiometricSuccess() }
        )
    }
}