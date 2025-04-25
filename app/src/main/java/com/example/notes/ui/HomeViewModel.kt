package com.example.notes.ui

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.Note
import com.example.notes.data.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(notesRepository: NotesRepository) : ViewModel() {
    val notes = notesRepository

    val homeUiState: StateFlow<HomeUiState> =
        notes.getAllNotesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState()
            )

    fun selectNote(note: Note) {
    }

    suspend fun addNote(note: Note) {
        notes.insertNote(note)
    }

    suspend fun editNote(note: Note) {
        notes.updateNote(note)
    }
}

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    var currentNote: Note? = null,
)