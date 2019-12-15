package pl.wawra.notes.presentation.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.*
import pl.wawra.notes.R
import pl.wawra.notes.base.BaseFragment
import pl.wawra.notes.database.entities.Note

class NotesFragment : BaseFragment(), NotesActions {

    private lateinit var viewModel: NotesViewModel
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(NotesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        viewModel.notesList.observe(
            viewLifecycleOwner,
            Observer {
                notesAdapter.data = it
            }
        )
    }

    private fun makeSnackBar(noteToRestore: Note) {
        view?.let {
            Snackbar.make(
                it,
                getString(R.string.note_deleted),
                Snackbar.LENGTH_LONG
            ).setAction(R.string.undo) { viewModel.restoreNotes(noteToRestore) }.show()
        }
    }

    override fun onCheckedClicked(note: Note) {
        viewModel.changeNoteChecked(note)
    }

    override fun onNoteBodyClicked(note: Note) {
        // TODO: check if note is protected, use biometry
        navigate?.navigate(NotesFragmentDirections.toNoteDetails(note.id))
    }

    override fun onDeleteClicked(note: Note) {
        viewModel.deleteNote(note)
        makeSnackBar(note)
    }

    private fun setupRecycler() {
        notesAdapter = NotesAdapter(this as NotesActions)
        fragment_notes_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        setUpTopBar(
            getString(R.string.your_notes),
            { /* TODO: Google login / logout*/ },
            { navigate?.navigate(NotesFragmentDirections.toNewNote()) },
            R.drawable.ic_google_light,
            R.drawable.ic_add
        )
        viewModel.getNotes()
    }

}