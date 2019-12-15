package pl.wawra.notes.presentation.notes

import pl.wawra.notes.database.entities.Note

interface NotesActions {

    fun onCheckedClicked(note: Note)
    fun onNoteClicked(note: Note)
    fun onDeleteClicked(note: Note)

}