package com.pedrompcardoso.germanicus.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedrompcardoso.germanicus.data.Difficulty
import com.pedrompcardoso.germanicus.data.Gender
import com.pedrompcardoso.germanicus.data.GermanWord
import com.pedrompcardoso.germanicus.data.WordRepository

class GameViewModel : ViewModel() {
    companion object {
        const val MAX_LIVES = 5
        const val MAX_TRANSLATION_LIVES = MAX_LIVES
        const val MAX_WORD_COMPLETION_LENGTH = 12
        private const val PROGRESSIVE_BLOCK_SIZE = 10
        private const val DIFFICULTY_STEP_SIZE = 2
        private const val MEDIUM_ONLY_BLOCK_INDEX = 5
        private const val WORD_COMPLETION_EXTRA_LETTERS = 4
        private const val INITIAL_COMPLETION_REVEAL_PERCENT = 40
        private const val MIN_COMPLETION_REVEAL_PERCENT = 10
        private const val COMPLETION_REVEAL_STEP_PERCENT = 5
        private const val COMPLETION_REVEAL_STEP_ANSWERS = 10
    }
    
    private val _currentWord = MutableLiveData<GermanWord>()
    val currentWord: LiveData<GermanWord> = _currentWord
    
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score
    
    private val _totalQuestions = MutableLiveData(0)
    val totalQuestions: LiveData<Int> = _totalQuestions

    private val _remainingLives = MutableLiveData(MAX_LIVES)
    val remainingLives: LiveData<Int> = _remainingLives

    private val _translationOptions = MutableLiveData<List<String>>()
    val translationOptions: LiveData<List<String>> = _translationOptions

    private val _completionLetterOptions = MutableLiveData<List<String>>()
    val completionLetterOptions: LiveData<List<String>> = _completionLetterOptions

    private val _completionSelectedIndices = MutableLiveData<List<Int>>(emptyList())
    val completionSelectedIndices: LiveData<List<Int>> = _completionSelectedIndices

    private val _completionAnswerSlots = MutableLiveData<List<String?>>(emptyList())
    val completionAnswerSlots: LiveData<List<String?>> = _completionAnswerSlots

    private val _completionRevealedIndices = MutableLiveData<Set<Int>>(emptySet())
    val completionRevealedIndices: LiveData<Set<Int>> = _completionRevealedIndices

    private val _heartRewardEvents = MutableLiveData(0)
    val heartRewardEvents: LiveData<Int> = _heartRewardEvents
    
    private val _gameMode = MutableLiveData<GameMode>()
    val gameMode: LiveData<GameMode> = _gameMode
    
    private val _isCorrect = MutableLiveData<Boolean?>()
    val isCorrect: LiveData<Boolean?> = _isCorrect
    
    private val _showResult = MutableLiveData<Boolean>()
    val showResult: LiveData<Boolean> = _showResult
    
    private val _isGameActive = MutableLiveData(false)
    val isGameActive: LiveData<Boolean> = _isGameActive
    
    private var currentWordIndex = 0
    private var wordsForGame = listOf<GermanWord>()
    private var progressiveBlockIndex = 0
    private var consecutiveCorrectAnswers = 0
    private var completionSelectedSlotIndices = listOf<Int>()
    
    fun startGame(mode: GameMode, wordCount: Int = 10) {
        _gameMode.value = mode
        _score.value = 0
        _totalQuestions.value = 0
        _isGameActive.value = true
        _showResult.value = false
        _isCorrect.value = null
        _remainingLives.value = MAX_LIVES
        _heartRewardEvents.value = 0
        resetCompletionAnswer()
        progressiveBlockIndex = 0
        consecutiveCorrectAnswers = 0

        wordsForGame = getNextProgressiveBlock(mode)
        currentWordIndex = 0
        
        if (wordsForGame.isNotEmpty()) {
            setCurrentWord(wordsForGame[0])
        } else {
            _isGameActive.value = false
        }
    }
    
