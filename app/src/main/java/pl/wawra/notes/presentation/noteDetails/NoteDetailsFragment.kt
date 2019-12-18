package pl.wawra.notes.presentation.noteDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_note_details.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseFragment
import pl.wawra.notes.utils.longToDate
import pl.wawra.notes.utils.modelHelpers.NoteWithCalendarEventId
import java.text.SimpleDateFormat
import java.util.*


class NoteDetailsFragment : BaseFragment() {

    private lateinit var viewModel: NoteDetailsViewModel
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val args by navArgs<NoteDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(NoteDetailsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_note_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.note.observe(viewLifecycleOwner, Observer { bindNote(it) })
    }

    private fun bindNote(note: NoteWithCalendarEventId) {
        fragment_note_details_title.text = note.title
        fragment_note_details_body.text = note.body
        fragment_note_details_date_and_time.text = dateFormat.format(longToDate(note.date))
        if (note.isProtected) {
            fragment_note_details_protected_image.visibility = View.VISIBLE
            fragment_note_details_protect_label.visibility = View.VISIBLE
        }
        if (!note.calendarEventId.isNullOrBlank()) {
            fragment_note_details_google_image.visibility = View.VISIBLE
            fragment_note_details_google_label.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        setUpTopBar(
            getString(R.string.new_note),
            { navigate?.popBackStack() },
            { editNote() },
            R.drawable.ic_return,
            R.drawable.ic_edit
        )
        viewModel.getNote(args.noteId)
    }

    private fun editNote() {
        // TODO: navigate to edit note fragment
    }

}