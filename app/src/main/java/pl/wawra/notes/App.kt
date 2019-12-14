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
                    add(Note(body = "Notatka ostatnia"))
                    add(Note(body = "Notatka kolejna"))
                    add(Note(body = "https://www.youtube.com/watch?v=A0xQsrtD6yY"))
                    add(Note(body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."))
                    add(Note(body = "https://www.google.pl/"))
                    add(Note(body = "Notatka druga"))
                    add(Note(body = "Notatka pierwsza"))
                }
                Db.noteDao.insert(notesToInsert)
            }
        }
    }

}