package com.github.zerobranch.beebox.settings.items

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.drawable
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_android.utils.ext.setDrawables
import com.github.zerobranch.beebox.settings.R
import com.github.zerobranch.beebox.settings.databinding.ItemDefaultTextBinding
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

abstract class DefaultTextItem : BaseBindableItem<ItemDefaultTextBinding>() {
    @get:StringRes abstract val title: Int
    @get:DrawableRes abstract val startIcon: Int
    @get:DrawableRes open val endIcon: Int? = CommonR.drawable.common_ic_right_arrow

    override fun getLayout() = R.layout.item_default_text

    override fun initializeViewBinding(view: View) = ItemDefaultTextBinding.bind(view)

    override fun bind(viewBinding: ItemDefaultTextBinding, position: Int) =
        with(viewBinding.tvItem) {
            text = getString(title)

            setDrawables(
                left = drawable(startIcon),
                right = endIcon?.let{ drawable(it) }
            )
        }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DefaultTextItem && title == other.title

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DefaultTextItem && title == other.title
}