    fun checkGenderAnswer(selectedGender: Gender) {
        val word = _currentWord.value ?: return
        val correct = word.gender == selectedGender

        if (correct) {
            _score.value = (_score.value ?: 0) + 1
            consecutiveCorrectAnswers++
            maybeRestoreLife()
        } else {
            consecutiveCorrectAnswers = 0
            _remainingLives.value = ((_remainingLives.value ?: MAX_LIVES) - 1).coerceAtLeast(0)
        }
        
        _totalQuestions.value = (_totalQuestions.value ?: 0) + 1

        if (correct) {
            nextQuestion()
        } else {
            _isCorrect.value = false
            _showResult.value = true
        }
    }
    
    fun checkTranslationAnswer(userAnswer: String) {
        val word = _currentWord.value ?: return
        val correct = userAnswer.trim().equals(word.english, ignoreCase = true)

        if (correct) {
            _score.value = (_score.value ?: 0) + 1
            consecutiveCorrectAnswers++
            maybeRestoreLife()
        } else {
            consecutiveCorrectAnswers = 0
            _remainingLives.value = ((_remainingLives.value ?: MAX_LIVES) - 1).coerceAtLeast(0)
        }
        
        _totalQuestions.value = (_totalQuestions.value ?: 0) + 1

        if (correct) {
            nextQuestion()
        } else {
            _isCorrect.value = false
            _showResult.value = true
        }
    }

    fun selectCompletionLetter(index: Int) {
        if (_gameMode.value != GameMode.WORD_COMPLETION || _showResult.value == true) {
            return
        }

        val options = _completionLetterOptions.value ?: return
        val selectedIndices = _completionSelectedIndices.value ?: emptyList()
        if (index !in options.indices || index in selectedIndices) {
            return
        }

        val word = _currentWord.value ?: return
        val answerSlots = _completionAnswerSlots.value.orEmpty()
        val targetSlotIndex = word.german.indices.firstOrNull { position ->
            position !in _completionRevealedIndices.value.orEmpty() && answerSlots.getOrNull(position) == null
        } ?: return

        _completionSelectedIndices.value = selectedIndices + index
        completionSelectedSlotIndices = completionSelectedSlotIndices + targetSlotIndex
        _completionAnswerSlots.value = answerSlots.replaceAt(targetSlotIndex, options[index])
    }

    fun removeLastCompletionLetter() {
        if (_gameMode.value != GameMode.WORD_COMPLETION || _showResult.value == true) {
            return
        }

        val selectedIndices = _completionSelectedIndices.value.orEmpty()
        if (selectedIndices.isEmpty()) {
            return
        }

        val lastSlotIndex = completionSelectedSlotIndices.lastOrNull() ?: return
        _completionSelectedIndices.value = selectedIndices.dropLast(1)
        completionSelectedSlotIndices = completionSelectedSlotIndices.dropLast(1)
        _completionAnswerSlots.value = _completionAnswerSlots.value.orEmpty().replaceAt(lastSlotIndex, null)
    }

    fun clearCompletionAnswer() {
        if (_gameMode.value != GameMode.WORD_COMPLETION || _showResult.value == true) {
            return
        }

        resetCompletionAnswer()
        _completionAnswerSlots.value = List(_currentWord.value?.german?.length ?: 0) { null }
    }

    fun submitWordCompletionAnswer() {
        if (_gameMode.value != GameMode.WORD_COMPLETION || _showResult.value == true) {
            return
        }

        val word = _currentWord.value ?: return
        if (!isCompletionAnswerComplete(word)) {
            return
        }

        checkWordCompletionAnswer(buildCompletionAnswer(word.german))
    }

    fun revealCompletionHint() {
        if (_gameMode.value != GameMode.WORD_COMPLETION || _showResult.value == true) {
            return
        }

        val word = _currentWord.value ?: return
        val answerSlots = _completionAnswerSlots.value.orEmpty()
        val revealedIndices = _completionRevealedIndices.value.orEmpty()
        val hintIndex = word.german.indices
            .filter { it !in revealedIndices && answerSlots.getOrNull(it) == null }
            .randomOrNull() ?: return

        _completionRevealedIndices.value = revealedIndices + hintIndex
    }
    
