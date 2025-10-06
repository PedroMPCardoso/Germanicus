package com.pedrompcardoso.germanicus.data

/**
 * Data class representing a German noun with all its declension forms
 */
data class GermanNoun(
    val lemma: String,
    val pos: List<String>,
    val genus: String,
    val flexion: Flexion
)

/**
 * Data class containing all declension forms for a German noun
 */
data class Flexion(
    val nominativSingular: String,
    val nominativPlural: String,
    val genitivSingular: String,
    val genitivPlural: String,
    val dativSingular: String,
    val dativPlural: String,
    val akkusativSingular: String,
    val akkusativPlural: String,
    val nominativSingularAlt: String? = null,
    val genitivSingularAlt: String? = null,
    val dativSingularAlt: String? = null
)

/**
 * Extension function to create a map representation similar to the Python output
 */
fun GermanNoun.toMap(): Map<String, Any> {
    val flexionMap = mutableMapOf<String, String>()
    
    flexionMap["nominativ singular"] = flexion.nominativSingular
    flexionMap["nominativ plural"] = flexion.nominativPlural
    flexionMap["genitiv singular"] = flexion.genitivSingular
    flexionMap["genitiv plural"] = flexion.genitivPlural
    flexionMap["dativ singular"] = flexion.dativSingular
    flexionMap["dativ plural"] = flexion.dativPlural
    flexionMap["akkusativ singular"] = flexion.akkusativSingular
    flexionMap["akkusativ plural"] = flexion.akkusativPlural
    
    // Add alternative forms if they exist
    flexion.nominativSingularAlt?.let { flexionMap["nominativ singular*"] = it }
    flexion.genitivSingularAlt?.let { flexionMap["genitiv singular*"] = it }
    flexion.dativSingularAlt?.let { flexionMap["dativ singular*"] = it }
    
    return mapOf(
        "lemma" to lemma,
        "pos" to pos,
        "genus" to genus,
        "flexion" to flexionMap
    )
}
