package com.github.zerobranch.beebox.commons_app.selecting

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.commons_app.databinding.CommonDialogMultiSelectItemBinding
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.toArrayList
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.xwray.groupie.GroupieAdapter
import dev.chrisbanes.insetter.applyInsetter

class MultiSelectItemDialog : BaseMainBottomSheetDialog(R.layout.common_dialog_multi_select_item) {
    companion object {
        const val TAG = "MultiSelectItem"
        const val RESULT_REQUEST_KEY = "MultiSelectedItems"

        fun instance(
            title: String,
            itemBundleKey: String,
            selectedItemsPositions: Set<Int>,
            items: List<Any>
        ) = MultiSelectItemDialog().apply {
            this.title = title
            this.itemBundleKey = itemBundleKey
            this.defaultSelectedPositions = selectedItemsPositions
            this.items = items.map { it.toString() }
        }
    }

    private var title: String by args()
    private var itemBundleKey: String by args()
    private var defaultSelectedPositions: Set<Int> by args()
    private var items: List<String> by args()

    private val binding by viewBinding(CommonDialogMultiSelectItemBinding::bind)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvItems }

    private val selectedItemsPositions = mutableSetOf<Int>()

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
            bundleOf(itemBundleKey to selectedItemsPositions.toArrayList())
        )
    }

    private fun initItems() {
        selectedItemsPositions.addAll(defaultSelectedPositions)

        with(binding.rvItems) {
            updatePadding(bottom = dialogTopOffset + paddingBottom)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }

        updateItems()
    }

    private fun updateItems() {
        itemsAdapter.updateAsync(
            items.mapIndexed { index, item ->
                SelectableItem(item, index in selectedItemsPositions) { isSelected ->
                    if (isSelected) {
                        selectedItemsPositions.remove(index)
                    } else {
                        selectedItemsPositions.add(index)
                    }

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
