package com.github.zerobranch.beebox.commons_app.base

import android.os.Bundle

data class ActivityNavCommand(
    val cls: Class<*>,
    var args: Bundle? = null
)