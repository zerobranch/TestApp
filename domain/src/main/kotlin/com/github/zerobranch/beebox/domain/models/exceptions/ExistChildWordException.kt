package com.github.zerobranch.beebox.domain.models.exceptions

import java.io.IOException

open class ExistChildWordException : IOException() {
    override val message: String?
        get() = "Exist child word"
}