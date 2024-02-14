package com.github.zerobranch.beebox.settings.items

import com.github.zerobranch.beebox.commons_app.R as CommonR

class BackupItem(
    override val title: Int = CommonR.string.common_backup,
    override val startIcon: Int = CommonR.drawable.common_ic_backup,
) : DefaultTextItem()