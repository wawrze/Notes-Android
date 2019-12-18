package pl.wawra.notes.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId

@Dao
interface NoteDao {

    @Query(
        """
        SELECT
            n.*,
            c.id AS calendarEventId
        FROM note n
            LEFT JOIN calendarevent c
                ON n.id = c.noteId
                    AND c.googleUser = (SELECT accountName FROM googleuser LIMIT 1)
        ORDER BY isDone, date
        """
    )
    fun getNotes(): List<NoteWithCalendarEventId>

    @Query(
        """
        SELECT
            n.*,
            c.id AS calendarEventId
        FROM note n
            LEFT JOIN calendarevent c
                ON n.id = c.noteId
                    AND c.googleUser = (SELECT accountName FROM googleuser LIMIT 1)
        WHERE n.id = :id
        LIMIT 1
        """
    )
    fun getNoteById(id: Long): NoteWithCalendarEventId?

    @Query("UPDATE note SET isDone = :isDone WHERE id = :noteId")
    fun updateNoteDone(noteId: Long, isDone: Boolean)

    @Query("DELETE FROM note WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM note")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notes: List<Note>)

}