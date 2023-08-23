package com.appsrandom.minimalism.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.models.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: Folder)

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE notes_table set lock=0")
    suspend fun deleteAllLocks()

    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotesByOldest(): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotesByNewest(): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table ORDER BY color ASC")
    fun getAllNotesByColor(): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE title LIKE :query OR content LIKE :query OR date LIKE :query ORDER BY id ASC")
    fun searchNote(query: String): LiveData<List<Note>>

    @Query("SELECT * FROM folders_table WHERE ref_folder = :query ORDER BY id DESC")
    fun getAllFolders(query: String): LiveData<List<Folder>>

    @Query("SELECT * FROM notes_table WHERE folder_name = :query")
    fun getAllNotes(query: String): LiveData<List<Note>>

}