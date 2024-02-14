package com.github.zerobranch.beebox.home.items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_app.utils.setPrimaryShadowColor
import com.github.zerobranch.beebox.home.R
import com.github.zerobranch.beebox.home.databinding.ItemRecentTrainingsBinding
import com.xwray.groupie.Item

class RecentTrainingsItem : BaseBindableItem<ItemRecentTrainingsBinding>() {

    override fun getLayout(): Int = R.layout.item_recent_trainings

    override fun initializeViewBinding(view: View) = ItemRecentTrainingsBinding.bind(view)

    override fun onViewBindingCreated(viewBinding: ItemRecentTrainingsBinding) = with(viewBinding) {
        super.onViewBindingCreated(viewBinding)
        root.setPrimaryShadowColor()
    }

    override fun bind(viewBinding: ItemRecentTrainingsBinding, position: Int) = with(viewBinding) {}

    override fun isSameAs(other: Item<*>): Boolean = other is RecentTrainingsItem

    override fun hasSameContentAs(other: Item<*>): Boolean = other is RecentTrainingsItem
}