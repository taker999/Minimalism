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

    fun getAllFolders(query: String): LiveData<List<Folder>> {
        return repository.getAllFolders(query)
    }

    fun getAllNotesByOldest(): LiveData<List<Note>> {
        return repository.allNotesByOldest()
    }

    fun getAllNotesByNewest(): LiveData<List<Note>> {
        return repository.allNotesByNewest()
    }

    fun getAllNotesByColor(): LiveData<List<Note>> {
        return repository.allNotesByColor()
    }

}