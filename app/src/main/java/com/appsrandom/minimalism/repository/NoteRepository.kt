package com.appsrandom.minimalism.repository

import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.models.Note

class NoteRepository(private val db: NoteDatabase) {

    fun allNotesByOldest(query: Int) = db.getNoteDao().getAllNotesByOldest(query)
    fun allNotesByNewest() = db.getNoteDao().getAllNotesByNewest()
    fun allNotesByColor() = db.getNoteDao().getAllNotesByColor()

    fun getAllFolders(query: Int) = db.getNoteDao().getAllFolders(query)
    fun getAllNotes(query: String) = db.getNoteDao().getAllNotes(query)
    fun searchNote(query: String) = db.getNoteDao().searchNote(query)

    suspend fun insertNote(note: Note) {
        db.getNoteDao().insertNote(note)
    }

    suspend fun insertFolder(folder: Folder) {
        db.getNoteDao().insertFolder(folder)
    }

    suspend fun deleteNote(note: Note) {
        db.getNoteDao().deleteNote(note)
    }

    suspend fun updateNote(note: Note) {
        db.getNoteDao().updateNote(note)
    }

    suspend fun deleteAllLocks() {
        db.getNoteDao().deleteAllLocks()
    }

}