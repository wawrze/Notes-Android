package pl.wawra.notes.presentation.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import pl.wawra.notes.calendar.CalendarClient
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.CalendarEvent
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId
import pl.wawra.notes.utils.onBg

class NotesViewModel : ViewModel() {

    private var noteDao = Db.noteDao
    private val googleUserDao = Db.googleUserDao
    private val calendarEventDao = Db.calendarEventDao

    val notesList: MutableLiveData<List<NoteWithCalendarEventId>> = MutableLiveData()

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
                // TODO: show progress bar
                googleUserDao.getUser()?.mainCalendar?.let {
                    CalendarClient.instance
                        ?.Events()
                        ?.delete(it, noteToDelete.calendarEventId)
                        ?.execute()
                }
                // TODO: hide progress bar
            }
            getNotes()
        }
    }

    fun restoreNotes(noteToRestore: NoteWithCalendarEventId) {
        onBg {
            noteDao.insert(Note().apply {
                id = noteToRestore.id
                title = noteToRestore.title
                body = noteToRestore.body
                date = noteToRestore.date
                isDone = noteToRestore.isDone
                isProtected = noteToRestore.isProtected
            })
            getNotes()
            // TODO: show progress bar
            val user = googleUserDao.getUser()
            if (user?.mainCalendar != null) {
                val newEvent = Event()
                    .setSummary(noteToRestore.title)
                    .setDescription(noteToRestore.body)
                    .apply {
                        start = EventDateTime()
                            .setDateTime(DateTime(noteToRestore.date))
                        end = EventDateTime()
                            .setDateTime(DateTime(noteToRestore.date))
                    }
                val confirmation = CalendarClient.instance
                    ?.Events()
                    ?.insert(user.mainCalendar, newEvent)
                    ?.execute()

                // TODO: hide progress bar

                if (confirmation?.id != null) {
                    calendarEventDao.insert(
                        CalendarEvent().apply {
                            id = confirmation.id
                            noteId = noteToRestore.id
                            googleUser = user.mainCalendar
                        }
                    )
                    // TODO: success message
                } else {
                    // TODO: error message
                }
            } else {
                // TODO: error message
            }
        }
    }

}