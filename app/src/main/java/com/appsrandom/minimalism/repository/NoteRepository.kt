package com.appsrandom.minimalism.repository

import androidx.lifecycle.LiveData
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.models.Note

class NoteRepository(private val db: NoteDatabase) {

    fun allNotesByOldest(query: Int) = db.getNoteDao().getAllNotesByOldest(query)
    fun allNotesByNewest(query: Int) = db.getNoteDao().getAllNotesByNewest(query)
    fun allNotesByColor(query: Int) = db.getNoteDao().getAllNotesByColor(query)

    fun getAllFolders(query: Int) = db.getNoteDao().getAllFolders(query)
    fun getAllNotes(query: String) = db.getNoteDao().getAllNotes(query)
    fun getUnreferencedFolders(query: List<Int>) = db.getNoteDao().getUnreferencedFolders(query)
    fun searchNote(query: String) = db.getNoteDao().searchNote(query)
    fun getAllFolderIds() = db.getNoteDao().getAllFolderIds()

    suspend fun insertNote(note: Note) {
        db.getNoteDao().insertNote(note)
    }

    suspend fun insertFolder(folder: Folder) {
        db.getNoteDao().insertFolder(folder)
    }

    suspend fun deleteNote(note: Note) {
        db.getNoteDao().deleteNote(note)
    }

    suspend fun deleteFolder(folder: Folder) {
        db.getNoteDao().deleteFolder(folder)
    }

    suspend fun deleteNotes(query: Int) {
        db.getNoteDao().deleteNotes(query)
    }

    suspend fun deleteFolders(query: Int) {
        db.getNoteDao().deleteFolders(query)
    }

    suspend fun updateNote(note: Note) {
        db.getNoteDao().updateNote(note)
    }

    suspend fun deleteAllLocks() {
        db.getNoteDao().deleteAllLocks()
    }

}