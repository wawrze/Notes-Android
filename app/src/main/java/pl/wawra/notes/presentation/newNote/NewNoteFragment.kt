package pl.wawra.notes.presentation.newNote

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_new_note.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseFragment

class NewNoteFragment : BaseFragment() {

    private lateinit var viewModel: NewNoteViewModel
    // TODO: add voice to text, text from image, protection
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(NewNoteViewModel::class.java)
        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onResume() {
        super.onResume()
        setUpTopBar(
            getString(R.string.new_note),
            { navigate?.popBackStack() },
            {
                activity?.let {
                    val imm =
                        it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(it.window?.currentFocus?.applicationWindowToken, 0)
                }
                val noteBody = fragment_new_note_title_input.text.toString()
                viewModel.addNote(noteBody)
                navigate?.navigateUp()
                Toast.makeText(context, getString(R.string.note_added), Toast.LENGTH_LONG).show()
            },
            R.drawable.ic_return,
            R.drawable.ic_save
        )
    }

}