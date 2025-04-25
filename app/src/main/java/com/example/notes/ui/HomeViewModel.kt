package com.example.notes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.Note
import com.example.notes.data.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote

    val homeUiState: StateFlow<HomeUiState> =
        notesRepository.getAllNotesStream().map { notes ->
            HomeUiState(
                noteList = notes,
                currentNote = _currentNote.value
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeUiState()
        )

    fun selectNote(note: Note) {
        _currentNote.value = note
    }

    suspend fun addNote(note: Note) {
        notesRepository.insertNote(note)
    }

    suspend fun editNote(note: Note) {
        notesRepository.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        notesRepository.deleteNote(note)
        _currentNote.value = null
    }
}

data class HomeUiState(
    val noteList: List<Note> = listOf(),
    val currentNote: Note? = null
)
