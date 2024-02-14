package com.github.zerobranch.beebox.commons_android.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@Deprecated(message = "Don't override", level = DeprecationLevel.WARNING)
abstract class BaseActivity : AppCompatActivity() {
    protected open val logger: ((msg: String) -> Unit)
        get() = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.invoke("onCreate")
    }

    override fun onStart() {
        super.onStart()
        logger.invoke("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.invoke("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.invoke("onPause")
    }

    override fun onStop() {
        super.onStop()
        logger.invoke("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.invoke("onDestroy")
    }

    protected fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    protected fun wasRecreated(savedInstanceState: Bundle?) = savedInstanceState != null
}