package com.appsrandom.minimalism.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appsrandom.minimalism.activities.FolderActivity
import com.appsrandom.minimalism.activities.MainActivity
import com.appsrandom.minimalism.adapters.RVNotesAdapter
import com.appsrandom.minimalism.databinding.FragmentSearchBinding
import com.appsrandom.minimalism.viewModel.NoteViewModel
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val noteViewModel: NoteViewModel by activityViewModels()
    private lateinit var rvNotesAdapter: RVNotesAdapter
    private lateinit var sharedPreferencesView: SharedPreferences
    private var bundle: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        bundle = arguments
        if (bundle != null) {
            (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE
        } else {
            (activity as FolderActivity).binding.searchContainerParent.visibility = View.VISIBLE
            (activity as FolderActivity).binding.addNoteParent.visibility = View.GONE
        }

        sharedPreferencesView = activity?.getSharedPreferences("sharedPrefsView", 0) as SharedPreferences

        recyclerViewDisplay()

        binding.search.requestFocus()
        val imgr = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Handler(Looper.getMainLooper()).postDelayed({
            imgr.showSoftInput(binding.search, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().isNotEmpty()) {
                    val text = s.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        noteViewModel.searchNote(query).observe(viewLifecycleOwner) {
                            rvNotesAdapter.submitList(it)
                        }
                    }
                } else {
                    rvNotesAdapter.submitList(listOf())
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        return binding.root
    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                setUpRecyclerView(2)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                setUpRecyclerView(3)
            }
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.rvNote.apply {
            val whichView = sharedPreferencesView.getString("view", "0")
            layoutManager = when (whichView) {
                "list" -> {
                    LinearLayoutManager(requireContext())
                }

                "grid" -> {
                    StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                }

                else -> {
                    StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                }
            }
//            setHasFixedSize(true)
            rvNotesAdapter = RVNotesAdapter()
            rvNotesAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = rvNotesAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
//        observerDataChanges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (bundle == null) {
            (activity as FolderActivity).binding.searchContainerParent.visibility = View.GONE
            (activity as FolderActivity).binding.addNoteParent.visibility = View.VISIBLE
        }
    }
}