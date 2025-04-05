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

    val allFlashcards: StateFlow<List<Flashcard>> = repository.getAllFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<FlashcardSettings> = settingsDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FlashcardSettings())

    private var _currentFlashcardIndex = MutableStateFlow(0)
    val currentFlashcardIndex: StateFlow<Int> = _currentFlashcardIndex.asStateFlow()

    val combinedFlashcardsAndSettings: StateFlow<Pair<List<Flashcard>, FlashcardSettings>> =
        allFlashcards.combine(settings) { flashcards, settings ->
            flashcards to settings
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList<Flashcard>() to FlashcardSettings()
        )

    init {
        viewModelScope.launch {
            allFlashcards.collect { flashcards ->
                if (flashcards.isEmpty()) {
                    _currentFlashcardIndex.value = -1
                } else if (_currentFlashcardIndex.value !in flashcards.indices) {
                    _currentFlashcardIndex.value = 0
                }
            }
        }
    }

    fun getCurrentFlashcard(): Flow<Flashcard?> = combinedFlashcardsAndSettings.map { (flashcards, _) ->
        if (flashcards.isNotEmpty() && _currentFlashcardIndex.value in flashcards.indices) {
            flashcards[_currentFlashcardIndex.value]
        } else {
            null
        }
    }

    fun nextFlashcard() {
        val flashcards = allFlashcards.value
        println("Next clicked, Flashcards size: ${flashcards.size}, Current Index: ${_currentFlashcardIndex.value}")
        if (flashcards.isNotEmpty()) {
            _currentFlashcardIndex.value = (_currentFlashcardIndex.value + 1) % flashcards.size
            println("New Index: ${_currentFlashcardIndex.value}")
        }
    }

    fun previousFlashcard() {
        val flashcards = allFlashcards.value
        println("Previous clicked, Flashcards size: ${flashcards.size}, Current Index: ${_currentFlashcardIndex.value}")
        if (flashcards.isNotEmpty()) {
            _currentFlashcardIndex.value = if (_currentFlashcardIndex.value - 1 < 0) {
                flashcards.size - 1
            } else {
                _currentFlashcardIndex.value - 1
            }
        }
        println("New Index: ${_currentFlashcardIndex.value}")
    }

    suspend fun insertFlashcard(flashcard: Flashcard) = repository.insert(flashcard)

    suspend fun updateFlashcard(flashcard: Flashcard) = repository.update(flashcard)

    suspend fun deleteFlashcard(flashcard: Flashcard) = repository.delete(flashcard)

    suspend fun updateSettings(showMongolian: Boolean, showForeign: Boolean) {
        settingsDataStore.updateSettings(showMongolian, showForeign)
    }

    fun getFlashcardById(id: Int): Flow<Flashcard?> = repository.getFlashcardById(id)
}