package pl.wawra.notes.presentation.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
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
        fragment_notes_return_button.setOnClickListener {
            notesAdapter.setDeleteMode(false)
            fragment_notes_remove_button.visibility = View.GONE
            fragment_notes_return_button.visibility = View.GONE
            notesAdapter.toRemove.clear()
        }
        fragment_notes_remove_button.setOnClickListener {
            val notesToRemove = notesAdapter.toRemove.toList()
            viewModel.deleteNotes(notesToRemove)
            notesAdapter.setDeleteMode(false)
            fragment_notes_remove_button.visibility = View.GONE
            fragment_notes_return_button.visibility = View.GONE
            makeSnackbar(notesToRemove)
            notesAdapter.toRemove.clear()
        }
    }

    private fun makeSnackbar(notesToRestore: List<Note>) {
        view?.let {
            Snackbar.make(
                it,
                getString(R.string.notes_deleted, notesToRestore.size),
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.undo) { viewModel.restoreNotes(notesToRestore) }
                .show()
        }
    }

    override fun onCheckedClicked(note: Note) {
        viewModel.changeNoteChecked(note)
    }

    override fun onNoteBodyClicked(note: Note) {
        if (note.body.startsWith("http", true)) {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(note.body)
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        toolbarTitle = getString(R.string.your_notes)
        navigationBack = false
        viewModel.getNotes()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_add_note -> {
            navigate?.navigate(NotesFragmentDirections.toNewNote())
            true
        }
        R.id.menu_delete_note -> {
            notesAdapter.setDeleteMode(true)
            fragment_notes_remove_button.visibility = View.VISIBLE
            fragment_notes_return_button.visibility = View.VISIBLE
            true
        }
        else -> false
    }

}