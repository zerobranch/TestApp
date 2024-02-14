package com.github.zerobranch.beebox.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class WordFull(
    @Embedded
    val word: WordEntity,

    @Relation(
        parentColumn = "word",
        entityColumn = "categoryId",
        associateBy = Junction(WordCategoryCrossRefEntity::class)
    )
    val wordCategories: List<WordCategoryEntity>,

    @Relation(
        parentColumn = "word",
        entityColumn = "typeId",
        associateBy = Junction(WordTypeCrossRefEntity::class)
    )
    val wordTypes: List<WordTypeEntity>
)
