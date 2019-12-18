package pl.wawra.notes.presentation.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import pl.wawra.notes.R
import pl.wawra.notes.calendar.CalendarClient
import pl.wawra.notes.database.Db
import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId
import pl.wawra.notes.utils.onBg

class NotesViewModel : ViewModel() {

    private var noteDao = Db.noteDao
    private val googleUserDao = Db.googleUserDao

    val notesList: MutableLiveData<List<NoteWithCalendarEventId>> = MutableLiveData()
    val changeProgressBar = MutableLiveData<Pair<Boolean, Int>>()
    val toastMessage = MutableLiveData<Int>()

    fun changeNoteChecked(note: NoteWithCalendarEventId) {
        onBg {
            noteDao.updateNoteDone(note.id, !note.isDone)
            getNotes()
        }
    }

    fun getNotes() {
        onBg {
            val notes = noteDao.getNotes()
            notesList.postValue(notes)
        }
    }

    fun deleteNote(noteToDelete: NoteWithCalendarEventId) {
        onBg {
            noteDao.deleteById(noteToDelete.id)
            if (!noteToDelete.calendarEventId.isNullOrBlank()) {
                changeProgressBar.postValue(Pair(true, R.string.removing_note))
                googleUserDao.getUser()?.mainCalendar?.let {
                    try {
                        CalendarClient.instance
                            ?.Events()
                            ?.delete(it, noteToDelete.calendarEventId)
                            ?.execute()
                    } catch (e: GoogleJsonResponseException) {
                        toastMessage.postValue(R.string.note_deleted_sync_failed)
                    }
                }
                changeProgressBar.postValue(Pair(false, -1))
            }
            getNotes()
            toastMessage.postValue(R.string.note_deleted)
        }
    }

}