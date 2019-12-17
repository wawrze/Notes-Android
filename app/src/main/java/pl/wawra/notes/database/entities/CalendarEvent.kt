package pl.wawra.notes.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalendarEvent(

    @PrimaryKey
    var id: String = "",

    var noteId: Long = 0L,

    var googleUser: String = ""

)