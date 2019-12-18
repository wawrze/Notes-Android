package pl.wawra.notes.presentation.notes

import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId

interface NotesActions {

    fun onCheckedClicked(note: NoteWithCalendarEventId)
    fun onNoteClicked(note: NoteWithCalendarEventId)
    fun onDeleteClicked(note: NoteWithCalendarEventId)

}