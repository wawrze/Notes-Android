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
        note.isDone = !note.isDone
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

    fun deleteNote(noteToDelete: Note) {
        onBg {
            noteDao.deleteById(noteToDelete.id)
            getNotes()
        }
    }

    fun restoreNotes(noteToRestore: Note) {
        onBg {
            noteDao.insert(noteToRestore)
            getNotes()
        }
    }

}