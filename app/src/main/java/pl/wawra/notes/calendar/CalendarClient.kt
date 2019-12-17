package pl.wawra.notes.calendar

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import pl.wawra.notes.presentation.MainActivity

class CalendarClient {

    companion object {

        var instance: Calendar? = null

        fun initCalendar(accountName: String?, context: MainActivity) {
            val cred = GoogleAccountCredential
                .usingOAuth2(context, setOf(CalendarScopes.CALENDAR))
                .setBackOff(ExponentialBackOff())
                .setSelectedAccountName(accountName)
            instance = Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(),
                cred
            ).setApplicationName("Notes").build()
        }

        fun closeCalendar() {
            instance = null
        }

    }

}