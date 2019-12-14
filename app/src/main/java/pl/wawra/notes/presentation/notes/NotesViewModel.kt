package pl.wawra.notes.presentation.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg

class NotesViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    val notesList: MutableLiveData<List<Note>> = MutableLiveData()

    fun changeNoteChecked(note: Note) {
        note.isChecked = !note.isChecked
        onBg {
            noteDao.insert(note)
            getNotes()
        }
    }

    fun getNotes() {
        onBg {
            val notes = noteDao.getNotes()
            notesList.postValue(notes)
        }
    }

    fun deleteNotes(notesToDelete: List<Note>) {
        onBg {
            noteDao.deleteByIds(notesToDelete.map { it.id }.toList())
            getNotes()
        }
    }

    fun restoreNotes(notesToRestore: List<Note>) {
        onBg {
            noteDao.insert(notesToRestore.toList())
            getNotes()
        }
    }

}