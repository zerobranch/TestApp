package com.github.zerobranch.beebox.commons_app.utils

import android.os.Build
import android.view.View
import androidx.annotation.ColorRes
import com.github.zerobranch.beebox.commons_android.utils.delegates.ThemeDelegate
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_app.R
import com.google.android.material.card.MaterialCardView

fun View.setPrimaryShadowColor(
    @ColorRes color: Int = R.color.common_primary,
    isOnlyLight: Boolean = true
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (isOnlyLight && !ThemeDelegate.isLight(resources)) {
            if (this is MaterialCardView) {
                cardElevation = 0f
            }

            elevation = 0f
            return
        }

        outlineAmbientShadowColor = colorInt(color)
        outlineSpotShadowColor = colorInt(color)
    }
}