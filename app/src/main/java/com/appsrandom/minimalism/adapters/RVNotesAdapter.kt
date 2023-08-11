package com.appsrandom.minimalism.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appsrandom.minimalism.activities.CreateOrEditNoteActivity
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.activities.InputPasswordActivity
import com.appsrandom.minimalism.databinding.NoteItemBinding
import com.appsrandom.minimalism.models.Note
import com.appsrandom.minimalism.utils.DiffUtilCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RVNotesAdapter: ListAdapter<Note, RVNotesAdapter.NotesViewHolder>(DiffUtilCallback()) {
    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NoteItemBinding.bind(itemView)
        val title = binding.noteItemTitle
        val content = binding.noteContentItem
        val date = binding.noteDate
        val parent = binding.noteItemLayoutParent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let {note ->
            holder.apply {
                val isLocked = note.isLocked
                val titleTemp = note.title
                if (titleTemp == "") {
                    title.text = "Untitled"
                    title.setTextColor(ContextCompat.getColor(parent.context, R.color.untitled_black))
                } else {
                    title.text = note.title
                    title.setTextColor(ContextCompat.getColor(parent.context, R.color.black))
                }

                val sharedPreferencesPassword = parent.context.getSharedPreferences("sharedPrefsPattern", 0)

                val isPasswordSet = sharedPreferencesPassword?.getString("password", "0")

                if (isLocked != "0" && isPasswordSet != "0") {
                    val contentTemp = note.content
                    val stringBuilder = StringBuilder(contentTemp)
                    for (i in contentTemp.indices) {
                        stringBuilder[i] = '*'
                    }
                    content.text = stringBuilder
                } else {
                    content.text = note.content
                }

                date.text = note.date
                var color = note.color
                CoroutineScope(Dispatchers.IO).launch {
                    val sharedPreferences = parent.context.getSharedPreferences("sharedPrefs",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

                    withContext(Dispatchers.Main) {
                        if (isDarkModeOn as Boolean && color == -1) {
                            color = -16777216
                            parent.setCardBackgroundColor(-16777216)
                        } else if (!isDarkModeOn && color == -16777216) {
                            color = -1
                            parent.setCardBackgroundColor(-1)
                        } else {
                            parent.setCardBackgroundColor(color)
                        }
                    }
                }

                parent.setOnClickListener {
                    parent.isClickable = false
                    title.isClickable = false
                    content.isClickable = false
                    date.isClickable = false
                    if (isLocked != "0" && isPasswordSet != "0") {
                        val intent = Intent(parent.context, InputPasswordActivity::class.java)
                        intent.putExtra("whichActivity", "EditNote")
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    } else {
                        val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    }

                }

                title.setOnClickListener {
                    parent.isClickable = false
                    title.isClickable = false
                    content.isClickable = false
                    date.isClickable = false
                    if (isLocked != "0" && isPasswordSet != "0") {
                        val intent = Intent(parent.context, InputPasswordActivity::class.java)
                        intent.putExtra("whichActivity", "EditNote")
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    } else {
                        val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    }
                }

                content.setOnClickListener {
                    parent.isClickable = false
                    title.isClickable = false
                    content.isClickable = false
                    date.isClickable = false
                    if (isLocked != "0" && isPasswordSet != "0") {
                        val intent = Intent(parent.context, InputPasswordActivity::class.java)
                        intent.putExtra("whichActivity", "EditNote")
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    } else {
                        val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    }
                }

                date.setOnClickListener {
                    parent.isClickable = false
                    title.isClickable = false
                    content.isClickable = false
                    date.isClickable = false
                    if (isLocked != "0" && isPasswordSet != "0") {
                        val intent = Intent(parent.context, InputPasswordActivity::class.java)
                        intent.putExtra("whichActivity", "EditNote")
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    } else {
                        val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                        intent.putExtra("title", note.title)
                        intent.putExtra("content", note.content)
                        intent.putExtra("date", note.date)
                        intent.putExtra("color", color)
                        intent.putExtra("id", note.id)
                        parent.context.startActivity(intent)
                    }
                }
            }
        }
    }
}