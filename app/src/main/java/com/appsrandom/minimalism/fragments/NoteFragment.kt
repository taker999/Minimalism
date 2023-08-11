package com.appsrandom.minimalism.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.activities.CreateOrEditNoteActivity
import com.appsrandom.minimalism.activities.MainActivity
import com.appsrandom.minimalism.adapters.RVNotesAdapter
import com.appsrandom.minimalism.databinding.FragmentNoteBinding
import com.appsrandom.minimalism.utils.SwipeToDelete
import com.appsrandom.minimalism.utils.hideKeyboard
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var rvNotesAdapter: RVNotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireView().hideKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(layoutInflater, container, false)

        binding.innerFab.setOnClickListener {
            binding.innerFab.isClickable = false
            val intent = Intent(context, CreateOrEditNoteActivity::class.java)
            startActivity(intent)
        }

        recyclerViewDisplay()

        swipeToDelete(binding.rvNote)

        //implementing search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.noteData.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().isNotEmpty()) {
                    val text = s.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        noteViewModel.searchNote(query).observe(viewLifecycleOwner) {
                            rvNotesAdapter.submitList(it)
                        }
                    } else {
                        observerDataChanges()
                    }
                } else {
                    observerDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.search.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
                (requireActivity() as MainActivity).binding.bottomNavigationView.isVisible = !hasFocus
//            }
        }

        binding.search.setOnEditorActionListener { v, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
            }
            return@setOnEditorActionListener true
        }

        binding.rvNote.setOnTouchListener { _, _ ->
            requireView().hideKeyboard()
            binding.search.clearFocus()
            (requireActivity() as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
            return@setOnTouchListener false
        }

//        binding.rvNote.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
//
//            when {
//                scrollY > oldScrollY -> {
//                    binding.chatFabText.visibility = View.GONE
//                }
//
//                scrollX == scrollY -> {
//                    binding.chatFabText.visibility = View.VISIBLE
//                }
//                else -> {
//                    binding.chatFabText.visibility = View.VISIBLE
//                }
//            }
//
//        }

        return binding.root
    }

    private fun swipeToDelete(rvNote: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = rvNotesAdapter.currentList[position]
                var actionBtnTapped = false
                noteViewModel.deleteNote(note)
                binding.search.clearFocus()
                if (binding.search.text.toString().isEmpty()) {
                    observerDataChanges()
                }
                val snackBar = Snackbar.make(requireView(), "Note Deleted", Snackbar.LENGTH_LONG).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {

                        transientBottomBar?.setAction("UNDO") {
                            noteViewModel.insertNote(note)
                            actionBtnTapped = true
                            binding.noteData.visibility = View.GONE
                        }

                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.innerFab)
                }
                snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.yellowOrange))
                snackBar.show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNote)

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
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()
    }

    fun observerDataChanges() {
        noteViewModel.getAllNotes().observe(viewLifecycleOwner) {list->
            binding.noteData.isVisible = list.isEmpty()
            rvNotesAdapter.submitList(list)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerViewDisplay()
        binding.innerFab.isClickable = true
    }
}