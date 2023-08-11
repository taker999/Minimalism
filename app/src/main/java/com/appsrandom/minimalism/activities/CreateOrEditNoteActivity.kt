package com.appsrandom.minimalism.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.adapters.RVNotesAdapter
import com.appsrandom.minimalism.databinding.ActivityCreateOrEditNoteBinding
import com.appsrandom.minimalism.databinding.BottomSheetBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.models.Note
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class CreateOrEditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrEditNoteBinding
    private lateinit var rvNotesAdapter: RVNotesAdapter
    private var color = -1
    private var colorMatch = -1
    private var id = -1
    private lateinit var noteViewModel: NoteViewModel
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private var title: String? = null
    private var content: String? = null
    private var date: String? = null
    private var isLocked: String? = "0"
    private var currentPosition: Int = -1
    private lateinit var popupMenu: PopupMenu
    private lateinit var sharedPreferencesPassword: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        date = intent.getStringExtra("date")
        color = intent.getIntExtra("color", -1)
        id = intent.getIntExtra("id", -1)
        currentPosition = intent.getIntExtra("currentPosition", -1)
        isLocked = if (intent.getStringExtra("isLocked") == null) {
            "0"
        } else {
            "1"
        }

        colorMatch = color

        sharedPreferencesPassword = getSharedPreferences("sharedPrefsPattern", 0)

        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]

        rvNotesAdapter = RVNotesAdapter()

        popupMenu = PopupMenu(this, binding.popUpMenu)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.edit_note_items, popupMenu.menu)
        showMenu()

        if (title != null) {
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

            if (isDarkModeOn as Boolean && color == -1) {
                color = -16777216
            }
            binding.apply {
                etTitle.setText(title)
                etNoteContent.setText(content.toString())
                lastEdited.text = getString(R.string.edited_on, date)
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)

            }
            this.window.statusBarColor = color
        } else {
            binding.lastEdited.text = getString(R.string.edited_on, SimpleDateFormat.getDateInstance().format(Date()))
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnBackPressed()
                }
            })

        binding.backButton.setOnClickListener {
            saveNote()
        }

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
        saveNote()
    }

    private fun saveNote() {
        if (binding.etTitle.text.toString().isBlank() && binding.etNoteContent.text.toString().isBlank()) {
            finish()
        } else if (binding.etTitle.text.toString().isBlank()) {
            when (title) {
                null -> {
                    noteViewModel.insertNote(Note("", binding.etNoteContent.text.toString(), currentDate, color))
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (content != binding.etNoteContent.text.toString() || colorMatch != color) {
                        noteViewModel.updateNote(Note("", binding.etNoteContent.text.toString(), currentDate, color, isLocked, id))
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        } else if (binding.etNoteContent.text.toString().isBlank()) {
            when (title) {
                null -> {
                    noteViewModel.insertNote(Note(binding.etTitle.text.toString(), "", currentDate, color))
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (title != binding.etTitle.text.toString() || colorMatch != color){
                        noteViewModel.updateNote(Note(binding.etTitle.text.toString(), "", currentDate, color, isLocked, id))
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        } else {
            when (title) {
                null -> {
                    noteViewModel.insertNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.text.toString(), currentDate, color))
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (title != binding.etTitle.text.toString() || content != binding.etNoteContent.text.toString() || colorMatch != color) {
                        noteViewModel.updateNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.text.toString(), currentDate, color, isLocked, id))
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        }
    }

    private fun createPdf() {
        if (binding.etTitle.text.toString().isBlank() && binding.etNoteContent.text.toString().isBlank()) {
            Toast.makeText(this, "Can't print empty note...", Toast.LENGTH_SHORT).show()
        } else {
            val printTitle = binding.etTitle.text.toString().trim()
            val printContent = binding.etNoteContent.text.toString().trim()



            val html = """<!DOCTYPE html>
<html>
<head>
	<title></title>
	<link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
</head>
<body>

<h1 style="font-size: xx-large">$printTitle</h1>
<p style="font-size: x-large">$printContent</p>

</body>

</html>"""
            binding.webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

            val printManager = this@CreateOrEditNoteActivity.getSystemService(PRINT_SERVICE) as PrintManager
            val adapter: PrintDocumentAdapter?
            val jobName = getString(R.string.app_name) + "Document"
            adapter=binding.webView.createPrintDocumentAdapter(jobName)
            printManager.print(
                jobName,
                adapter, PrintAttributes.Builder().build()
            )
        }
    }

    private fun showMenu() {
//        popupMenu = PopupMenu(this, binding.popUpMenu)
//        val inflater: MenuInflater = popupMenu.menuInflater
//        inflater.inflate(R.menu.edit_note_items, popupMenu.menu)
        if (isLocked == "0") {
            popupMenu.menu.findItem(R.id.lock).setIcon(R.drawable.ic_lock).title = "Lock"
        } else {
            popupMenu.menu.findItem(R.id.lock).setIcon(R.drawable.ic_unlock).title = "Unlock"
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.save -> {
                    saveNote()
                }
                R.id.print -> {
                    createPdf()
                }
                R.id.share -> {
                    shareNote()
                }
                R.id.delete -> {
                    deleteNote()
                }
                R.id.lock -> {
                    if (title == null) {
                        lockNoteOnCreatingNote()
                    } else if (isLocked == "0") {
                        lockNote()
                    } else {
                        Toast.makeText(this, "Unlocked", Toast.LENGTH_SHORT).show()
                        isLocked = "0"
                        noteViewModel.updateNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.text.toString(), currentDate, color, isLocked, id))
                        popupMenu.menu.findItem(R.id.lock).setIcon(R.drawable.ic_lock).title = "Lock"
                    }
                }
            }
            true
        }
        binding.popUpMenu.setOnClickListener {
            try {
                val popUp = PopupMenu::class.java.getDeclaredField("mPopup")
                popUp.isAccessible = true
                val menu = popUp.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }
    }

    private fun lockNoteOnCreatingNote() {
        val isPasswordSet = sharedPreferencesPassword.getString("password", "0")
        if (isPasswordSet == "0") {
            startActivity(Intent(this, CreatePasswordActivity::class.java))
        } else {
            if (binding.etTitle.text.toString().isBlank() && binding.etNoteContent.text.toString().isBlank()) {
                finish()
            } else if (binding.etTitle.text.toString().isBlank()) {
                noteViewModel.insertNote(Note("", binding.etNoteContent.text.toString(), currentDate, color, "1"))
                Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show()
            } else if (binding.etNoteContent.text.toString().isBlank()) {
                noteViewModel.insertNote(Note(binding.etTitle.text.toString(), "", currentDate, color, "1"))
                Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show()
            } else {
                noteViewModel.insertNote(Note(binding.etTitle.text.toString(), binding.etNoteContent.text.toString(), currentDate, color, "1"))
                Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun lockNote() {
        val isPasswordSet = sharedPreferencesPassword.getString("password", "0")
        if (isPasswordSet == "0") {
            startActivity(Intent(this, CreatePasswordActivity::class.java))
        } else {
            Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show()
            isLocked = "1"
            popupMenu.menu.findItem(R.id.lock).setIcon(R.drawable.ic_unlock).title = "Unlock"
            noteViewModel.updateNote(Note(title.toString(), content.toString(), date.toString(), color, isLocked, id))
        }
    }

    private fun shareNote() {
        if (binding.etTitle.text.toString().isBlank() && binding.etNoteContent.text.toString().isBlank()) {
            Toast.makeText(this, "Can't share empty note...", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Minimalism")
                val shareTitle = binding.etTitle.text.toString().trim()
                val shareContent = binding.etNoteContent.text.toString().trim()
                val shareMessage = (shareTitle+"\n"+shareContent).trim()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one..."))
            }
        }
    }

    private fun deleteNote() {

        if (title == null) {
            finish()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Note")
            builder.setMessage("Are you sure you want to delete this note permanently?")
            builder.setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    noteViewModel.deleteNote(Note("", "", "", -1, isLocked, id))
                    finish()
                }
                .setNegativeButton("No") { _, _ ->
                    // User cancelled the dialog
                }
            // Create the AlertDialog object and return it
            builder.show()
        }
    }
}