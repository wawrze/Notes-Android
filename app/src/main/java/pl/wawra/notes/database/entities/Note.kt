package pl.wawra.notes.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var title: String = "",

    var isChecked: Boolean = false
// TODO: additional fields (body, date and time, is protected, Google sync)
)