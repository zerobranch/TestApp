package com.github.zerobranch.beebox.recent_trainings_dialog

import android.annotation.SuppressLint
import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_app.utils.title
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.recent_trainings_dialog.databinding.ItemRecentTrainingBinding
import com.xwray.groupie.Item

class RecentTrainingItem(
    val training: ProgressTraining,
) : BaseBindableItem<ItemRecentTrainingBinding>() {

    override fun getLayout(): Int = R.layout.item_recent_training

    override fun initializeViewBinding(view: View) = ItemRecentTrainingBinding.bind(view)

    @SuppressLint("SetTextI18n")
    override fun bind(viewBinding: ItemRecentTrainingBinding, position: Int) = with(viewBinding) {
        val trainingTypeTitle = getString(training.mainTrainingType.title)
        val date = training.createdDate.stringWithFormat("dd MMM HH:mm")
        title.text = "$trainingTypeTitle $date"
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is RecentTrainingItem && other.training == training

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is RecentTrainingItem && other.training == training
}