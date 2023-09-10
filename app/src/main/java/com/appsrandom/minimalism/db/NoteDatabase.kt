package com.appsrandom.minimalism.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.models.Note


@Database(entities = [Note::class, Folder::class], version = 3, exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
                db.execSQL("ALTER TABLE notes_table ADD COLUMN lock TEXT DEFAULT '0'")
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
                db.execSQL("ALTER TABLE notes_table ADD COLUMN folder_id INT DEFAULT -2147483648")

                db.execSQL("CREATE TABLE IF NOT EXISTS folders_table (\n" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    folder_name TEXT,\n" +
                        "    folder_color INTEGER DEFAULT -1,\n" +
                        "    ref_folder_id INTEGER DEFAULT -2147483648,\n" +
                        "    is_selected INTEGER DEFAULT 0\n" +
                        ")")
            }
        }

        fun getDatabase(context: Context): NoteDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}