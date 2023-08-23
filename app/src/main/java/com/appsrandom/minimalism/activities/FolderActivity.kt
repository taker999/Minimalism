package com.appsrandom.minimalism.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.adapters.RVFoldersAdapter
import com.appsrandom.minimalism.adapters.RVNotesAdapter
import com.appsrandom.minimalism.databinding.ActivityFolderBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import java.util.concurrent.TimeUnit

class FolderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderBinding
    private lateinit var rvNotesAdapter: RVNotesAdapter
    private lateinit var noteViewModel: NoteViewModel
    private var folderName: String? = "folder"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        folderName = intent.getStringExtra("folderName")

        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]

        recyclerViewDisplay()

        binding.viewFab.setOnClickListener {
            val intent = Intent(this, CreateOrEditNoteActivity::class.java)
            intent.putExtra("folderName", folderName)
            startActivity(intent)
        }
    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.rvNote.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvNotesAdapter = RVNotesAdapter()
            rvNotesAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = rvNotesAdapter
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()
    }

    private fun observerDataChanges() {
        noteViewModel.getAllNotes("0").observe(this) {list->
            binding.noData.isVisible = list.isEmpty()
            rvNotesAdapter.submitList(list)
        }
    }
}