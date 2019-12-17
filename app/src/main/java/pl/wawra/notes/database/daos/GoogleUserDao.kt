package pl.wawra.notes.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.wawra.notes.database.entities.GoogleUser

@Dao
interface GoogleUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(googleUser: GoogleUser)

    @Query("DELETE FROM googleuser")
    fun delete()

    @Query("SELECT * FROM googleuser LIMIT 1")
    fun getUser(): GoogleUser?

    @Query("UPDATE googleuser SET mainCalendar = :mainCalendar")
    fun updateMainCalendar(mainCalendar: String)

    @Query("SELECT mainCalendar FROM googleuser LIMIT 1")
    fun getUsersMainCalendar(): String?

}