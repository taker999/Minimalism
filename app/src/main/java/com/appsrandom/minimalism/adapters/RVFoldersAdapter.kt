package com.appsrandom.minimalism.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.activities.FolderActivity
import com.appsrandom.minimalism.activities.MainActivity
import com.appsrandom.minimalism.databinding.FolderItemBinding
import com.appsrandom.minimalism.fragments.NoteFragment
import com.appsrandom.minimalism.fragments.SettingsFragment
import com.appsrandom.minimalism.models.Folder
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.thebluealliance.spectrum.SpectrumPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RVFoldersAdapter: ListAdapter<Folder, RVFoldersAdapter.FoldersViewHolder>(DiffUtilCallback()) {

    private lateinit var activityContext: Context
    private val items: ArrayList<Folder> = ArrayList()
    private var folderColor = -1
    private var fName = ""
    private var dataClickListener: DataClickListener? = null
    private lateinit var menu: Menu
    private lateinit var bottomNavEditFolder: BottomNavigationView

    fun setDataPassListener(listener: DataClickListener) {
        dataClickListener = listener
    }

    inner class FoldersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = FolderItemBinding.bind(itemView)
        var folderName = binding.folderTitle
        val tickIcon = binding.tick
        val parent = binding.folderParentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolder {
        activityContext = parent.context
        return FoldersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false))
    }

    override fun onBindViewHolder(holder: FoldersViewHolder, position: Int) {
        getItem(position).let {folder ->
            holder.folderName.text = folder.folderName
            fName = folder.folderName

            folderColor = folder.folderColor

            val sharedPreferences = holder.parent.context.getSharedPreferences("sharedPrefs",
                AppCompatActivity.MODE_PRIVATE
            )
            val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

            if (isDarkModeOn as Boolean && folderColor == -1) {
                folderColor = -16777216
                holder.parent.setCardBackgroundColor(folderColor)
            } else if (!isDarkModeOn && folderColor == -16777216) {
                folderColor = -1
                holder.parent.setCardBackgroundColor(folderColor)
            } else {
                holder.parent.setCardBackgroundColor(folderColor)
            }

            try {
                menu = (activityContext as MainActivity).binding.bottomNavigationViewEditFolder.menu
                bottomNavEditFolder = (activityContext as MainActivity).binding.bottomNavigationViewEditFolder
            } catch (_: Exception) {
                menu = (activityContext as FolderActivity).binding.bottomNavigationViewEditFolder.menu
                bottomNavEditFolder = (activityContext as FolderActivity).binding.bottomNavigationViewEditFolder
            }

            holder.parent.setOnLongClickListener {
                folder.isSelected = !folder.isSelected
                if (folder.isSelected) {
                    items.add(folder)
                    bottomNavEditFolder.visibility = View.VISIBLE
                    holder.tickIcon.visibility = View.VISIBLE
                    menu.findItem(R.id.navNote).isVisible = false
                    menu.findItem(R.id.navSettings).isVisible = false
                    if (items.size == 1) {
                        menu.findItem(R.id.navEdit).isVisible = true
                        menu.findItem(R.id.navDelete).isVisible = true
                    } else {
                        menu.findItem(R.id.navEdit).isVisible = false
                        menu.findItem(R.id.navDelete).isVisible = true
                    }
                } else {
                    holder.tickIcon.visibility = View.GONE
                    items.remove(folder)
                    if (items.size == 1) {
                        bottomNavEditFolder.visibility = View.VISIBLE
                        menu.findItem(R.id.navEdit).isVisible = true
                        menu.findItem(R.id.navDelete).isVisible = true
                    } else {
                        bottomNavEditFolder.visibility = View.VISIBLE
                        menu.findItem(R.id.navEdit).isVisible = false
                        menu.findItem(R.id.navDelete).isVisible = true
                    }
                    if (items.isEmpty()) {
                        bottomNavEditFolder.visibility = View.GONE
                        menu.findItem(R.id.navNote).isVisible = true
                        menu.findItem(R.id.navSettings).isVisible = true
                        menu.findItem(R.id.navEdit).isVisible = false
                        menu.findItem(R.id.navDelete).isVisible = false
                    }
                }
                dataClickListener?.onDataItemClicked(items)
                return@setOnLongClickListener true
            }

            holder.parent.setOnClickListener {
                if (items.isEmpty()) {
                    val intent = Intent(holder.parent.context, FolderActivity::class.java)
                    intent.putExtra("folderId", folder.id)
                    intent.putExtra("folderName", folder.folderName)
                    intent.putExtra("folderColor", folderColor)
                    holder.parent.context.startActivity(intent)
                } else {
                    folder.isSelected = !folder.isSelected
                    if (folder.isSelected) {
                        items.add(folder)
                        bottomNavEditFolder.visibility = View.VISIBLE
                        holder.tickIcon.visibility = View.VISIBLE
                        menu.findItem(R.id.navNote).isVisible = false
                        menu.findItem(R.id.navSettings).isVisible = false
                        if (items.size == 1) {
                            menu.findItem(R.id.navEdit).isVisible = true
                            menu.findItem(R.id.navDelete).isVisible = true
                        } else {
                            menu.findItem(R.id.navEdit).isVisible = false
                            menu.findItem(R.id.navDelete).isVisible = true
                        }
                    } else {
                        holder.tickIcon.visibility = View.GONE
                        items.remove(folder)
                        if (items.size == 1) {
                            bottomNavEditFolder.visibility = View.VISIBLE
                            menu.findItem(R.id.navEdit).isVisible = true
                            menu.findItem(R.id.navDelete).isVisible = true
                        } else {
                            bottomNavEditFolder.visibility = View.VISIBLE
                            menu.findItem(R.id.navEdit).isVisible = false
                            menu.findItem(R.id.navDelete).isVisible = true
                        }
                        if (items.isEmpty()) {
                            bottomNavEditFolder.visibility = View.GONE
                            menu.findItem(R.id.navNote).isVisible = true
                            menu.findItem(R.id.navSettings).isVisible = true
                            menu.findItem(R.id.navEdit).isVisible = false
                            menu.findItem(R.id.navDelete).isVisible = false
                        }
                    }
                }
                dataClickListener?.onDataItemClicked(items)
            }
        }

    }

    class DiffUtilCallback: DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem.id == newItem.id
        }
    }

    interface DataClickListener {
        fun onDataItemClicked(data: ArrayList<Folder>)
    }

}