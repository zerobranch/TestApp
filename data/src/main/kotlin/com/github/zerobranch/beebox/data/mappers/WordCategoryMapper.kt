package com.github.zerobranch.beebox.data.mappers

import com.github.zerobranch.beebox.data.entity.WordCategoryEntity
import com.github.zerobranch.beebox.domain.models.WordCategory

fun WordCategoryEntity.toDomain(): WordCategory =
    WordCategory(
        id = categoryId,
        name = name,
        description = description,
        createdDate = createdDate
    )