package com.pedrompcardoso.germanicus.data

import android.content.Context

object WordRepository {
    
    private var germanWords: List<GermanWord> = emptyList()
    private var isInitialized = false
    
    fun initialize(context: Context) {
        if (!isInitialized) {
            germanWords = CsvWordLoader.loadWordsFromCsv(context)
            isInitialized = true
        }
    }
    
    fun getAllWords(): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords
    }
    
    fun getWordsByDifficulty(difficulty: Difficulty): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords.filter { it.difficulty == difficulty }
    }

    fun getRandomWordsByDifficulty(difficulty: Difficulty, count: Int): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }

        val matchingWords = germanWords.filter { it.difficulty == difficulty }
        return getRandomFromWords(matchingWords, count)
    }

    fun getRandomWordsByDifficulty(
        difficulty: Difficulty,
        count: Int,
        excludedGenders: Set<Gender>
    ): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }

        val matchingWords = germanWords.filter { it.difficulty == difficulty && it.gender !in excludedGenders }
        return getRandomFromWords(matchingWords, count)
    }

    private fun getRandomFromWords(matchingWords: List<GermanWord>, count: Int): List<GermanWord> {
        if (matchingWords.isEmpty() || count <= 0) {
            return emptyList()
        }

        return if (count <= matchingWords.size) {
            matchingWords.shuffled().take(count)
        } else {
            List(count) { matchingWords.random() }
        }
    }

    fun getTranslationOptions(correctTranslation: String, optionCount: Int = 4): List<String> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }

        val normalizedCorrect = correctTranslation.trim().lowercase()
        val wrongOptions = germanWords
            .map { it.english.trim() }
            .filter { it.isNotEmpty() && it.lowercase() != normalizedCorrect }
            .distinctBy { it.lowercase() }
            .shuffled()
            .take((optionCount - 1).coerceAtLeast(0))

        return (wrongOptions + correctTranslation).shuffled()
    }
    
    fun getRandomWords(count: Int): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords.shuffled().take(count)
    }
    
    fun getRandomWord(): GermanWord {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords.random()
    }
    
    fun getWordCount(): Int {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords.size
    }
    
    fun getWordsByGender(gender: Gender): List<GermanWord> {
        if (!isInitialized) {
            throw IllegalStateException("WordRepository not initialized. Call initialize() first.")
        }
        return germanWords.filter { it.gender == gender }
    }
} 
