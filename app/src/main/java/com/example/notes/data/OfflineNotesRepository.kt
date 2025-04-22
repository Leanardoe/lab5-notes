package com.example.notes.data

import kotlinx.coroutines.flow.Flow

class OfflineNotesRepository(private val noteDao: NoteDao) : NotesRepository {
    override fun getAllNotesStream(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getNoteStream(id: Int): Flow<Note?> = noteDao.getNote(id)
    override suspend fun insertNote(note: Note) = noteDao.insert(note)
    override suspend fun deleteNote(note: Note) = noteDao.delete(note)
    override suspend fun updateNote(note: Note) = noteDao.update(note)
}