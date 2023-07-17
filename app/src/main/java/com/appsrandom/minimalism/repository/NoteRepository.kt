package com.appsrandom.minimalism.repository

import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.models.Note

class NoteRepository(private val db: NoteDatabase) {

    fun allNotes() = db.getNoteDao().getAllNotes()
    fun searchNote(query: String) = db.getNoteDao().searchNote(query)

    suspend fun insertNote(note: Note) {
        db.getNoteDao().insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        db.getNoteDao().deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        db.getNoteDao().updateNote(note)
    }

}