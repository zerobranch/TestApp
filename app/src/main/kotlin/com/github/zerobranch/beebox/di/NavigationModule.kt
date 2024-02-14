package com.github.zerobranch.beebox.di

import com.github.zerobranch.beebox.add_word_dialog.AddWordNavProvider
import com.github.zerobranch.beebox.auth.AuthNavProvider
import com.github.zerobranch.beebox.category_word_list_dialog.CategoryWordListNavProvider
import com.github.zerobranch.beebox.choose_category_dialog.ChooseCategoryNavProvider
import com.github.zerobranch.beebox.choose_type_dialog.ChooseTypeNavProvider
import com.github.zerobranch.beebox.home.HomeNavProvider
import com.github.zerobranch.beebox.lists.ListsNavProvider
import com.github.zerobranch.beebox.lists.page.category.CategoriesPageNavProvider
import com.github.zerobranch.beebox.lists.page.type.WordTypesPageNavProvider
import com.github.zerobranch.beebox.lists.page.word.WordPageNavProvider
import com.github.zerobranch.beebox.navigation.AddWordNavProviderImpl
import com.github.zerobranch.beebox.navigation.AuthNavProviderImpl
import com.github.zerobranch.beebox.navigation.CategoriesPageNavProviderImpl
import com.github.zerobranch.beebox.navigation.CategoryWordListNavProviderImpl
import com.github.zerobranch.beebox.navigation.ChooseCategoryNavProviderImpl
import com.github.zerobranch.beebox.navigation.ChooseTypeNavProviderImpl
import com.github.zerobranch.beebox.navigation.HomeNavProviderImpl
import com.github.zerobranch.beebox.navigation.ListsNavProviderImpl
import com.github.zerobranch.beebox.navigation.RecentTrainingsNavProviderImpl
import com.github.zerobranch.beebox.navigation.SearchNavProviderImpl
import com.github.zerobranch.beebox.navigation.SettingsNavProviderImpl
import com.github.zerobranch.beebox.navigation.TrainingNavProviderImpl
import com.github.zerobranch.beebox.navigation.TrainingSettingsNavProviderImpl
import com.github.zerobranch.beebox.navigation.TypeWordListNavProviderImpl
import com.github.zerobranch.beebox.navigation.WordGroupNavProviderImpl
import com.github.zerobranch.beebox.navigation.WordPageNavProviderImpl
import com.github.zerobranch.beebox.navigation.WordTypesPageNavProviderImpl
import com.github.zerobranch.beebox.recent_trainings_dialog.RecentTrainingsNavProvider
import com.github.zerobranch.beebox.search.SearchNavProvider
import com.github.zerobranch.beebox.settings.SettingsNavProvider
import com.github.zerobranch.beebox.training.TrainingNavProvider
import com.github.zerobranch.beebox.training_settings_dialog.TrainingSettingsNavProvider
import com.github.zerobranch.beebox.type_word_list_dialog.TypeWordListNavProvider
import com.github.zerobranch.beebox.word_groups_dialog.WordGroupNavProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {
    @Binds
    fun bindAuthNavProvider(impl: AuthNavProviderImpl): AuthNavProvider

    @Binds
    fun bindHomeNavProvider(impl: HomeNavProviderImpl): HomeNavProvider

    @Binds
    fun bindSettingsNavProvider(impl: SettingsNavProviderImpl): SettingsNavProvider

    @Binds
    fun bindListsNavProvider(impl: ListsNavProviderImpl): ListsNavProvider

    @Binds
    fun bindSearchNavProvider(impl: SearchNavProviderImpl): SearchNavProvider

    @Binds
    fun bindAddNavProvider(impl: AddWordNavProviderImpl): AddWordNavProvider

    @Binds
    fun bindWordGroupNavProvider(impl: WordGroupNavProviderImpl): WordGroupNavProvider

    @Binds
    fun bindChooseCategoryNavProvider(impl: ChooseCategoryNavProviderImpl): ChooseCategoryNavProvider

    @Binds
    fun bindChooseTypeNavProvider(impl: ChooseTypeNavProviderImpl): ChooseTypeNavProvider

    @Binds
    fun bindWordTypesPageNavProvider(impl: WordTypesPageNavProviderImpl): WordTypesPageNavProvider

    @Binds
    fun bindCategoriesPageNavProvider(impl: CategoriesPageNavProviderImpl): CategoriesPageNavProvider

    @Binds
    fun bindWordPageNavProvider(impl: WordPageNavProviderImpl): WordPageNavProvider

    @Binds
    fun bindTypeWordListNavProvider(impl: TypeWordListNavProviderImpl): TypeWordListNavProvider

    @Binds
    fun bindCategoryWordListNavProvider(impl: CategoryWordListNavProviderImpl): CategoryWordListNavProvider

    @Binds
    fun bindTrainingSettingsNavProvider(impl: TrainingSettingsNavProviderImpl): TrainingSettingsNavProvider

    @Binds
    fun bindTrainingNavProvider(impl: TrainingNavProviderImpl): TrainingNavProvider

    @Binds
    fun bindRecentTrainingsNavProvider(impl: RecentTrainingsNavProviderImpl): RecentTrainingsNavProvider
}