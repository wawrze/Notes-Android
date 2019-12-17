package pl.wawra.notes.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.wawra.notes.database.entities.CalendarEvent

@Dao
interface CalendarEventDao {

    @Query("SELECT id FROM calendarevent WHERE noteId = :noteId AND googleUser = :googleUser LIMIT 1")
    fun getEventId(noteId: Long, googleUser: String): String?

    @Query("DELETE FROM calendarevent WHERE id = :id")
    fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: CalendarEvent)

}