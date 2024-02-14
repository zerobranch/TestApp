package com.github.zerobranch.beebox.settings.items

import com.github.zerobranch.beebox.commons_app.R as CommonR

class AppearanceItem(
    override val title: Int = CommonR.string.common_title_appearance,
    override val startIcon: Int = CommonR.drawable.common_ic_appearance,
) : DefaultTextItem()