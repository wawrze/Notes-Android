package pl.wawra.notes.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.wawra.notes.database.entities.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY isDone, date")
    fun getNotes(): List<Note>

    @Query("SELECT * FROM note WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Note?

    @Query("DELETE FROM note WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM note")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notes: List<Note>)

}