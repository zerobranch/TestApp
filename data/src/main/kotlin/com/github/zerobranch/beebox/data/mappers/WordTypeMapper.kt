package com.github.zerobranch.beebox.data.mappers

import com.github.zerobranch.beebox.data.entity.WordTypeEntity
import com.github.zerobranch.beebox.domain.models.WordType

fun WordTypeEntity.toDomain(): WordType =
    WordType(
        id = typeId,
        name = name,
        description = description,
        createdDate = createdDate
    )