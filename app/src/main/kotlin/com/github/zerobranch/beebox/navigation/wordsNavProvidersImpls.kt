package com.github.zerobranch.beebox.navigation

import androidx.core.os.bundleOf
import com.github.zerobranch.beebox.R
import com.github.zerobranch.beebox.add_word_dialog.AddWordDialog
import com.github.zerobranch.beebox.add_word_dialog.AddWordNavProvider
import com.github.zerobranch.beebox.category_dialog.CategoryDialog
import com.github.zerobranch.beebox.category_word_list_dialog.CategoryWordListDialog
import com.github.zerobranch.beebox.category_word_list_dialog.CategoryWordListNavProvider
import com.github.zerobranch.beebox.choose_category_dialog.ChooseCategoryNavProvider
import com.github.zerobranch.beebox.choose_type_dialog.ChooseTypeNavProvider
import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.home.HomeNavProvider
import com.github.zerobranch.beebox.lists.ListsNavProvider
import com.github.zerobranch.beebox.lists.page.category.CategoriesPageNavProvider
import com.github.zerobranch.beebox.lists.page.type.WordTypesPageNavProvider
import com.github.zerobranch.beebox.lists.page.word.WordPageNavProvider
import com.github.zerobranch.beebox.search.SearchDialog
import com.github.zerobranch.beebox.search.SearchNavProvider
import com.github.zerobranch.beebox.settings.SettingsNavProvider
import com.github.zerobranch.beebox.simple_dialog.SimpleDialog
import com.github.zerobranch.beebox.type_word_list_dialog.TypeWordListDialog
import com.github.zerobranch.beebox.type_word_list_dialog.TypeWordListNavProvider
import com.github.zerobranch.beebox.word_groups_dialog.WordGroupDialog
import com.github.zerobranch.beebox.word_groups_dialog.WordGroupNavProvider
import com.github.zerobranch.beebox.word_type_dialog.WordTypeDialog
import javax.inject.Inject

class ListsNavProviderImpl @Inject constructor() : ListsNavProvider {
    override val toSearchScreen: NavCommand = NavCommand(R.id.action_lists_screen_to_search_dialog)

    override val toCreateCategoryDialog: NavCommand =
        NavCommand(R.id.action_lists_screen_to_category_dialog_screen)

    override val toCreateWordTypeDialog: NavCommand =
        NavCommand(R.id.action_lists_screen_to_word_type_dialog_screen)
}

class TypeWordListNavProviderImpl @Inject constructor() : TypeWordListNavProvider {
    override fun toDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_type_word_list_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toConfirmDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_type_word_list_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override val toEditWordScreen: NavCommand = NavCommand(
        R.id.action_type_word_list_screen_to_add_word_dialog,
        bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to true)
    )

    override fun toAddWordScreen(wordType: WordType): NavCommand = NavCommand(
        R.id.action_type_word_list_screen_to_search_dialog,
        bundleOf(SearchDialog.WORD_TYPE_KEY to wordType)
    )
}

class CategoryWordListNavProviderImpl @Inject constructor() : CategoryWordListNavProvider {
    override fun toDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_category_word_list_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toConfirmDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_category_word_list_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override val toEditWordScreen: NavCommand = NavCommand(
        R.id.action_category_word_list_screen_to_add_word_dialog,
        bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to true)
    )

    override fun toAddWordScreen(wordCategory: WordCategory): NavCommand = NavCommand(
        R.id.action_category_word_list_screen_to_search_dialog,
        bundleOf(SearchDialog.WORD_CATEGORY_KEY to wordCategory)
    )
}

class WordPageNavProviderImpl @Inject constructor() : WordPageNavProvider {
    override fun toDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toConfirmDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override val toEditWordScreen: NavCommand = NavCommand(
        R.id.action_lists_screen_to_add_word_dialog,
        bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to true)
    )
}

class WordTypesPageNavProviderImpl @Inject constructor() : WordTypesPageNavProvider {

    override fun toDeleteDialog(
        resultKey: String,
        wordType: WordType,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to wordType,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.DESCRIPTION_KEY to description,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toEditWordTypeDialog(wordType: WordType): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_word_type_dialog_screen,
            bundleOf(WordTypeDialog.WORD_TYPE_KEY to wordType)
        )

    override fun toTypeWordList(wordType: WordType): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_type_word_list_screen,
            bundleOf(TypeWordListDialog.WORD_TYPE_KEY to wordType)
        )
}

class CategoriesPageNavProviderImpl @Inject constructor() : CategoriesPageNavProvider {

