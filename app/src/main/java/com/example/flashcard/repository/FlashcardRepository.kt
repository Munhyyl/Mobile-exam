package com.example.flashcard.repository

import com.example.flashcard.data.Flashcard
import com.example.flashcard.data.FlashcardDao
import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    val allFlashcards: Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()

    suspend fun insert(flashcard: Flashcard) = flashcardDao.insert(flashcard)

    suspend fun update(flashcard: Flashcard) = flashcardDao.update(flashcard)

    suspend fun delete(flashcard: Flashcard) = flashcardDao.delete(flashcard)

    suspend fun getFlashcardById(id: Int) = flashcardDao.getFlashcardById(id)
}