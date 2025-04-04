package com.example.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flashcard.data.SettingsDataStore
import com.example.flashcard.repository.FlashcardRepository
import com.example.flashcard.view.FlashcardViewModel

// FlashcardViewModelFactory.kt
class FlashcardViewModelFactory(
    private val repository: FlashcardRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashcardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlashcardViewModel(repository, settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}