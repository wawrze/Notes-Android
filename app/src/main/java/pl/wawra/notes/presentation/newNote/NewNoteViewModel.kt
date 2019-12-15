package pl.wawra.notes.presentation.newNote

import androidx.lifecycle.ViewModel
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg
import java.util.*

class NewNoteViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    fun addNote(
        title: String,
        body: String,
        date: Calendar,
        isProtected: Boolean,
        toSync: Boolean
    ) {
        onBg {
            noteDao.insert(
                Note(
                    title = title,
                    body = body,
                    date = date.timeInMillis,
                    isProtected = isProtected
                )
            )
            if (toSync) {
                // TODO: send note to Google calendar
            }
        }
    }

}