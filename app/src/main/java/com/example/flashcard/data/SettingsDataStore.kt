package com.example.flashcard.data


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class FlashcardSettings(
    val showMongolian: Boolean = true,
    val showForeign: Boolean = true
)

class SettingsDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    private object PreferencesKeys {
        val SHOW_MONGOLIAN = booleanPreferencesKey("show_mongolian")
        val SHOW_FOREIGN = booleanPreferencesKey("show_foreign")
    }

    val settingsFlow: Flow<FlashcardSettings> = context.dataStore.data
        .map { preferences ->
            FlashcardSettings(
                showMongolian = preferences[PreferencesKeys.SHOW_MONGOLIAN] ?: true,
                showForeign = preferences[PreferencesKeys.SHOW_FOREIGN] ?: true
            )
        }

    suspend fun updateSettings(showMongolian: Boolean, showForeign: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_MONGOLIAN] = showMongolian
            preferences[PreferencesKeys.SHOW_FOREIGN] = showForeign
        }
    }
}