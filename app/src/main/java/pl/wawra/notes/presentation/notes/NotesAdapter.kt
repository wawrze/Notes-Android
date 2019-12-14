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

    private var deleteMode = false
    val toRemove = HashSet<Note>()

    fun setDeleteMode(isDeleteMode: Boolean) {
        deleteMode = isDeleteMode
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NoteViewHolder, item: Note) {
        holder.itemView.apply {
            item_note_check_button.setOnClickListener { actions.onCheckedClicked(item) }
            item_note_text.setOnClickListener { actions.onNoteBodyClicked(item) }
            item_note_text.text = item.body
            if (item.isChecked) {
                item_note_text.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                item_note_text.paintFlags = Typeface.NORMAL
            }
            item_note_checkbox.visibility = if (deleteMode) View.VISIBLE else View.INVISIBLE
            item_note_check_button.visibility = if (deleteMode) View.INVISIBLE else View.VISIBLE
            item_note_checkbox.isChecked = toRemove.contains(item)
            item_note_checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    toRemove.add(item)
                } else {
                    toRemove.remove(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(inflate(parent, R.layout.item_note))

    class NoteViewHolder(itemView: View) : ViewHolder(itemView)

}