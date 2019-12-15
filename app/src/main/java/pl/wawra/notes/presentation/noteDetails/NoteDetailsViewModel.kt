package pl.wawra.notes.presentation.noteDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg


class NoteDetailsViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    val note: MutableLiveData<Note> = MutableLiveData()

    fun getNote(noteId: Long) {
        onBg {
            note.postValue(noteDao.getNoteById(noteId))
        }
    }

}