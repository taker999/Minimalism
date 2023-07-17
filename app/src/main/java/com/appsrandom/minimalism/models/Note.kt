package com.appsrandom.minimalism.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes_table")
class Note(
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "content")val content: String,
    @ColumnInfo(name = "date")val date:String,
    @ColumnInfo(name = "color")val color: Int = -1,
    @PrimaryKey(autoGenerate = true)var id: Int = 0): Serializable {

}