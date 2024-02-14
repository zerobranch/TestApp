package com.github.zerobranch.beebox.category_dialog

import com.github.zerobranch.beebox.category_dialog.databinding.DialogCategoryBinding
import com.github.zerobranch.beebox.commons_android.utils.delegates.nullableArgs
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.safePostDelayed
import com.github.zerobranch.beebox.commons_android.utils.ext.showKeyboard
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainDialogFragment
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.WordCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class CategoryDialog : BaseMainDialogFragment(R.layout.dialog_category) {
    companion object {
        const val WORD_CATEGORY_KEY = "word_category_key"
    }

    private val binding by viewBinding(DialogCategoryBinding::bind)
    private val wordCategory by nullableArgs<WordCategory>(WORD_CATEGORY_KEY)

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: CategoryViewModel by lazy { factory.get(wordCategory) }

    private val isCreatingMode: Boolean
        get() = wordCategory == null

    override fun initUi(): Unit = with(binding) {
        etName.safePostDelayed(300) { etName.showKeyboard() }

        if (isCreatingMode) {
            tvTitle.text = getString(CommonR.string.common_title_create_category)
            tvApply.text = getString(CommonR.string.common_create)
            tvApply.setOnClickListener {
                viewModel.onCreateClick(
                    etName.text.toString().trim(),
                    etDescription.text.toString().trim(),
                )
            }
        } else {
            tvTitle.text = getString(CommonR.string.common_title_edit_category)
            tvApply.text = getString(CommonR.string.common_edit)

            val category = requireNotNull(wordCategory)
            etName.setText(category.name)
            etDescription.setText(category.description)

            tvApply.setOnClickListener {
                viewModel.onEditClick(
                    category.id,
                    etName.text.toString().trim(),
                    etDescription.text.toString().trim(),
                )
            }
        }
    }

    override fun onDispatchCancel() {
        if (wordCategory.isNotNull()) {
            dialogDispatcher?.onDialogCancel(
                tag,
                BundleKeys.CATEGORY_DIALOG_RESULT_KEY,
                wordCategory
            )
        }
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is CategoryAction.Dismiss -> exit()
                }
            }
            .launchWhenCreated()
    }
}