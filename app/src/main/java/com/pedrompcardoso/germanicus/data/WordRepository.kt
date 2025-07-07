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