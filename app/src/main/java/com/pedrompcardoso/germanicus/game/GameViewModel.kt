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
        const val MAX_TRANSLATION_LIVES = 5
        private const val TRANSLATION_BLOCK_SIZE = 10
        private const val DIFFICULTY_STEP_SIZE = 2
        private const val MEDIUM_ONLY_BLOCK_INDEX = 5
    }
    
    private val _currentWord = MutableLiveData<GermanWord>()
    val currentWord: LiveData<GermanWord> = _currentWord
    
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score
    
    private val _totalQuestions = MutableLiveData(0)
    val totalQuestions: LiveData<Int> = _totalQuestions

    private val _remainingLives = MutableLiveData(MAX_TRANSLATION_LIVES)
    val remainingLives: LiveData<Int> = _remainingLives

    private val _translationOptions = MutableLiveData<List<String>>()
    val translationOptions: LiveData<List<String>> = _translationOptions
    
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
    private var translationBlockIndex = 0
    
    fun startGame(mode: GameMode, wordCount: Int = 10) {
        _gameMode.value = mode
        _score.value = 0
        _totalQuestions.value = 0
        _isGameActive.value = true
        _showResult.value = false
        _isCorrect.value = null
        _remainingLives.value = MAX_TRANSLATION_LIVES
        translationBlockIndex = 0

        wordsForGame = if (mode == GameMode.TRANSLATION) {
            getNextTranslationBlock()
        } else {
            WordRepository.getRandomWords(wordCount)
        }
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
        
        _isCorrect.value = correct
        _showResult.value = true
        
        if (correct) {
            _score.value = (_score.value ?: 0) + 1
        }
        
        _totalQuestions.value = (_totalQuestions.value ?: 0) + 1
    }
    
    fun checkTranslationAnswer(userAnswer: String) {
        val word = _currentWord.value ?: return
        val correct = userAnswer.trim().equals(word.english, ignoreCase = true)

        if (correct) {
            _score.value = (_score.value ?: 0) + 1
        } else {
            _remainingLives.value = ((_remainingLives.value ?: MAX_TRANSLATION_LIVES) - 1).coerceAtLeast(0)
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
        if (_gameMode.value == GameMode.TRANSLATION && (_remainingLives.value ?: 0) <= 0) {
            _isGameActive.value = false
            return
        }

        _showResult.value = false
        _isCorrect.value = null
        
        currentWordIndex++
        
        if (currentWordIndex < wordsForGame.size) {
            setCurrentWord(wordsForGame[currentWordIndex])
        } else if (_gameMode.value == GameMode.TRANSLATION) {
            wordsForGame = getNextTranslationBlock()
            currentWordIndex = 0

            if (wordsForGame.isNotEmpty()) {
                setCurrentWord(wordsForGame[0])
            } else {
                _isGameActive.value = false
            }
        } else {
            _isGameActive.value = false
        }
    }

    fun hasNoTranslationLives(): Boolean {
        return _gameMode.value == GameMode.TRANSLATION && (_remainingLives.value ?: 0) <= 0
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

    private fun getNextTranslationBlock(): List<GermanWord> {
        val mediumCount: Int
        val easyCount: Int
        val hardCount: Int

        if (translationBlockIndex <= MEDIUM_ONLY_BLOCK_INDEX) {
            mediumCount = (translationBlockIndex * DIFFICULTY_STEP_SIZE).coerceAtMost(TRANSLATION_BLOCK_SIZE)
            easyCount = TRANSLATION_BLOCK_SIZE - mediumCount
            hardCount = 0
        } else {
            hardCount = ((translationBlockIndex - MEDIUM_ONLY_BLOCK_INDEX) * DIFFICULTY_STEP_SIZE)
                .coerceAtMost(TRANSLATION_BLOCK_SIZE)
            mediumCount = TRANSLATION_BLOCK_SIZE - hardCount
            easyCount = 0
        }

        translationBlockIndex++

        return buildList {
            addAll(WordRepository.getRandomWordsByDifficulty(Difficulty.EASY, easyCount))
            addAll(WordRepository.getRandomWordsByDifficulty(Difficulty.MEDIUM, mediumCount))
            addAll(WordRepository.getRandomWordsByDifficulty(Difficulty.HARD, hardCount))
        }.shuffled()
    }

    private fun setCurrentWord(word: GermanWord) {
        _currentWord.value = word

        if (_gameMode.value == GameMode.TRANSLATION) {
            _translationOptions.value = WordRepository.getTranslationOptions(word.english)
        }
    }
} 
