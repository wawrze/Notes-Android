package pl.wawra.notes.presentation.newNote

import androidx.lifecycle.ViewModel
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg

class NewNoteViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    fun addNote(noteBody: String) {
        onBg {
            noteDao.insert(Note(title = noteBody))
            // TODO: if note is to sync with Google - send it to calendar
        }
    }

}