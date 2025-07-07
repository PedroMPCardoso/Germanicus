package com.pedrompcardoso.germanicus.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedrompcardoso.germanicus.data.Gender
import com.pedrompcardoso.germanicus.data.GermanWord
import com.pedrompcardoso.germanicus.data.WordRepository

class GameViewModel : ViewModel() {
    
    private val _currentWord = MutableLiveData<GermanWord>()
    val currentWord: LiveData<GermanWord> = _currentWord
    
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score
    
    private val _totalQuestions = MutableLiveData(0)
    val totalQuestions: LiveData<Int> = _totalQuestions
    
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
    
    fun startGame(mode: GameMode, wordCount: Int = 10) {
        _gameMode.value = mode
        _score.value = 0
        _totalQuestions.value = 0
        _isGameActive.value = true
        _showResult.value = false
        _isCorrect.value = null
        
        wordsForGame = WordRepository.getRandomWords(wordCount)
        currentWordIndex = 0
        
        if (wordsForGame.isNotEmpty()) {
            _currentWord.value = wordsForGame[0]
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
        
        _isCorrect.value = correct
        _showResult.value = true
        
        if (correct) {
            _score.value = (_score.value ?: 0) + 1
        }
        
        _totalQuestions.value = (_totalQuestions.value ?: 0) + 1
    }
    
    fun nextQuestion() {
        _showResult.value = false
        _isCorrect.value = null
        
        currentWordIndex++
        
        if (currentWordIndex < wordsForGame.size) {
            _currentWord.value = wordsForGame[currentWordIndex]
        } else {
            _isGameActive.value = false
        }
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
} 