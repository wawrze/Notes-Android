package pl.wawra.notes.presentation.newNote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_new_note.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseFragment
import pl.wawra.notes.presentation.MainActivity
import java.util.*

class NewNoteFragment : BaseFragment() {

    private lateinit var viewModel: NewNoteViewModel
    private var date = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(NewNoteViewModel::class.java)
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        setTime(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE))

        fragment_new_note_date_input.setOnClickListener {
            val year = date.get(Calendar.YEAR)
            val month = date.get(Calendar.MONTH)
            val day = date.get(Calendar.DAY_OF_MONTH)

            context?.let {
                DatePickerDialog(
                    it,
                    DatePickerDialog.OnDateSetListener { _, y, m, d ->
                        setDate(y, m, d)
                    },
                    year,
                    month,
                    day
                ).show()
            }
        }
        fragment_new_note_time_input.setOnClickListener {
            val hour = date.get(Calendar.HOUR_OF_DAY)
            val minute = date.get(Calendar.MINUTE)

            TimePickerDialog(
                activity,
                TimePickerDialog.OnTimeSetListener { _, h, m -> setTime(h, m) },
                hour,
                minute,
                true
            ).show()
        }
        fragment_new_note_microphone_button.setOnClickListener {
            (activity as MainActivity).voiceToText(fragment_new_note_body_input)
        }
        fragment_new_note_camera_button.setOnClickListener {
            (activity as MainActivity).takePhoto { getTextFromImage(it) }
        }

        // TODO: hide checkboxes / buttons if not possible to use
    }

    override fun onResume() {
        super.onResume()
        setUpTopBar(
            getString(R.string.new_note),
            { navigate?.popBackStack() },
            { addNote() },
            R.drawable.ic_return,
            R.drawable.ic_save
        )
    }

    private fun addNote() {
        activity?.let {
            val imm =
                it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.window?.currentFocus?.applicationWindowToken, 0)
        }
        val noteTitle = fragment_new_note_title_input.text.toString()
        val noteBody = fragment_new_note_body_input.text.toString()
        val isProtected = fragment_new_note_protect_check_box.isChecked
        val toSync = fragment_new_note_google_check_box.isChecked
        viewModel.addNote(noteTitle, noteBody, date, isProtected, toSync)
        navigate?.navigateUp()
        Toast.makeText(context, getString(R.string.note_added), Toast.LENGTH_LONG).show()
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        date.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val stringBuilder = StringBuilder()
        if (day < 10) stringBuilder.append("0")
        stringBuilder.append(day)
        stringBuilder.append("/")
        if (month + 1 < 10) stringBuilder.append("0")
        stringBuilder.append(month + 1)
        stringBuilder.append("/")
        stringBuilder.append(year)

        fragment_new_note_date_input.text = stringBuilder.toString()
    }

    private fun setTime(hour: Int, minute: Int) {
        date.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val stringBuilder = StringBuilder()
        if (hour < 10) stringBuilder.append("0")
        stringBuilder.append(hour)
        stringBuilder.append(":")
        if (minute < 10) stringBuilder.append("0")
        stringBuilder.append(minute)

        fragment_new_note_time_input.text = stringBuilder.toString()
    }

    private fun getTextFromImage(image: Bitmap) {
        // TODO: some progress message
        viewModel.recognizeTextFromImage(image).observe(
            viewLifecycleOwner,
            Observer {
                // TODO: remove progress message
                if (it.isNullOrBlank()) {
                    // TODO: no text message
                } else {
                    fragment_new_note_body_input.setText(it, TextView.BufferType.EDITABLE)
                }
            }
        )
    }

}