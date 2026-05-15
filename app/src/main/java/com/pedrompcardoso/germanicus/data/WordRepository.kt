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
        if (matchingWords.isEmpty() || count <= 0) {
            return emptyList()
        }

        return if (count <= matchingWords.size) {
            matchingWords.shuffled().take(count)
        } else {
            List(count) { matchingWords.random() }
        }
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
