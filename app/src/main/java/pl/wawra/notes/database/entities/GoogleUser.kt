package pl.wawra.notes.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GoogleUser(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var token: String = "",

    var accountName: String = "",

    var mainCalendar: String = ""

)