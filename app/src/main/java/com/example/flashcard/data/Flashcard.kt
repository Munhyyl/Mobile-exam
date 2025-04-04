package com.example.flashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mongolianWord: String,
    val foreignWord: String,
    val lastReviewed: Long = System.currentTimeMillis()
)
