package com.github.zerobranch.beebox.training_settings_dialog

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.size
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.ext.context
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.utils.title
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.training_settings_dialog.databinding.DialogTrainingSettingsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR
import com.github.zerobranch.beebox.commons_items.R as CommonItemsR

@AndroidEntryPoint
class TrainingSettingsDialog : BaseMainBottomSheetDialog(R.layout.dialog_training_settings) {
    companion object {
        const val TRAINING_TYPE_KEY = "training_type_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = false

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: TrainingSettingsNavProvider

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: TrainingSettingsViewModel by lazy { factory.get(trainingType) }

    private val binding by viewBinding(DialogTrainingSettingsBinding::bind)
    private val trainingType by args<TrainingType>(TRAINING_TYPE_KEY)

    override fun initUi() = with(binding) {
        initEdgeToEdgeMode()

        tvTitle.setText(trainingType.title)
        ibAddCategory.setOnClickListener { viewModel.onAddWordCategoryClick() }
        ibAddWordType.setOnClickListener { viewModel.onAddWordTypeClick() }
        ibClose.setOnClickListener { viewModel.onCloseClick() }
        btnLaunch.setOnClickListener { viewModel.onLaunchClick() }
    }

    override fun observeOnStates() {
        viewModel.uiState.onEach { state ->
            setWordTypes(state.wordTypes)
            setCategories(state.wordCategories)

            binding.btnLaunch.isEnabled =
                state.wordTypes.isNotEmpty() || state.wordCategories.isNotEmpty()
        }.launchWhenCreated()

        viewModel.action.onEach { action ->
            when (action) {
                TrainingSettingsAction.Exit -> exit()
                TrainingSettingsAction.GoToChooseCategory -> navigate(navProvider.toChooseCategoryScreen)
                TrainingSettingsAction.GoToChooseWordType -> navigate(navProvider.toChooseWordTypeScreen)
                is TrainingSettingsAction.GoToTraining -> navigate(navProvider.toTrainingScreen(action.training))
            }
        }.launchWhenCreated()
    }

    private fun setWordTypes(wordTypes: List<WordType>) = with(binding) {
        val inflater = LayoutInflater.from(context)
        chipGroupWordType.removeViews(0, chipGroupWordType.size - 1)
        wordTypes.forEach { type ->
            chipGroupWordType.addChip(
                inflater = inflater,
                text = type.name,
                closeIconClickListener = { viewModel.onWordTypeDeleteClick(type) },
            )
        }
    }

    private fun setCategories(wordCategories: List<WordCategory>) = with(binding) {
        val inflater = LayoutInflater.from(context)
        chipGroupCategory.removeViews(0, chipGroupCategory.size - 1)
        wordCategories.forEach { category ->
            chipGroupCategory.addChip(
                inflater = inflater,
                text = category.name,
                closeIconClickListener = { viewModel.onCategoryDeleteClick(category) },
            )
        }
    }

    private fun ChipGroup.addChip(
        inflater: LayoutInflater,
        text: String?,
        closeIconClickListener: View.OnClickListener,
    ) {
        val chip = inflater.inflate(CommonItemsR.layout.item_chip, this, false) as Chip
        chip.text = text
        chip.setOnCloseIconClickListener(closeIconClickListener)
        addView(chip, childCount - 1)
    }

    private fun initEdgeToEdgeMode() {
        binding.root.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}