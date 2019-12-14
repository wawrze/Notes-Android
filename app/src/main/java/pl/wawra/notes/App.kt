package pl.wawra.notes

import androidx.multidex.MultiDexApplication
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg

@Suppress("unused")
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Db.init(this)
        addNotesToDb()
    }

    private fun addNotesToDb() {
        onBg {
            if (Db.noteDao.getNotes().isEmpty()) {
                val notesToInsert = ArrayList<Note>().apply {
                    add(Note(title = "Notatka ostatnia"))
                    add(Note(title = "Notatka kolejna"))
                    add(Note(title = "https://www.youtube.com/watch?v=A0xQsrtD6yY"))
                    add(Note(title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."))
                    add(Note(title = "https://www.google.pl/"))
                    add(Note(title = "Notatka druga"))
                    add(Note(title = "Notatka pierwsza"))
                }
                Db.noteDao.insert(notesToInsert)
            }
        }
    }

}