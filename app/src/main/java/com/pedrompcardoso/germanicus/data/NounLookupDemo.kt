package com.pedrompcardoso.germanicus.data

import android.content.Context
import android.util.Log

/**
 * Demo class showing how to use the GermanNouns functionality
 * Similar to your Python example
 */
class NounLookupDemo(private val context: Context) {
    
    private val nouns = GermanNouns(context)
    
    /**
     * Demo function showing how to lookup a word like in your Python example
     */
    fun demonstrateLookup() {
        // Lookup a word (similar to your Python code)
        val word = nouns["Fahrrad"]
        
        Log.d("NounLookup", "Found ${word.size} entry(ies) for 'Fahrrad'")
        
        // Print each entry (similar to pprint)
        word.forEachIndexed { index, noun ->
            Log.d("NounLookup", "Entry ${index + 1}: ${noun.toMap()}")
        }
        
        // You can also use the prettyPrint method
        nouns.prettyPrint("Fahrrad")
    }
    
    /**
     * Example of how to use the nouns in your app
     */
    fun getNounInfo(lemma: String): List<Map<String, Any>> {
        return nouns[lemma].map { it.toMap() }
    }
    
    /**
     * Get all declension forms for a specific case and number
     */
    fun getDeclension(lemma: String, case: String, number: String): String? {
        val words = nouns[lemma]
        if (words.isEmpty()) return null
        
        val flexion = words[0].flexion
        return when ("$case $number") {
            "nominativ singular" -> flexion.nominativSingular
            "nominativ plural" -> flexion.nominativPlural
            "genitiv singular" -> flexion.genitivSingular
            "genitiv plural" -> flexion.genitivPlural
            "dativ singular" -> flexion.dativSingular
            "dativ plural" -> flexion.dativPlural
            "akkusativ singular" -> flexion.akkusativSingular
            "akkusativ plural" -> flexion.akkusativPlural
            else -> null
        }
    }
    
    /**
     * Check if a word exists in the database
     */
    fun wordExists(lemma: String): Boolean {
        return nouns.contains(lemma)
    }
    
    /**
     * Get some statistics about the loaded nouns
     */
    fun getStats(): Map<String, Any> {
        return mapOf(
            "total_lemmas" to nouns.size(),
            "sample_lemmas" to nouns.getAllLemmas().take(10).toList()
        )
    }
}
