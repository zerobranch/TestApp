package com.github.zerobranch.beebox.word_type_dialog

import com.github.zerobranch.beebox.commons_android.utils.delegates.nullableArgs
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.safePostDelayed
import com.github.zerobranch.beebox.commons_android.utils.ext.showKeyboard
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainDialogFragment
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.word_type_dialog.databinding.DialogWordTypeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class WordTypeDialog : BaseMainDialogFragment(R.layout.dialog_word_type) {
    companion object {
        const val WORD_TYPE_KEY = "word_type_key"
    }

    private val binding by viewBinding(DialogWordTypeBinding::bind)
    private val wordType by nullableArgs<WordType>(WORD_TYPE_KEY)

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: WordTypeViewModel by lazy { factory.get(wordType) }

    private val isCreatingMode: Boolean
        get() = wordType == null

    override fun initUi() = with(binding) {
        etName.safePostDelayed(300) { etName.showKeyboard() }

        if (isCreatingMode) {
            tvTitle.text = getString(CommonR.string.common_title_create_word_type)
            tvApply.text = getString(CommonR.string.common_create)
            tvApply.setOnClickListener {
                viewModel.onCreateClick(
                    etName.text.toString().trim(),
                    etDescription.text.toString().trim(),
                )
            }
        } else {
            tvTitle.text = getString(CommonR.string.common_title_edit_word_type)
            tvApply.text = getString(CommonR.string.common_edit)

            val type = requireNotNull(wordType)
            etName.setText(type.name)
            etDescription.setText(type.description)

            tvApply.setOnClickListener {
                viewModel.onEditClick(
                    type.id,
                    etName.text.toString().trim(),
                    etDescription.text.toString().trim(),
                )
            }
        }
    }

    override fun observeOnStates() {
        viewModel.action.onEach { action ->
            when (action) {
                is WordTypeAction.Dismiss -> exit()
            }
        }.launchWhenCreated()
    }

    override fun onDispatchCancel() {
        super.onDispatchCancel()
        if (wordType.isNotNull()) {
            dialogDispatcher?.onDialogCancel(
                tag,
                BundleKeys.WORD_TYPE_DIALOG_RESULT_KEY,
                wordType
            )
        }
    }
}