    fun nextQuestion() {
        if ((_remainingLives.value ?: 0) <= 0) {
            _isGameActive.value = false
            return
        }

        _showResult.value = false
        _isCorrect.value = null
        
        currentWordIndex++
        
        if (currentWordIndex < wordsForGame.size) {
            setCurrentWord(wordsForGame[currentWordIndex])
        } else {
            val mode = _gameMode.value
            if (mode == null) {
                _isGameActive.value = false
                return
            }

            wordsForGame = getNextProgressiveBlock(mode)
            currentWordIndex = 0

            if (wordsForGame.isNotEmpty()) {
                setCurrentWord(wordsForGame[0])
            } else {
                _isGameActive.value = false
            }
        }
    }

    fun hasNoTranslationLives(): Boolean {
        return _gameMode.value == GameMode.TRANSLATION && hasNoLives()
    }

    fun hasNoLives(): Boolean {
        return (_remainingLives.value ?: 0) <= 0
    }
    
    fun getCorrectAnswer(): String {
        return when (_gameMode.value) {
            GameMode.GENDER_GUESSING -> _currentWord.value?.gender?.article ?: ""
            GameMode.TRANSLATION -> _currentWord.value?.english ?: ""
            GameMode.WORD_COMPLETION -> _currentWord.value?.german ?: ""
            null -> ""
        }
    }
    
    fun getCurrentQuestion(): String {
        return when (_gameMode.value) {
            GameMode.GENDER_GUESSING -> _currentWord.value?.german ?: ""
            GameMode.TRANSLATION -> _currentWord.value?.german ?: ""
            GameMode.WORD_COMPLETION -> _currentWord.value?.english ?: ""
            null -> ""
        }
    }
    
    fun getQuestionPrompt(): String {
        return when (_gameMode.value) {
            GameMode.GENDER_GUESSING -> "What is the gender of this word?"
            GameMode.TRANSLATION -> "What is the English translation?"
            GameMode.WORD_COMPLETION -> "Complete the German translation"
            null -> ""
        }
    }

    private fun getNextProgressiveBlock(mode: GameMode): List<GermanWord> {
        val mediumCount: Int
        val easyCount: Int
        val hardCount: Int

        if (progressiveBlockIndex <= MEDIUM_ONLY_BLOCK_INDEX) {
            mediumCount = (progressiveBlockIndex * DIFFICULTY_STEP_SIZE).coerceAtMost(PROGRESSIVE_BLOCK_SIZE)
            easyCount = PROGRESSIVE_BLOCK_SIZE - mediumCount
            hardCount = 0
        } else {
            hardCount = ((progressiveBlockIndex - MEDIUM_ONLY_BLOCK_INDEX) * DIFFICULTY_STEP_SIZE)
                .coerceAtMost(PROGRESSIVE_BLOCK_SIZE)
            mediumCount = PROGRESSIVE_BLOCK_SIZE - hardCount
            easyCount = 0
        }

        progressiveBlockIndex++
        val excludedGenders = if (mode == GameMode.GENDER_GUESSING) setOf(Gender.PLURAL) else emptySet()

        return buildList {
            addAll(getRandomWordsByDifficulty(Difficulty.EASY, easyCount, excludedGenders))
            addAll(getRandomWordsByDifficulty(Difficulty.MEDIUM, mediumCount, excludedGenders))
            addAll(getRandomWordsByDifficulty(Difficulty.HARD, hardCount, excludedGenders))
        }.shuffled()
    }

    private fun getRandomWordsByDifficulty(
        difficulty: Difficulty,
        count: Int,
        excludedGenders: Set<Gender>
    ): List<GermanWord> {
        return if (_gameMode.value == GameMode.WORD_COMPLETION) {
            WordRepository.getRandomWordsByDifficulty(
                difficulty,
                count,
                MAX_WORD_COMPLETION_LENGTH,
                germanLettersOnly = true
            )
        } else if (excludedGenders.isEmpty()) {
            WordRepository.getRandomWordsByDifficulty(difficulty, count)
        } else {
            WordRepository.getRandomWordsByDifficulty(difficulty, count, excludedGenders)
        }
    }

    private fun setCurrentWord(word: GermanWord) {
        if (_gameMode.value == GameMode.WORD_COMPLETION) {
            resetCompletionAnswer()
            _completionRevealedIndices.value = buildCompletionRevealedIndices(word.german)
            _completionAnswerSlots.value = List(word.german.length) { null }
        }

        _currentWord.value = word

        if (_gameMode.value == GameMode.TRANSLATION) {
            _translationOptions.value = WordRepository.getTranslationOptions(word.english)
        }

        if (_gameMode.value == GameMode.WORD_COMPLETION) {
            _completionLetterOptions.value = buildCompletionLetterOptions(
                word.german,
                _completionRevealedIndices.value.orEmpty()
            )
        }
    }

