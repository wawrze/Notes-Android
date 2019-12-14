package pl.wawra.notes.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var body: String = "",

    var isChecked: Boolean = false

)