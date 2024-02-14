package com.github.zerobranch.beebox.lists

import androidx.fragment.app.viewModels
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.ext.addTabSelectedListener
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.supportImageTint
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainFragment
import com.github.zerobranch.beebox.commons_app.utils.setPrimaryShadowColor
import com.github.zerobranch.beebox.lists.databinding.FragmentListsBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class ListsFragment : BaseMainFragment(R.layout.fragment_lists), DialogDispatcher {
    @Inject
    internal lateinit var navProvider: ListsNavProvider

    private val viewModel: ListsViewModel by viewModels()
    private val binding by viewBinding(FragmentListsBinding::bind)
    private var listsAdapter: ListsAdapter? = null

    private val pageDialogDispatcher: DialogDispatcher?
        get() = childFragmentManager.fragments.getOrNull(binding.vpItems.currentItem) as? DialogDispatcher

    override fun initUi() {
        super.initUi()
        initEdgeToEdgeMode()
        initViewPager()

        binding.fabAdd.setOnClickListener { viewModel.onAddClick(binding.vpItems.currentItem) }
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    ListsAction.OpenSearchWord -> navigate(navProvider.toSearchScreen)
                    ListsAction.OpenAddCategory -> navigate(navProvider.toCreateCategoryDialog)
                    ListsAction.OpenAddWordType -> navigate(navProvider.toCreateWordTypeDialog)
                }
            }
            .launchWhenCreated()
    }

    override fun onDialogResume(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogResume(tag, resultKey, data)
        pageDialogDispatcher?.onDialogResume(tag, resultKey, data)
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)
        pageDialogDispatcher?.onDialogAction(tag, resultKey, data)
    }

    override fun onDialogNeural(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogNeural(tag, resultKey, data)
        pageDialogDispatcher?.onDialogNeural(tag, resultKey, data)
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        pageDialogDispatcher?.onDialogCancel(tag, resultKey, data)
    }

    override fun onDialogDismiss(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogDismiss(tag, resultKey, data)
        pageDialogDispatcher?.onDialogDismiss(tag, resultKey, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listsAdapter = null
        binding.vpItems.adapter = null
    }

    private fun initViewPager() = with(binding) {
        listsAdapter = ListsAdapter(this@ListsFragment)
        vpItems.adapter = listsAdapter
        vpItems.isUserInputEnabled = false
        vpItems.offscreenPageLimit = 2

        TabLayoutMediator(tlItems, vpItems) { tab, position ->
            tab.text = getRubricTitle(position)
        }.attach()

        tlItems.addTabSelectedListener { position ->
            val pageIcon = when (position) {
                0 -> CommonR.drawable.common_ic_cyclone
                1 -> CommonR.drawable.common_ic_category
                else -> CommonR.drawable.common_ic_type
            }

            val pageColor = when (position) {
                    0 -> CommonR.color.common_beebox_blue
                    1 -> CommonR.color.common_fuchsia_dark
                    else -> CommonR.color.common_dark_yellow
            }

            fabAdd.setImageResource(pageIcon)
            fabAdd.setPrimaryShadowColor(pageColor)
            fabAdd.supportImageTint(pageColor)
        }
    }

    private fun getRubricTitle(position: Int): CharSequence =
        getString(
            when (position) {
                0 -> CommonR.string.common_title_words
                1 -> CommonR.string.common_title_categories
                else -> CommonR.string.common_title_types
            }
        )

    private fun initEdgeToEdgeMode() {
        binding.tlItems.applyInsetter {
            type(statusBars = true) { margin(top = true) }
        }
    }
}

