package pl.wawra.notes.presentation.noteDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.android.synthetic.main.fragment_note_details.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseFragment
import pl.wawra.notes.calendar.CalendarClient
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.longToDate
import pl.wawra.notes.utils.onBg
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

    private fun bindNote(note: Note) {
        fragment_note_details_title.text = note.title
        fragment_note_details_body.text = note.body
        fragment_note_details_date_and_time.text = dateFormat.format(longToDate(note.date))
        if (note.isProtected) {
            fragment_note_details_protected_image.visibility = View.VISIBLE
            fragment_note_details_protect_label.visibility = View.VISIBLE
        }
        if (false) { // TODO: show Google sync info
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

        onBg {
            val googleUserDao = Db.googleUserDao

            val mainCalendar = googleUserDao.getUsersMainCalendar()

            if (mainCalendar != null) {

                val newEvent = Event()
                    .setSummary("title")
                    .setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
                    .apply {
                        val date = Calendar.getInstance()
                        start = EventDateTime()
                            .setDateTime(DateTime(date.timeInMillis))
                        end = EventDateTime()
                            .setDateTime(DateTime(date.timeInMillis))
                    }


                val addConfirmation =
                    CalendarClient.instance?.Events()?.insert(mainCalendar, newEvent)?.execute()
            }

//            val status = addConfirmation?.status // "confirmed"
//            val id = addConfirmation?.id

//            CalendarClient.instance?.Events()?.delete(mainCalendar, id)?.execute()
        }
    }

}