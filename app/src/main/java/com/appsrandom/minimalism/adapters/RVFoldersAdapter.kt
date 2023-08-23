package com.appsrandom.minimalism.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.activities.FolderActivity
import com.appsrandom.minimalism.databinding.FolderItemBinding
import com.appsrandom.minimalism.models.Folder

class RVFoldersAdapter: ListAdapter<Folder, RVFoldersAdapter.FoldersViewHolder>(DiffUtilCallback()) {

    inner class FoldersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = FolderItemBinding.bind(itemView)
        var folderName = binding.folderTitle
        val parent = binding.folderParentLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolder {
        return FoldersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false))
    }

    override fun onBindViewHolder(holder: FoldersViewHolder, position: Int) {
        getItem(position).let {folder ->
            holder.folderName.text = folder.folderName
            holder.parent.setOnClickListener {
                val intent = Intent(holder.parent.context, FolderActivity::class.java)
                intent.putExtra("folderName", "folder")
                holder.parent.context.startActivity(intent)
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

}