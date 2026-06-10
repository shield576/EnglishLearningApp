package com.example.englishlearningapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordViewModel(private val wordDao: WordDao) : ViewModel() {
    val words = wordDao.getVisibleWords().asLiveData()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _insertSuccess = MutableSharedFlow<Unit>(replay = 0)
    val insertSuccess: SharedFlow<Unit> = _insertSuccess

    fun addWord(english: String, japanese: String) {
        val englishWord = english.trim()
        val japaneseMeaning = japanese.trim()
        if (englishWord.isBlank() || japaneseMeaning.isBlank()) return

        viewModelScope.launch {
            val existing = wordDao.getWordByEnglish(englishWord)
            if (existing != null) {
                _errorMessage.value = "「$englishWord」はすでに登録されています"
                return@launch
            }
            wordDao.insert(
                Word(
                    englishWord = englishWord,
                    japaneseMeaning = japaneseMeaning
                )
            )
            _insertSuccess.emit(Unit)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun showAllWords() {
        viewModelScope.launch {
            wordDao.showAllWords()
        }
    }

    fun removeWord(word: Word) {
        viewModelScope.launch {
            wordDao.delete(word)
        }
    }

    fun hideWord(word: Word) {
        viewModelScope.launch {
            wordDao.update(word.copy(isVisible = false))
        }
    }
}

class WordViewModelFactory(private val wordDao: WordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(wordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
