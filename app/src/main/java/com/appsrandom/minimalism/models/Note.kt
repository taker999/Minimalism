package com.appsrandom.minimalism.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity(tableName = "notes_table")
data class Note(
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "content")val content: String,
    @ColumnInfo(name = "date")val date: String,
    @ColumnInfo(name = "color")val color: Int = -1,
    @ColumnInfo(name = "lock")val isLocked: String? = "0",
    @PrimaryKey(autoGenerate = true)var id: Int = 0,
    @ColumnInfo(name = "folder_id")var folderId: Int = Int.MIN_VALUE
)

@Keep
@Entity(tableName = "folders_table")
data class Folder(
    @ColumnInfo(name = "folder_name")val folderName: String,
    @ColumnInfo(name = "folder_color")val folderColor: Int = -1,
    @PrimaryKey(autoGenerate = true)var id: Int = 0,
    @ColumnInfo(name = "ref_folder_id")var refFolderId: Int = Int.MIN_VALUE,
    @ColumnInfo(name = "is_selected")var isSelected: Boolean = false
)


