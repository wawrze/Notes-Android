package pl.wawra.notes.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var title: String = "",

    var body: String = "",

    var date: Long = Calendar.getInstance().timeInMillis,

    var isDone: Boolean = false,

    var isProtected: Boolean = false

)