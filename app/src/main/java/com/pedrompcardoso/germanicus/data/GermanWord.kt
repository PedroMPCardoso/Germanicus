package com.pedrompcardoso.germanicus.data

data class GermanWord(
    val german: String,
    val english: String,
    val gender: Gender,
    val difficulty: Difficulty = Difficulty.EASY
)

enum class Gender(val article: String, val displayName: String) {
    MASCULINE("der", "Masculine"),
    FEMININE("die", "Feminine"),
    NEUTER("das", "Neuter"),
    PLURAL("die", "Plural")
}

enum class Difficulty {
    EASY, MEDIUM, HARD
} 