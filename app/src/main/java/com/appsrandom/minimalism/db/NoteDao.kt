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

    @Delete
    suspend fun deleteFolders(folder: List<Folder>)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("DELETE FROM notes_table WHERE folder_id = :query")
    suspend fun deleteNotes(query: Int)

    @Query("DELETE FROM folders_table WHERE ref_folder_id = :query")
    suspend fun deleteFolders(query: Int)

    @Update
    suspend fun updateNote(note: Note)

    @Query("UPDATE notes_table set lock=0")
    suspend fun deleteAllLocks()

    @Query("SELECT id FROM folders_table")
    fun getAllFolderIds(): List<Int>

    @Query("SELECT * FROM folders_table WHERE ref_folder_id NOT IN (SELECT id FROM folders_table) AND ref_folder_id != -2147483648")
    fun getUnreferencedFolders(): LiveData<List<Folder>>

    @Query("SELECT * FROM notes_table WHERE folder_id = :query ORDER BY id ASC")
    fun getAllNotesByOldest(query: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE folder_id = :query ORDER BY id DESC")
    fun getAllNotesByNewest(query: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE folder_id = :query ORDER BY color ASC")
    fun getAllNotesByColor(query: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table WHERE title LIKE :query OR content LIKE :query OR date LIKE :query ORDER BY id ASC")
    fun searchNote(query: String): LiveData<List<Note>>

    @Query("SELECT * FROM folders_table WHERE ref_folder_id = :query ORDER BY id DESC")
    fun getAllFolders(query: Int): LiveData<List<Folder>>

    @Query("SELECT * FROM notes_table WHERE folder_id = :query")
    fun getAllNotes(query: String): LiveData<List<Note>>

}