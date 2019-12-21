package pl.wawra.notes

import androidx.multidex.MultiDexApplication
import pl.wawra.notes.database.Db

@Suppress("unused")
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Db.init(this)
    }

}