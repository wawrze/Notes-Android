package pl.wawra.notes.presentation.notes

import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_note.view.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseAdapter
import pl.wawra.notes.database.entities.Note

class NotesAdapter(private val actions: NotesActions) :
    BaseAdapter<Note, NotesAdapter.NoteViewHolder>() {

    override fun onBindViewHolder(holder: NoteViewHolder, item: Note) {
        holder.itemView.apply {
            item_note_done_button.setOnClickListener { actions.onCheckedClicked(item) }
            item_note_text.setOnClickListener { actions.onNoteBodyClicked(item) }
            item_note_text.text = item.title
            if (item.isChecked) {
                item_note_text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                item_note_text.paintFlags = Typeface.NORMAL
            }
            item_note_delete_button.setOnClickListener {
                actions.onDeleteClicked(item)
            }
            item_note_date.text = "" // TODO: set note date and time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(inflate(parent, R.layout.item_note))

    class NoteViewHolder(itemView: View) : ViewHolder(itemView)

}