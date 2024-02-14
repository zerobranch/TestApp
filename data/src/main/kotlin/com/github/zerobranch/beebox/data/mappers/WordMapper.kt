package com.github.zerobranch.beebox.data.mappers

import com.github.zerobranch.beebox.data.entity.HintEntity
import com.github.zerobranch.beebox.data.entity.TranscriptionEntity
import com.github.zerobranch.beebox.data.entity.WordEntity
import com.github.zerobranch.beebox.data.entity.WordFull
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Hint
import com.github.zerobranch.beebox.domain.models.words.Transcription
import com.github.zerobranch.beebox.domain.models.words.Word
import com.squareup.moshi.Moshi
import org.threeten.bp.ZonedDateTime

fun WordFull.toDomain(moshi: Moshi, typeCount: Int, categoriesCount: Int): Word =
    Word(
        word = word.word,
        transcriptions = word.transcriptions.map { it.toDomain() },
        translates = word.translate,
        comment = word.comment,
        rank = word.rank,
        hints = word.hints.map { it.toDomain() },
        parentWord = word.parentWord?.toDomain(moshi, 0, 0),
        createdDate = word.createdDate,
        categories = wordCategories.map { category ->
            WordCategory(
                id = category.categoryId,
                name = category.name,
                description = category.description,
                createdDate = category.createdDate
            )
        },
        wordTypes = wordTypes.map { type ->
            WordType(
                id = type.typeId,
                name = type.name,
                description = type.description,
                createdDate = type.createdDate
            )
        },
        typesCount = typeCount,
        categoriesCount = categoriesCount
    )

fun HintEntity.toDomain(): Hint = Hint(id = id, text = text)

fun TranscriptionEntity.toDomain(): Transcription =
    Transcription(
        id = id,
        title = title,
        firstTranscriptionHint = firstTranscriptionHint,
        firstTranscription = firstTranscription,
        secondTranscriptionHint = secondTranscriptionHint,
        secondTranscription = secondTranscription,
        firstTranscriptionAudio = firstTranscriptionAudio,
        secondTranscriptionAudio = secondTranscriptionAudio,
    )

fun DraftWord.toEntity(createdDate: ZonedDateTime? = null) = WordEntity(
    word = word,
    transcriptions = transcriptions.map { it.toEntity() },
    translate = translates,
    comment = comment,
    rank = rank,
    hints = hints.map { it.toEntity() },
    createdDate = createdDate ?: ZonedDateTime.now(),
    parentWordId = parentWord?.word
)

fun Hint.toEntity() = HintEntity(
    id = id,
    text = text,
)

fun Transcription.toEntity() = TranscriptionEntity(
    id = id,
    title = title,
    firstTranscriptionHint = firstTranscriptionHint,
    firstTranscription = firstTranscription,
    secondTranscriptionHint = secondTranscriptionHint,
    secondTranscription = secondTranscription,
    firstTranscriptionAudio = firstTranscriptionAudio,
    secondTranscriptionAudio = secondTranscriptionAudio,
)

fun Word.toDraft(): DraftWord =
    DraftWord(
        word = word,
        parentWord = parentWord,
        transcriptions = transcriptions,
        translates = translates,
        comment = comment,
        rank = rank,
        hints = hints,
        categories = categories,
        wordTypes = wordTypes,
    )
