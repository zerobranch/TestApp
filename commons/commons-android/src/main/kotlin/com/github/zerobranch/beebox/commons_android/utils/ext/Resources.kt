package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.res.Configuration
import android.content.res.Resources
import com.github.zerobranch.beebox.commons_android.R

val Resources.isLandscape: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

val Resources.isTablet: Boolean
    get() = getBoolean(R.bool.isTablet)