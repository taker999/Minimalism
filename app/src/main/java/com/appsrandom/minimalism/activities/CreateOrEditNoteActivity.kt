package com.appsrandom.minimalism.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.adapters.RVNotesAdapter
import com.appsrandom.minimalism.databinding.ActivityCreateOrEditNoteBinding
import com.appsrandom.minimalism.databinding.BottomSheetBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.fragments.NoteFragment
import com.appsrandom.minimalism.models.Note
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.r0adkll.slidr.Slidr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class CreateOrEditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrEditNoteBinding
    private lateinit var rvNotesAdapter: RVNotesAdapter
    private var note: Note? = null
    private var color = -1
    private var colorMatch = -1
    private var id = -1
    private lateinit var noteViewModel: NoteViewModel
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private var obj: ArrayList<Any> = ArrayList()
    private var title: String? = null
    private var content: String? = null
    private var date: String? = null
    private var result: String? = null
    private var currentPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Slidr.attach(this)

        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]

        rvNotesAdapter = RVNotesAdapter()

        showMenu()

//        args = intent.getBundleExtra("notes")
//        obj = args?.getSerializable("ARRAYLIST") as ArrayList<Any>

        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        date = intent.getStringExtra("date")
        color = intent.getIntExtra("color", -1)
        id = intent.getIntExtra("id", -1)
        currentPosition = intent.getIntExtra("currentPosition", -1)

        colorMatch = color

        val html = """<!DOCTYPE html>
<html>
<head>
	<title></title>
	<link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
</head>
<body>

<h1 style="font-size: xx-large">$title</h1>
<p style="font-size: x-large">$content</p>

</body>

</html>"""
        binding.webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        if (title != null) {
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

            if (isDarkModeOn as Boolean && color == -1) {
                color = -16777216
            }
            binding.apply {
                etTitle.setText(title)
                etNoteContent.renderMD(content.toString())
                lastEdited.text = getString(R.string.edited_on, date)
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)

            }
            this.window.statusBarColor = color
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnBackPressed()
                }
            })

        binding.backButton.setOnClickListener {
            if (binding.etTitle.text.toString().isNotBlank() && binding.etNoteContent.getMD().isNotBlank()) {
                when (title) {
                    null -> {
                        noteViewModel.insertNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color))
                        finish()
                    }
                    else -> {
                        if (binding.etTitle.text.toString() == title && binding.etNoteContent.getMD() == content && colorMatch == color) {
                            finish()
                        } else {
                            noteViewModel.updateNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color, id))
                            finish()
                        }

                    }
                }
            } else {
                finish()
            }
        }

//        binding.saveNote.setOnClickListener {
//            saveNote()
//            createPdf()
//        }

//        try {
//            binding.etNoteContent.setOnFocusChangeListener { _, hasFocus ->
//                if (hasFocus) {
//                    binding.bottomBar.visibility = View.VISIBLE
//                    binding.etNoteContent.setStylesBar(binding.styleBar)
//                } else {
//                    binding.bottomBar.visibility = View.GONE
//                }
//            }
//        } catch (_: Exception) {
//
//        }

        binding.lastEdited.text = getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))

        binding.fabColorPic.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
            val bottomSheetView: View = layoutInflater.inflate(R.layout.bottom_sheet, null)
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }
            val bottomSheetBinding = BottomSheetBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener {
                        color = it
                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            this@CreateOrEditNoteActivity.window.statusBarColor = color
                        }
                        bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun exitOnBackPressed() {
        if (binding.etTitle.text.toString().isNotBlank() && binding.etNoteContent.getMD().isNotBlank()) {
            when (title) {
                null -> {
                    noteViewModel.insertNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color))
                    finish()
                }
                else -> {
                    if (binding.etTitle.text.toString() == title && binding.etNoteContent.getMD() == content && colorMatch == color) {
                        finish()
                    } else {
                        noteViewModel.updateNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color, id))
                        finish()
                    }

                }
            }
        } else {
            finish()
        }
    }

    private fun saveNote() {
        if (binding.etTitle.text.toString().isBlank() || binding.etNoteContent.getMD().isBlank()) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
        } else {
            when (title) {
                null -> {
                    noteViewModel.insertNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color))
                    finish()
                }
                else -> {
                    noteViewModel.updateNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.getMD(), currentDate, color, id))
                    finish()
                }
            }
        }
    }

    private fun createPdf() {
        val printManager = this@CreateOrEditNoteActivity.getSystemService(PRINT_SERVICE) as PrintManager
        val adapter: PrintDocumentAdapter?
        val jobName = getString(R.string.app_name) + "Document"
        adapter=binding.webView.createPrintDocumentAdapter(jobName)
        printManager.print(
            jobName,
            adapter, PrintAttributes.Builder().build()
        )
    }

    private fun showMenu() {
        val popUpMenu = PopupMenu(this, binding.popUpMenu)
        val inflater: MenuInflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.edit_note_items, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.save -> {
                    saveNote()
                }
                R.id.print -> {
                    createPdf()
                }
                R.id.share -> {

                }
                R.id.delete -> {
                    deleteNote(currentPosition)
                }
            }
            true
        }
        binding.popUpMenu.setOnClickListener {
            try {
                val popUp = PopupMenu::class.java.getDeclaredField("mPopup")
                popUp.isAccessible = true
                val menu = popUp.get(popUpMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popUpMenu.show()
            }
        }
    }

    private fun deleteNote(currentPosition: Int) {
        noteViewModel.deleteNote(Note("", "", "", -1, id))
        var actionBtnTapped = false
        val snackBar = Snackbar.make(binding.noteContentFragmentParent, "Note Deleted", Snackbar.LENGTH_LONG).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
            }

            override fun onShown(transientBottomBar: Snackbar?) {

                transientBottomBar?.setAction("UNDO") {
                    noteViewModel.insertNote(Note(title.toString(), content.toString(), date.toString(), color, id))
                    actionBtnTapped = true
                }

                super.onShown(transientBottomBar)
            }
        }).apply {
            animationMode = Snackbar.ANIMATION_MODE_FADE
        }
        snackBar.setActionTextColor(ContextCompat.getColor(applicationContext, R.color.yellowOrange))
        snackBar.show()

        val bundle = Bundle()
        bundle.putString("edttext", "From Activity")
// set Fragmentclass Arguments
// set Fragmentclass Arguments
        val fragobj = NoteFragment()
        fragobj.arguments = bundle
        finish()
    }
}