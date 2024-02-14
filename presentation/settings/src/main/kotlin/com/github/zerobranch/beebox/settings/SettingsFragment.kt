package com.github.zerobranch.beebox.settings

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.addDividerDecoration
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainFragment
import com.github.zerobranch.beebox.settings.databinding.FragmentSettingsBinding
import com.github.zerobranch.beebox.settings.items.AppearanceItem
import com.github.zerobranch.beebox.settings.items.BackupItem
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseMainFragment(R.layout.fragment_settings) {
    @Inject
    internal lateinit var navProvider: SettingsNavProvider

    private val viewModel: SettingsViewModel by viewModels()
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvSettings }

    override fun initUi() {
        super.initUi()
        initItemsRv()
        initEdgeToEdgeMode()
        showNavigationBar()
    }

    override fun observeOnStates() {
        super.observeOnStates()
        observeOnUiState()
        observeOnActions()
    }

    private fun initItemsRv() = with(binding.rvSettings) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = itemsAdapter

        addDividerDecoration()

        itemsAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is BackupItem -> viewModel.onBackupClick()
                is AppearanceItem -> viewModel.onAppearanceClick()
            }
        }
    }

    private fun observeOnActions() {
        viewModel.actions
            .onEach { action ->
                when (action) {
                    is SettingsAction.GoToBackup -> navigate(navProvider.toBackup)
                }
            }
            .launchWhenCreated()
    }

    private fun observeOnUiState() {
        viewModel.uiState
            .onEach { items ->
                itemsAdapter.replaceAll(
                    items.map { item ->
                        when (item) {
                            UiItems.Appearance -> AppearanceItem()
                            UiItems.Backup -> BackupItem()
                        }
                    }
                )
            }
            .launchWhenCreated()
    }

    private fun initEdgeToEdgeMode() = with(binding) {
        toolbar.applyInsetter {
            type(statusBars = true) { margin(top = true) }
        }

        rvSettings.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}