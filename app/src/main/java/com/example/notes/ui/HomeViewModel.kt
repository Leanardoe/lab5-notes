package com.example.notes.ui

import androidx.compose.runtime.collectAsState
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.Note
import com.example.notes.data.NotesRepository
import com.example.notes.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(notesRepository: NotesRepository, userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    val notes = notesRepository
    val preferences = userPreferencesRepository

    val homeUiState: StateFlow<HomeUiState> =
        notes.getAllNotesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState()
            )

    val darkModeState: StateFlow<DarkModeState> =
        preferences.isDarkMode.map { isDarkMode ->
            DarkModeState(isDarkMode)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = DarkModeState(false)
            )

    suspend fun addNote(note: Note) {
        notes.insertNote(note)
    }

    suspend fun editNote(note: Note) {
        notes.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        notes.deleteNote(note)
    }

    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferences.saveLayoutPreference(isDarkMode)
        }
    }
}

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    var currentNote: Note? = null,
)

data class DarkModeState(
    val darkMode: Boolean
)