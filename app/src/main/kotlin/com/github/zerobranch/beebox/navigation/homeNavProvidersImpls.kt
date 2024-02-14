package com.github.zerobranch.beebox.navigation

import androidx.core.os.bundleOf
import com.github.zerobranch.beebox.R
import com.github.zerobranch.beebox.add_word_dialog.AddWordDialog
import com.github.zerobranch.beebox.auth.AuthActivity
import com.github.zerobranch.beebox.commons_app.base.ActivityNavCommand
import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.home.HomeNavProvider
import com.github.zerobranch.beebox.recent_trainings_dialog.RecentTrainingsNavProvider
import com.github.zerobranch.beebox.simple_dialog.SimpleDialog
import com.github.zerobranch.beebox.training.TrainingFragment
import com.github.zerobranch.beebox.training.TrainingNavProvider
import com.github.zerobranch.beebox.training_settings_dialog.TrainingSettingsDialog
import com.github.zerobranch.beebox.training_settings_dialog.TrainingSettingsNavProvider
import javax.inject.Inject

class HomeNavProviderImpl @Inject constructor() : HomeNavProvider {
    override val toRecentTrainingsDialog: NavCommand =
        NavCommand(R.id.action_home_screen_to_recent_trainings_dialog)

    override fun toTrainingSettingsDialog(trainingType: TrainingType): NavCommand =
        NavCommand(
            R.id.action_home_screen_to_training_settings_screen,
            bundleOf(
                TrainingSettingsDialog.TRAINING_TYPE_KEY to trainingType
            )
        )
}

class TrainingSettingsNavProviderImpl @Inject constructor() : TrainingSettingsNavProvider {
    override val toChooseCategoryScreen: NavCommand =
        NavCommand(R.id.action_training_settings_screen_to_choose_category_dialog_screen)

    override val toChooseWordTypeScreen: NavCommand =
        NavCommand(R.id.action_training_settings_screen_to_choose_type_dialog_screen)

    override fun toTrainingScreen(training: ProgressTraining): NavCommand =
        NavCommand(
            R.id.action_training_settings_screen_to_training_screen,
            bundleOf(TrainingFragment.PROGRESS_TRAINING_KEY to training)
        )
}

class TrainingNavProviderImpl @Inject constructor() : TrainingNavProvider {
    override val toEditWordScreen: NavCommand = NavCommand(
        R.id.action_training_screen_to_add_word_dialog,
        bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to true)
    )

    override val toLocalSearchScreen: NavCommand =
        NavCommand(R.id.action_add_word_dialog_to_local_search_dialog_screen)
}

class RecentTrainingsNavProviderImpl @Inject constructor() : RecentTrainingsNavProvider {
    override fun toTrainingScreen(training: ProgressTraining): NavCommand =
        NavCommand(
            R.id.action_recent_trainings_dialog_to_training_screen,
            bundleOf(TrainingFragment.PROGRESS_TRAINING_KEY to training)
        )

    override fun toDeleteDialog(
        resultKey: String,
        progressTraining: ProgressTraining,
        title: String,
        actionBtnText: String,
        cancelBtnText: String
    ): NavCommand = NavCommand(
        R.id.action_recent_trainings_dialog_to_simple_dialog_screen,
        bundleOf(
            SimpleDialog.RESULT_KEY to resultKey,
            SimpleDialog.TARGET_ITEM_KEY to progressTraining,
            SimpleDialog.TITLE_KEY to title,
            SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
            SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
        )
    )
}