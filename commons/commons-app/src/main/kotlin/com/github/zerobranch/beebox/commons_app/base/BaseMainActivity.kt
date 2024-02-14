package com.github.zerobranch.beebox.commons_app.base

import android.content.Intent
import com.github.zerobranch.beebox.commons_android.base.BaseActivity
import com.github.zerobranch.beebox.logging.info

abstract class BaseMainActivity : BaseActivity() {
    private companion object {
        const val TAG = "ActivityLifecycle"
    }

    override val logger: (String) -> Unit
        get() = { msg -> javaClass.info(TAG, msg) }

    protected open fun navigate(
        navCommand: ActivityNavCommand,
        withFinish: Boolean = false
    ) {
        startActivity(
            Intent(this, navCommand.cls)
                .apply {
                    navCommand.args?.let { args -> putExtras(args) }
                }
        )

        if (withFinish) {
            finish()
        }
    }

    protected open fun navigate(navCommand: DialogNavCommand) {
        val dialog = navCommand.dialog

        dialog.arguments = navCommand.args
        dialog.show(supportFragmentManager, dialog::class.qualifiedName)
    }
}