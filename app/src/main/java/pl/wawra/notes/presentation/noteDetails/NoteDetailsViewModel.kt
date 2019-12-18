package pl.wawra.notes.presentation.noteDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId
import pl.wawra.notes.utils.onBg


class NoteDetailsViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    val note: MutableLiveData<NoteWithCalendarEventId> = MutableLiveData()

    fun getNote(noteId: Long) {
        onBg {
            val n = noteDao.getNoteById(noteId)
            note.postValue(n)
        }
    }

}