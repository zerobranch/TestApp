package com.github.zerobranch.beebox.commons_android.base

interface DialogDispatcher {
    fun onDialogAction(tag: String? = null, resultKey: String? = null, data: Any? = null) {}
    fun onDialogNeural(tag: String? = null, resultKey: String? = null, data: Any? = null) {}
    fun onDialogResume(tag: String? = null, resultKey: String? = null, data: Any? = null) {}
    fun onDialogDismiss(tag: String? = null, resultKey: String? = null, data: Any? = null) {}
    fun onDialogCancel(tag: String? = null, resultKey: String? = null, data: Any? = null) {}
}