package com.pedrompcardoso.germanicus.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Class for loading and querying German nouns from the nouns.csv file
 * Similar to the Python Nouns class you referenced
 */
class GermanNouns(private val context: Context) {
    
    private val nounsMap: Map<String, List<GermanNoun>> by lazy {
        loadNouns()
    }
    
    /**
     * Lookup a word by its lemma (dictionary form)
     * Returns a list of GermanNoun objects (similar to Python's list of dicts)
     */
    operator fun get(lemma: String): List<GermanNoun> {
        return nounsMap[lemma] ?: emptyList()
    }
    
    /**
     * Get all available lemmas
     */
    fun getAllLemmas(): Set<String> = nounsMap.keys
    
    /**
     * Check if a lemma exists
     */
    fun contains(lemma: String): Boolean = lemma in nounsMap
    
    /**
     * Get the count of unique lemmas
     */
    fun size(): Int = nounsMap.size
    
    /**
     * Load nouns from the CSV file
     */
    private fun loadNouns(): Map<String, List<GermanNoun>> {
        val nouns = mutableMapOf<String, MutableList<GermanNoun>>()
        
        try {
            val inputStream = context.assets.open("nouns.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            
            // Skip header line
            reader.readLine()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let { processLine(it, nouns) }
            }
            
            reader.close()
            inputStream.close()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return nouns
    }
    
    /**
     * Process a single line from the CSV file
     */
    private fun processLine(line: String, nouns: MutableMap<String, MutableList<GermanNoun>>) {
        val parts = line.split(",")
        
        if (parts.size < 70) return // Skip incomplete lines
        
        val lemma = parts[0].trim()
        val pos = parts[1].split(",").map { it.trim() }
        val genus = parts[2].trim()
        
        // Extract declension forms (corrected indices based on actual CSV structure)
        // Based on the Fahrrad example: indices 7, 16, 25, 34, 43, 52, 61, 70 are correct
        val nominativSingular = parts[7].trim()
        val nominativPlural = parts[16].trim()
        val genitivSingular = parts[25].trim()
        val genitivPlural = parts[34].trim()
        val dativSingular = parts[43].trim()
        val dativPlural = parts[52].trim()
        val akkusativSingular = parts[61].trim()
        val akkusativPlural = parts[70].trim()
        
        // Alternative forms (marked with *) - these are the next columns after the main forms
        val nominativSingularAlt = parts[8].trim().takeIf { it.isNotEmpty() }
        val genitivSingularAlt = parts[26].trim().takeIf { it.isNotEmpty() }
        val dativSingularAlt = parts[44].trim().takeIf { it.isNotEmpty() }
        
        // Skip entries with empty lemma or essential forms
        if (lemma.isEmpty() || nominativSingular.isEmpty() || genus.isEmpty()) {
            return
        }
        
        val flexion = Flexion(
            nominativSingular = nominativSingular,
            nominativPlural = nominativPlural,
            genitivSingular = genitivSingular,
            genitivPlural = genitivPlural,
            dativSingular = dativSingular,
            dativPlural = dativPlural,
            akkusativSingular = akkusativSingular,
            akkusativPlural = akkusativPlural,
            nominativSingularAlt = nominativSingularAlt,
            genitivSingularAlt = genitivSingularAlt,
            dativSingularAlt = dativSingularAlt
        )
        
        val noun = GermanNoun(
            lemma = lemma,
            pos = pos,
            genus = genus,
            flexion = flexion
        )
        
        // Add to map (some lemmas might have multiple entries)
        nouns.getOrPut(lemma) { mutableListOf() }.add(noun)
    }
    
    /**
     * Pretty print a word (similar to Python's pprint)
     */
    fun prettyPrint(lemma: String) {
        val words = this[lemma]
        if (words.isEmpty()) {
            println("Word '$lemma' not found")
            return
        }
        
        println("Found ${words.size} entry(ies) for '$lemma':")
        words.forEachIndexed { index, noun ->
            println("Entry ${index + 1}:")
            println(noun.toMap())
            println()
        }
    }
}
