package pl.wawra.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class Db : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: Db

        val noteDao get() = INSTANCE.noteDao()

        fun init(context: Context) {
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    Db::class.java,
                    "notes_database.db"
                ).build()
            }
        }

    }

}