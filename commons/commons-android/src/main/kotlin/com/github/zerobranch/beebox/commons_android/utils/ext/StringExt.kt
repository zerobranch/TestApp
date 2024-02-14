package com.github.zerobranch.beebox.commons_android.utils.ext

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun String.fromHtml(): Spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
