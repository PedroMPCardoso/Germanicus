package com.pedrompcardoso.germanicus.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvWordLoader {
    
    fun loadWordsFromCsv(context: Context): List<GermanWord> {
        val words = mutableListOf<GermanWord>()
        
        try {
            val inputStream = context.assets.open("german_words.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            // Skip header line
            reader.readLine()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size >= 4) {
                    val german = parts[0].trim()
                    val english = parts[1].trim()
                    val gender = Gender.valueOf(parts[2].trim())
                    val difficulty = Difficulty.valueOf(parts[3].trim())
                    
                    words.add(GermanWord(german, english, gender, difficulty))
                }
            }
            
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to hardcoded words if CSV fails
            return getFallbackWords()
        }
        
        return words
    }
    
    private fun getFallbackWords(): List<GermanWord> {
        return listOf(
            GermanWord("Haus", "house", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Auto", "car", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Buch", "book", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Tisch", "table", Gender.MASCULINE, Difficulty.EASY),
            GermanWord("Stuhl", "chair", Gender.MASCULINE, Difficulty.EASY),
            GermanWord("Tür", "door", Gender.FEMININE, Difficulty.EASY),
            GermanWord("Fenster", "window", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Katze", "cat", Gender.FEMININE, Difficulty.EASY),
            GermanWord("Hund", "dog", Gender.MASCULINE, Difficulty.EASY),
            GermanWord("Brot", "bread", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Wasser", "water", Gender.NEUTER, Difficulty.EASY),
            GermanWord("Milch", "milk", Gender.FEMININE, Difficulty.EASY),
            GermanWord("Kaffee", "coffee", Gender.MASCULINE, Difficulty.EASY),
            GermanWord("Tee", "tea", Gender.MASCULINE, Difficulty.EASY),
            GermanWord("Apfel", "apple", Gender.MASCULINE, Difficulty.EASY)
        )
    }
} 