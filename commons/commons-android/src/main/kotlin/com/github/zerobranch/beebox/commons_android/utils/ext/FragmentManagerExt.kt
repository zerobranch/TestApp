package com.github.zerobranch.beebox.commons_android.utils.ext

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.tryDismissDialog(tag: String?) {
    (findFragmentByTag(tag) as? DialogFragment)?.dismiss()
}