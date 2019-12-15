package pl.wawra.notes

import androidx.multidex.MultiDexApplication
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg
import java.util.*
import kotlin.collections.ArrayList

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
                    add(
                        Note(
                            title = "Notatka ostatnia",
                            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2018)
                                set(Calendar.MONTH, 11)
                                set(Calendar.DAY_OF_MONTH, 15)
                                set(Calendar.HOUR_OF_DAY, 20)
                                set(Calendar.MINUTE, 12)
                            }.timeInMillis,
                            isProtected = true
                        )
                    )
                    add(
                        Note(
                            title = "Notatka kolejna",
                            body = "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2019)
                                set(Calendar.MONTH, 5)
                                set(Calendar.DAY_OF_MONTH, 1)
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 5)
                            }.timeInMillis
                        )
                    )
                    add(
                        Note(
                            title = "Notatka testowa",
                            body = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2019)
                                set(Calendar.MONTH, 10)
                                set(Calendar.DAY_OF_MONTH, 9)
                                set(Calendar.HOUR_OF_DAY, 16)
                                set(Calendar.MINUTE, 2)
                            }.timeInMillis
                        )
                    )
                    add(
                        Note(
                            title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                            body = "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2020)
                                set(Calendar.MONTH, 1)
                                set(Calendar.DAY_OF_MONTH, 28)
                                set(Calendar.HOUR_OF_DAY, 5)
                                set(Calendar.MINUTE, 20)
                            }.timeInMillis,
                            isProtected = true
                        )
                    )
                    add(
                        Note(
                            title = "Inna notatka testowa",
                            body = "Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2019)
                                set(Calendar.MONTH, 9)
                                set(Calendar.DAY_OF_MONTH, 30)
                                set(Calendar.HOUR_OF_DAY, 15)
                                set(Calendar.MINUTE, 30)
                            }.timeInMillis,
                            isProtected = true
                        )
                    )
                    add(
                        Note(
                            title = "Notatka druga",
                            body = "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2019)
                                set(Calendar.MONTH, 0)
                                set(Calendar.DAY_OF_MONTH, 17)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 50)
                            }.timeInMillis
                        )
                    )
                    add(
                        Note(
                            title = "Notatka pierwsza",
                            body = "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus.",
                            date = Calendar.getInstance().apply {
                                set(Calendar.YEAR, 2018)
                                set(Calendar.MONTH, 3)
                                set(Calendar.DAY_OF_MONTH, 1)
                                set(Calendar.HOUR_OF_DAY, 8)
                                set(Calendar.MINUTE, 45)
                            }.timeInMillis
                        )
                    )
                }
                Db.noteDao.insert(notesToInsert)
            }
        }
    }

}