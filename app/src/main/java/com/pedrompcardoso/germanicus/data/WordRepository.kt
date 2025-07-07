package com.pedrompcardoso.germanicus.data

object WordRepository {
    
    private val germanWords = listOf(
        // Easy words
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
        GermanWord("Apfel", "apple", Gender.MASCULINE, Difficulty.EASY),
        
        // Medium words
        GermanWord("Computer", "computer", Gender.MASCULINE, Difficulty.MEDIUM),
        GermanWord("Telefon", "telephone", Gender.NEUTER, Difficulty.MEDIUM),
        GermanWord("Zeitung", "newspaper", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Zeitschrift", "magazine", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Schlüssel", "key", Gender.MASCULINE, Difficulty.MEDIUM),
        GermanWord("Tasche", "bag", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Geld", "money", Gender.NEUTER, Difficulty.MEDIUM),
        GermanWord("Zeit", "time", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Tag", "day", Gender.MASCULINE, Difficulty.MEDIUM),
        GermanWord("Nacht", "night", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Jahr", "year", Gender.NEUTER, Difficulty.MEDIUM),
        GermanWord("Monat", "month", Gender.MASCULINE, Difficulty.MEDIUM),
        GermanWord("Woche", "week", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Stadt", "city", Gender.FEMININE, Difficulty.MEDIUM),
        GermanWord("Land", "country", Gender.NEUTER, Difficulty.MEDIUM),
        
        // Hard words
        GermanWord("Geschichte", "history", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Wissenschaft", "science", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Entwicklung", "development", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Verbindung", "connection", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Verständnis", "understanding", Gender.NEUTER, Difficulty.HARD),
        GermanWord("Möglichkeit", "possibility", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Verantwortung", "responsibility", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Bedeutung", "meaning", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Erfahrung", "experience", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Verhältnis", "relationship", Gender.NEUTER, Difficulty.HARD),
        GermanWord("Gesellschaft", "society", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Regierung", "government", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Wirtschaft", "economy", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Kultur", "culture", Gender.FEMININE, Difficulty.HARD),
        GermanWord("Natur", "nature", Gender.FEMININE, Difficulty.HARD)
    )
    
    fun getAllWords(): List<GermanWord> = germanWords
    
    fun getWordsByDifficulty(difficulty: Difficulty): List<GermanWord> {
        return germanWords.filter { it.difficulty == difficulty }
    }
    
    fun getRandomWords(count: Int): List<GermanWord> {
        return germanWords.shuffled().take(count)
    }
    
    fun getRandomWord(): GermanWord {
        return germanWords.random()
    }
} 