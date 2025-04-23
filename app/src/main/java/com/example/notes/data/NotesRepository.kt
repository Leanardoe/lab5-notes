package com.example.notes.data

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDao: NoteDao) :  {
    fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()
    fun getNoteStream(id: Int): Flow<Note?> = noteDao.getNote(id)
    suspend fun insertNote(note: Note) = noteDao.insert(note)
    suspend fun deleteNote(note: Note) = noteDao.delete(note)
    suspend fun updateNote(note: Note) = noteDao.update(note)
}