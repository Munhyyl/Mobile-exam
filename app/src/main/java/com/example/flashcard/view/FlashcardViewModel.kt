package com.example.flashcard.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.Flashcard
import com.example.flashcard.data.FlashcardSettings
import com.example.flashcard.data.SettingsDataStore
import com.example.flashcard.repository.FlashcardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: FlashcardRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    val allFlashcards = repository.allFlashcards
    val settings = settingsDataStore.settingsFlow

    private var _currentFlashcardIndex = 0
    val currentFlashcardIndex: Int
        get() = _currentFlashcardIndex

    val combinedFlashcardsAndSettings: StateFlow<Pair<List<Flashcard>, FlashcardSettings>> = allFlashcards.combine(settings) { flashcards, settings ->
        flashcards to settings
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<Flashcard>() to FlashcardSettings()
    )

    init {
        viewModelScope.launch {
            allFlashcards.collect { flashcards ->
                if (flashcards.isNotEmpty()) {
                    _currentFlashcardIndex = 0
                } else {
                    _currentFlashcardIndex = -1
                }
            }
        }
    }

    fun getCurrentFlashcard(): Flashcard? {
        val (flashcards, _) = combinedFlashcardsAndSettings.value
        return if (flashcards.isNotEmpty() && _currentFlashcardIndex in flashcards.indices) {
            flashcards[_currentFlashcardIndex]
        } else {
            null
        }
    }

    fun nextFlashcard() {
        val (flashcards, _) = combinedFlashcardsAndSettings.value
        if (flashcards.isNotEmpty()) {
            _currentFlashcardIndex = (_currentFlashcardIndex + 1) % flashcards.size
        }
    }

    fun previousFlashcard() {
        val (flashcards, _) = combinedFlashcardsAndSettings.value
        if (flashcards.isNotEmpty()) {
            _currentFlashcardIndex = if (_currentFlashcardIndex - 1 < 0) {
                flashcards.size - 1
            } else {
                _currentFlashcardIndex - 1
            }
        }
    }

    suspend fun insertFlashcard(flashcard: Flashcard) = repository.insert(flashcard)

    suspend fun updateFlashcard(flashcard: Flashcard) = repository.update(flashcard)

    suspend fun deleteFlashcard(flashcard: Flashcard) = repository.delete(flashcard)

    suspend fun updateSettings(showMongolian: Boolean, showForeign: Boolean) {
        settingsDataStore.updateSettings(showMongolian, showForeign)
    }

    suspend fun getFlashcardById(id: Int): Flashcard? = repository.getFlashcardById(id)
}