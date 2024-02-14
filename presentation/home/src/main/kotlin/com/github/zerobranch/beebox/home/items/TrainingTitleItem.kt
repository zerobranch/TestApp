package com.github.zerobranch.beebox.home.items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_app.utils.setPrimaryShadowColor
import com.github.zerobranch.beebox.commons_app.utils.title
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.home.R
import com.github.zerobranch.beebox.home.databinding.ItemTrainingTitleBinding
import com.xwray.groupie.Item

class TrainingTitleItem(
    val trainingType: TrainingType,
) : BaseBindableItem<ItemTrainingTitleBinding>() {

    override fun getLayout(): Int = R.layout.item_training_title

    override fun initializeViewBinding(view: View) = ItemTrainingTitleBinding.bind(view)

    override fun onViewBindingCreated(viewBinding: ItemTrainingTitleBinding) = with(viewBinding) {
        super.onViewBindingCreated(viewBinding)
        root.setPrimaryShadowColor()
    }

    override fun bind(viewBinding: ItemTrainingTitleBinding, position: Int) = with(viewBinding) {
        title.setText(trainingType.title)
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is TrainingTitleItem && other.trainingType == trainingType

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is TrainingTitleItem && other.trainingType == trainingType
}