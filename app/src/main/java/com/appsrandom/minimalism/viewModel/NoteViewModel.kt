package com.appsrandom.minimalism.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.models.Note
import com.appsrandom.minimalism.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    fun deleteAllLocks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllLocks()
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun deleteFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFolder(folder)
    }

    fun deleteNotes(query: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotes(query)
    }

    fun deleteFolders(query: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFolders(query)
    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertFolder(folder)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
    }

    fun searchNote(query: String): LiveData<List<Note>> {
        return repository.searchNote(query)
    }

    fun getAllNotes(query: String): LiveData<List<Note>> {
        return repository.getAllNotes(query)
    }

    fun getAllFolders(query: Int): LiveData<List<Folder>> {
        return repository.getAllFolders(query)
    }

    fun getAllNotesByOldest(query: Int): LiveData<List<Note>> {
        return repository.allNotesByOldest(query)
    }

    fun getAllNotesByNewest(query: Int): LiveData<List<Note>> {
        return repository.allNotesByNewest(query)
    }

    fun getAllNotesByColor(query: Int): LiveData<List<Note>> {
        return repository.allNotesByColor(query)
    }

    fun getUnreferencedFolders(query: List<Int>): Int? {
        return repository.getUnreferencedFolders(query)
    }

    fun getAllFolderIds(): LiveData<List<Int>> {
        return repository.getAllFolderIds()
    }

}