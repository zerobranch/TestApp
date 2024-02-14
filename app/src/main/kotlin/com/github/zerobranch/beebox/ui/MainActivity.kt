package com.github.zerobranch.beebox.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.zerobranch.beebox.R
import com.github.zerobranch.beebox.backup.BackupFragment
import com.github.zerobranch.beebox.commons_android.utils.ext.enableSecureScreen
import com.github.zerobranch.beebox.commons_android.utils.ext.fadeIn
import com.github.zerobranch.beebox.commons_android.utils.ext.fadeOut
import com.github.zerobranch.beebox.commons_app.base.BaseMainActivity
import com.github.zerobranch.beebox.commons_app.base.Paggable
import com.github.zerobranch.beebox.commons_app.utils.setPrimaryShadowColor
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseMainActivity(), Paggable {
    companion object {
        const val DB_BACKUP_KEY = BackupFragment.DB_BACKUP_KEY
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableSecureScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        initDeepLink()
    }

    override fun showNavigationBar(isSmooth: Boolean) {
        if (isSmooth) {
            binding.bottomNavigation.fadeIn { binding.bottomNavigation.isVisible = true }
        } else {
            binding.bottomNavigation.isVisible = true
        }
    }

    override fun hideNavigationBar(isSmooth: Boolean) {
        if (isSmooth) {
            binding.bottomNavigation.fadeOut { binding.bottomNavigation.isVisible = false }
        } else {
            binding.bottomNavigation.isVisible = false
        }
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        binding.bottomNavigation.setPrimaryShadowColor()
    }

    @Suppress("DEPRECATION")
    private fun initDeepLink() {
        if (intent.extras?.getSerializable(DB_BACKUP_KEY).isNotNull()) {
            NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.backup_screen)
                .setArguments(intent.extras)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
                .send()
        }
    }
}
