package com.github.zerobranch.beebox.commons_app.selecting

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.commons_app.databinding.CommonDialogSingleSelectItemBinding
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.xwray.groupie.GroupieAdapter
import dev.chrisbanes.insetter.applyInsetter

class SingleSelectItemDialog : com.github.zerobranch.beebox.commons_android.base.BaseBottomSheetDialog(R.layout.common_dialog_single_select_item) {
    companion object {
        const val TAG = "SingleSelectItem"
        const val RESULT_REQUEST_KEY = "SelectedItem"

        fun instance(
            title: String,
            itemBundleKey: String,
            selectedItemPosition: Int?,
            items: List<Any>
        ) = SingleSelectItemDialog().apply {
            this.title = title
            this.itemBundleKey = itemBundleKey
            this.selectedItemPosition = selectedItemPosition ?: -1
            this.items = items.map { it.toString() }
        }
    }

    private var title: String by args()
    private var itemBundleKey: String by args()
    private var selectedItemPosition: Int by args()
    private var items: List<String> by args()
    private var currentSelectedItemPosition: Int = -1

    private val binding by viewBinding(CommonDialogSingleSelectItemBinding::bind)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvItems }

    override fun getTheme(): Int = R.style.CommonBaseBottomSheetDialogTheme_Large

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initItems()
        initEdgeToEdgeMode()

        binding.tvTitle.text = title
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        setFragmentResult(
            RESULT_REQUEST_KEY,
            bundleOf(itemBundleKey to currentSelectedItemPosition)
        )
    }

    private fun initItems() {
        with(binding.rvItems) {
            currentSelectedItemPosition = selectedItemPosition

            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            adapter = itemsAdapter
            manager.scrollToPositionWithOffset(currentSelectedItemPosition, 40.dp)
        }

        updateItems()
    }

    private fun updateItems() {
        itemsAdapter.updateAsync(
            items.mapIndexed { index, item ->
                SelectableItem(item, index == currentSelectedItemPosition) {
                    currentSelectedItemPosition = index
                    updateItems()
                }
            }
        )
    }

    private fun initEdgeToEdgeMode() = with(binding) {
        rvItems.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}
