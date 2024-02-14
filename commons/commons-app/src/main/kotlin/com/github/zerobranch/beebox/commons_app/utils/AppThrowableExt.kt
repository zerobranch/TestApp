package com.github.zerobranch.beebox.commons_app.utils

import com.github.zerobranch.beebox.domain.models.exceptions.NoInternetException

val Throwable.isNoInternet: Boolean
    get() = this is NoInternetException