    override fun toDeleteDialog(
        resultKey: String,
        wordCategory: WordCategory,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to wordCategory,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.DESCRIPTION_KEY to description,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toEditCategoryDialog(wordCategory: WordCategory): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_category_dialog_screen,
            bundleOf(CategoryDialog.WORD_CATEGORY_KEY to wordCategory)
        )

    override fun toCategoryWordList(wordCategory: WordCategory): NavCommand =
        NavCommand(
            R.id.action_lists_screen_to_category_word_list_screen,
            bundleOf(CategoryWordListDialog.WORD_CATEGORY_KEY to wordCategory)
        )
}

class AddWordNavProviderImpl @Inject constructor() : AddWordNavProvider {
    override val toChooseCategoryScreen: NavCommand =
        NavCommand(R.id.action_add_screen_to_choose_category_dialog_screen)

    override val toChooseWordTypeScreen: NavCommand =
        NavCommand(R.id.action_add_word_screen_to_choose_type_dialog_screen)

    override val toLocalSearchScreen: NavCommand =
        NavCommand(R.id.action_add_word_dialog_to_local_search_dialog_screen)
}

class WordGroupNavProviderImpl @Inject constructor() : WordGroupNavProvider {
    override val toChooseCategoryScreen: NavCommand =
        NavCommand(R.id.action_word_group_dialog_screen_to_choose_category_dialog_screen)

    override val toChooseWordTypeScreen: NavCommand =
        NavCommand(R.id.action_word_group_dialog_screen_to_choose_type_dialog_screen)

    override val toLocalSearchScreen: NavCommand =
        NavCommand(R.id.action_word_group_dialog_screen_to_local_search_dialog_screen2)

    override fun toDeleteDialog(
        resultKey: String,
        word: DraftWord,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_word_group_dialog_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to word,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toOpenExitDialog(
        resultKey: String,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand = NavCommand(
        R.id.action_word_group_dialog_screen_to_simple_dialog_screen,
        bundleOf(
            SimpleDialog.RESULT_KEY to resultKey,
            SimpleDialog.TITLE_KEY to title,
            SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
            SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
        )
    )
}

class ChooseCategoryNavProviderImpl @Inject constructor() : ChooseCategoryNavProvider {
    override fun toDeleteDialog(
        resultKey: String,
        wordCategory: WordCategory,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_choose_category_dialog_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to wordCategory,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.DESCRIPTION_KEY to description,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toEditCategoryDialog(wordCategory: WordCategory): NavCommand =
        NavCommand(
            R.id.action_choose_category_dialog_screen_to_category_dialog_screen,
            bundleOf(CategoryDialog.WORD_CATEGORY_KEY to wordCategory)
        )

    override val toCreateCategoryDialog: NavCommand =
        NavCommand(R.id.action_choose_category_dialog_screen_to_category_dialog_screen)
}

class ChooseTypeNavProviderImpl @Inject constructor() : ChooseTypeNavProvider {
    override fun toDeleteDialog(
        resultKey: String,
        wordType: WordType,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand =
        NavCommand(
            R.id.action_choose_type_dialog_screen_to_simple_dialog_screen,
            bundleOf(
                SimpleDialog.RESULT_KEY to resultKey,
                SimpleDialog.TARGET_ITEM_KEY to wordType,
                SimpleDialog.TITLE_KEY to title,
                SimpleDialog.DESCRIPTION_KEY to description,
                SimpleDialog.ACTION_BTN_TEXT_KEY to actionBtnText,
                SimpleDialog.CANCEL_BTN_TEXT_KEY to cancelBtnText,
            )
        )

    override fun toEditWordTypeDialog(wordType: WordType): NavCommand =
        NavCommand(
            R.id.action_choose_type_dialog_screen_to_word_type_dialog_screen,
            bundleOf(WordTypeDialog.WORD_TYPE_KEY to wordType)
        )

    override val toCreateWordTypeDialog: NavCommand =
        NavCommand(R.id.action_choose_type_dialog_screen_to_word_type_dialog_screen)
}

class SearchNavProviderImpl @Inject constructor() : SearchNavProvider {
    override val toAddWordScreen: NavCommand =
        NavCommand(
            R.id.action_search_dialog_to_add_screen,
            bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to false)
        )

    override val toEditWordScreen: NavCommand =
        NavCommand(
            R.id.action_search_dialog_to_add_screen,
            bundleOf(AddWordDialog.IS_EDIT_MODE_KEY to true)
        )

    override fun toWordGroupScreen(draftWord: List<DraftWord>): NavCommand =
        NavCommand(
            R.id.action_search_dialog_screen_to_word_group_dialog_screen,
            bundleOf(WordGroupDialog.DRAFT_WORDS_KEY to draftWord)
        )
}
