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
        private const val PROGRESSIVE_BLOCK_SIZE = 10
        private const val DIFFICULTY_STEP_SIZE = 2
        private const val MEDIUM_ONLY_BLOCK_INDEX = 5
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
    
    fun startGame(mode: GameMode, wordCount: Int = 10) {
        _gameMode.value = mode
        _score.value = 0
        _totalQuestions.value = 0
        _isGameActive.value = true
        _showResult.value = false
        _isCorrect.value = null
        _remainingLives.value = MAX_LIVES
        _heartRewardEvents.value = 0
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
            null -> ""
        }
    }
    
    fun getCurrentQuestion(): String {
        return when (_gameMode.value) {
            GameMode.GENDER_GUESSING -> _currentWord.value?.german ?: ""
            GameMode.TRANSLATION -> _currentWord.value?.german ?: ""
            null -> ""
        }
    }
    
    fun getQuestionPrompt(): String {
        return when (_gameMode.value) {
            GameMode.GENDER_GUESSING -> "What is the gender of this word?"
            GameMode.TRANSLATION -> "What is the English translation?"
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
        return if (excludedGenders.isEmpty()) {
            WordRepository.getRandomWordsByDifficulty(difficulty, count)
        } else {
            WordRepository.getRandomWordsByDifficulty(difficulty, count, excludedGenders)
        }
    }

    private fun setCurrentWord(word: GermanWord) {
        _currentWord.value = word

        if (_gameMode.value == GameMode.TRANSLATION) {
            _translationOptions.value = WordRepository.getTranslationOptions(word.english)
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
} 
