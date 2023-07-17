package com.appsrandom.minimalism.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appsrandom.minimalism.activities.CreateOrEditNoteActivity
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.databinding.NoteItemBinding
import com.appsrandom.minimalism.models.Note
import com.appsrandom.minimalism.utils.DiffUtilCallback
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class RVNotesAdapter: ListAdapter<Note, RVNotesAdapter.NotesViewHolder>(DiffUtilCallback()) {
    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NoteItemBinding.bind(itemView)
        val title = binding.noteItemTitle
        val content = binding.noteContentItem
        val date = binding.noteDate
        val parent = binding.noteItemLayoutParent
        val markWon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java) {visitor, _ ->
                        visitor.forceNewLine()
                    }
                }
            })
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let {note ->
            holder.apply {
                title.text = note.title
                markWon.setMarkdown(content, note.content)

                date.text = note.date
                val sharedPreferences = parent.context.getSharedPreferences("sharedPrefs",
                    AppCompatActivity.MODE_PRIVATE
                )
                val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

                if (isDarkModeOn as Boolean && note.color == -1) {
                    parent.setCardBackgroundColor(-16777216)
                } else {
                    parent.setCardBackgroundColor(note.color)
                }

                parent.setOnClickListener {
                    val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("date", note.date)
                    intent.putExtra("color", note.color)
                    intent.putExtra("id", note.id)
                    parent.context.startActivity(intent)
                }

                title.setOnClickListener {
                    val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("date", note.date)
                    intent.putExtra("color", note.color)
                    intent.putExtra("id", note.id)
                    parent.context.startActivity(intent)
                }

                content.setOnClickListener {
                    val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("date", note.date)
                    intent.putExtra("color", note.color)
                    intent.putExtra("id", note.id)
                    parent.context.startActivity(intent)
                }

                date.setOnClickListener {
                    val intent = Intent(parent.context, CreateOrEditNoteActivity::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("date", note.date)
                    intent.putExtra("color", note.color)
                    intent.putExtra("id", note.id)
                    parent.context.startActivity(intent)
                }
            }
        }
    }
}