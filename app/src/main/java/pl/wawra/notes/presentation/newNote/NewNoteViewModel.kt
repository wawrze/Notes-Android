package pl.wawra.notes.presentation.newNote

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import pl.wawra.notes.R
import pl.wawra.notes.calendar.CalendarClient
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.CalendarEvent
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg
import java.io.ByteArrayOutputStream
import java.util.*


class NewNoteViewModel : ViewModel() {

    private val noteDao = Db.noteDao
    private val googleUserDao = Db.googleUserDao
    private val calendarEventDao = Db.calendarEventDao

    val isUserLoggedIn = MutableLiveData<Boolean>()
    val changeProgressBar = MutableLiveData<Pair<Boolean, Int>>()
    val toastMessage = MutableLiveData<Int>()
    val goBack = MutableLiveData<Boolean>()

    fun addNote(
        title: String,
        body: String,
        date: Calendar,
        isProtected: Boolean,
        toSync: Boolean
    ) {
        onBg {
            val noteId = noteDao.insert(
                Note(
                    title = title,
                    body = body,
                    date = date.timeInMillis,
                    isProtected = isProtected
                )
            )
            if (toSync) {
                changeProgressBar.postValue(Pair(false, R.string.sync_in_progress))

                val user = googleUserDao.getUser()
                if (user?.mainCalendar != null) {
                    val newEvent = Event()
                        .setSummary(title)
                        .setDescription(body)
                        .apply {
                            start = EventDateTime()
                                .setDateTime(DateTime(date.timeInMillis))
                            end = EventDateTime()
                                .setDateTime(DateTime(date.timeInMillis))
                        }
                    val confirmation = CalendarClient.instance
                        ?.Events()
                        ?.insert(user.mainCalendar, newEvent)
                        ?.execute()

                    changeProgressBar.postValue(Pair(false, -1))

                    if (confirmation?.id != null) {
                        calendarEventDao.insert(
                            CalendarEvent().apply {
                                id = confirmation.id
                                this.noteId = noteId
                                googleUser = user.mainCalendar
                            }
                        )
                        toastMessage.postValue(R.string.note_added_sync_success)
                    } else {
                        toastMessage.postValue(R.string.note_added_sync_failed)
                    }
                } else {
                    toastMessage.postValue(R.string.note_added_sync_failed)
                }
            } else {
                toastMessage.postValue(R.string.note_added)
            }
            goBack.postValue(true)
        }
    }

    fun isUserLoggedIn() {
        onBg {
            isUserLoggedIn.postValue(googleUserDao.getUser() != null)
        }
    }

    fun recognizeTextFromImage(image: Bitmap): MutableLiveData<String?> {
        val result = MutableLiveData<String?>()

        onBg {
            try {
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()
                image.recycle()

                val vision = Vision.Builder(
                    NetHttpTransport(),
                    AndroidJsonFactory(),
                    null
                ).setVisionRequestInitializer(
                    VisionRequestInitializer("GOOGLE API KEY")
                ).build()

                val inputImage = Image().apply { encodeContent(byteArray) }

                val desiredFeature = Feature()
                desiredFeature.type = "DOCUMENT_TEXT_DETECTION"

                val request = AnnotateImageRequest().apply {
                    this.image = inputImage
                    features = listOf(desiredFeature)
                }
                val batchRequest = BatchAnnotateImagesRequest().apply { requests = listOf(request) }
                val batchResponse = vision.images().annotate(batchRequest).execute()

                val text = batchResponse.responses[0].fullTextAnnotation.text
                result.postValue(text)
            } catch (e: Throwable) {
                result.postValue(null)
            }
        }

        return result
    }

}