    private fun maybeRestoreLife() {
        val currentLives = _remainingLives.value ?: MAX_LIVES
        if (consecutiveCorrectAnswers >= 5 && currentLives < MAX_LIVES) {
            _remainingLives.value = currentLives + 1
            consecutiveCorrectAnswers = 0
            _heartRewardEvents.value = (_heartRewardEvents.value ?: 0) + 1
        }
    }

    private fun checkWordCompletionAnswer(answer: String) {
        val word = _currentWord.value ?: return
        val correct = answer.equals(word.german, ignoreCase = true)

        if (correct) {
            _score.value = (_score.value ?: 0) + 1
            consecutiveCorrectAnswers++
            maybeRestoreLife()
        } else {
            consecutiveCorrectAnswers = 0
            _remainingLives.value = ((_remainingLives.value ?: MAX_LIVES) - 1).coerceAtLeast(0)
        }

        _totalQuestions.value = (_totalQuestions.value ?: 0) + 1

        if (correct) {
            nextQuestion()
        } else {
            _isCorrect.value = false
            _showResult.value = true
        }
    }

    private fun buildCompletionLetterOptions(word: String, revealedIndices: Set<Int>): List<String> {
        val targetLetters = word
            .mapIndexedNotNull { index, letter ->
                if (index in revealedIndices) null else letter.uppercaseChar().toString()
            }
        val wordUppercaseLetters = word.map { it.uppercaseChar().toString() }.toSet()
        val extraLetters = mutableListOf<String>()
        val decoyLetters = listOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "R", "S", "T", "U", "V", "W", "Z", "Ä", "Ö", "Ü"
        ).filter { it !in wordUppercaseLetters }.shuffled()

        decoyLetters.take(WORD_COMPLETION_EXTRA_LETTERS).forEach { extraLetters.add(it) }

        return (targetLetters + extraLetters).shuffled()
    }

    private fun buildCompletionRevealedIndices(word: String): Set<Int> {
        if (word.length <= 1) {
            return emptySet()
        }

        val revealPercent = getCompletionRevealPercent()
        val revealCount = ((word.length * revealPercent) / 100)
            .coerceAtLeast(1)
            .coerceAtMost(word.length - 1)

        return word.indices.shuffled().take(revealCount).toSet()
    }

    private fun getCompletionRevealPercent(): Int {
        val answeredQuestions = _totalQuestions.value ?: 0
        val reduction = (answeredQuestions / COMPLETION_REVEAL_STEP_ANSWERS) * COMPLETION_REVEAL_STEP_PERCENT
        return (INITIAL_COMPLETION_REVEAL_PERCENT - reduction).coerceAtLeast(MIN_COMPLETION_REVEAL_PERCENT)
    }

    private fun buildCompletionAnswer(word: String): String {
        val revealedIndices = _completionRevealedIndices.value.orEmpty()
        val answerSlots = _completionAnswerSlots.value.orEmpty()

        return buildString {
            word.forEachIndexed { index, letter ->
                if (index in revealedIndices) {
                    append(letter.uppercaseChar())
                } else {
                    append(answerSlots.getOrNull(index).orEmpty())
                }
            }
        }
    }

    private fun resetCompletionAnswer() {
        _completionSelectedIndices.value = emptyList()
        completionSelectedSlotIndices = emptyList()
        _completionAnswerSlots.value = emptyList()
    }

    private fun isCompletionAnswerComplete(word: GermanWord): Boolean {
        val revealedIndices = _completionRevealedIndices.value.orEmpty()
        val answerSlots = _completionAnswerSlots.value.orEmpty()

        return word.german.indices.all { index ->
            index in revealedIndices || answerSlots.getOrNull(index) != null
        }
    }

    private fun <T> List<T>.replaceAt(index: Int, value: T): List<T> {
        return mapIndexed { currentIndex, currentValue ->
            if (currentIndex == index) value else currentValue
        }
    }
} 
