package com.github.zerobranch.beebox.commons_app.base

import android.os.Bundle
import androidx.fragment.app.DialogFragment

data class DialogNavCommand(
    val dialog: DialogFragment,
    var args: Bundle? = null
)