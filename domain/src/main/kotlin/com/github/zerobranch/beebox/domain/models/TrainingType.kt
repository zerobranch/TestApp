package com.github.zerobranch.beebox.domain.models

enum class TrainingType(val id: Int) {
    IN_ENGLISH(0),
    IN_RUSSIAN(1),
    IN_ENGLISH_AND_RUSSIAN(2),
    AURALLY(3),
    ENTER_WORD(4);

    companion object {
        fun parse(id: Int): TrainingType? = values().firstOrNull { it.id == id }
    }
}