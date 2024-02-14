package com.github.zerobranch.beebox.simple_dialog

import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.nullableArgs
import com.github.zerobranch.beebox.commons_android.utils.ext.setTextOrHide
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainDialogFragment
import com.github.zerobranch.beebox.simple_dialog.databinding.DialogSimpleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class SimpleDialog : BaseMainDialogFragment(R.layout.dialog_simple) {
    companion object {
        const val RESULT_KEY = "result_key"
        const val TARGET_ITEM_KEY = "target_item_key"
        const val DESCRIPTION_KEY = "description_key"
        const val TITLE_KEY = "title_key"

        const val ACTION_BTN_TEXT_KEY = "action_btn_text_key"
        const val CANCEL_BTN_TEXT_KEY = "cancel_btn_text_key"
        const val NEURAL_BTN_TEXT_KEY = "neural_btn_text_key"
    }

    private val resultKey by args<String>(RESULT_KEY)
    private val binding by viewBinding(DialogSimpleBinding::bind)
    private val target by nullableArgs<Serializable>(TARGET_ITEM_KEY)
    private val title by nullableArgs<String>(TITLE_KEY)
    private val description by nullableArgs<String>(DESCRIPTION_KEY)
    private val actionBtnText by nullableArgs<String>(ACTION_BTN_TEXT_KEY)
    private val cancelBtnText by nullableArgs<String>(CANCEL_BTN_TEXT_KEY)
    private val neuralBtnText by nullableArgs<String>(NEURAL_BTN_TEXT_KEY)

    override fun initUi() = with(binding) {
        tvTitle.setTextOrHide(title)
        tvDescription.setTextOrHide(description)
        tvCancel.setTextOrHide(cancelBtnText)
        tvAction.setTextOrHide(actionBtnText)
        tvNeural.setTextOrHide(neuralBtnText)

        tvAction.setOnClickListener {
            dialogDispatcher?.onDialogAction(tag, resultKey, target)
            exit()
        }

        tvCancel.setOnClickListener {
            dialogDispatcher?.onDialogCancel(tag, resultKey, target)
            exit()
        }

        tvNeural.setOnClickListener {
            dialogDispatcher?.onDialogNeural(tag, resultKey, target)
            exit()
        }
    }

    override fun onDispatchCancel() {
        super.onDispatchCancel()
        dialogDispatcher?.onDialogCancel(tag, resultKey, target)
    }
}