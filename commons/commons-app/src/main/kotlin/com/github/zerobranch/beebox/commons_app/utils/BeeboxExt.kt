package com.github.zerobranch.beebox.commons_app.utils

import android.net.Uri
import androidx.fragment.app.Fragment
import com.github.zerobranch.beebox.commons_android.utils.delegates.ThemeDelegate
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.openInCustomTabs
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.domain.models.ThemeType
import com.github.zerobranch.beebox.domain.models.TrainingType

context(Fragment)
fun openWordInBrowser(url: String) =
    context?.openInCustomTabs(
        url = Uri.parse(url),
        toolbarColor = colorInt(R.color.common_bg_primary),
        enterResId = R.anim.z_axis_enter,
        exitResId = R.anim.z_axis_exit,
        enterPopResId = R.anim.z_axis_enter_pop,
        exitPopResId = R.anim.z_axis_exit_pop
    )

fun parseThemeType(value: Int): ThemeType =
    when (value) {
        ThemeDelegate.LIGHT -> ThemeType.LIGHT
        ThemeDelegate.NIGHT -> ThemeType.NIGHT
        ThemeDelegate.FOLLOW_SYSTEM -> ThemeType.FOLLOW_SYSTEM
        ThemeDelegate.AUTO_BATTERY -> ThemeType.AUTO_BATTERY
        else -> ThemeType.FOLLOW_SYSTEM
    }

val ThemeType.value: Int
    get() = when (this) {
        ThemeType.LIGHT -> ThemeDelegate.LIGHT
        ThemeType.NIGHT -> ThemeDelegate.NIGHT
        ThemeType.FOLLOW_SYSTEM -> ThemeDelegate.FOLLOW_SYSTEM
        ThemeType.AUTO_BATTERY -> ThemeDelegate.AUTO_BATTERY
    }

val TrainingType.title: Int
    get() = when (this) {
        TrainingType.AURALLY -> R.string.common_training_aurally_title
        TrainingType.IN_ENGLISH -> R.string.common_training_in_english_title
        TrainingType.IN_RUSSIAN -> R.string.common_training_in_russian_title
        TrainingType.IN_ENGLISH_AND_RUSSIAN -> R.string.common_training_in_english_and_russian_title
        TrainingType.ENTER_WORD -> R.string.common_training_enter_word_title
    }
