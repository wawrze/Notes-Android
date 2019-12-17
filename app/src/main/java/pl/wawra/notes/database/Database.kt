package pl.wawra.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.wawra.notes.database.daos.CalendarEventDao
import pl.wawra.notes.database.daos.GoogleUserDao
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.CalendarEvent
import pl.wawra.notes.database.entities.GoogleUser
import pl.wawra.notes.database.entities.Note

@Database(
    entities = [Note::class, GoogleUser::class, CalendarEvent::class],
    version = 1,
    exportSchema = false
)
abstract class Db : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun googleUserDao(): GoogleUserDao

    abstract fun calendarEventDao(): CalendarEventDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: Db

        val noteDao get() = INSTANCE.noteDao()

        val googleUserDao get() = INSTANCE.googleUserDao()

        val calendarEventDao get() = INSTANCE.calendarEventDao()

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