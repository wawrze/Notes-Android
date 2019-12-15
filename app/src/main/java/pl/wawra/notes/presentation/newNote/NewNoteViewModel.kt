package pl.wawra.notes.presentation.newNote

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.AnnotateImageRequest
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest
import com.google.api.services.vision.v1.model.Feature
import com.google.api.services.vision.v1.model.Image
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.daos.NoteDao
import pl.wawra.notes.database.entities.Note
import pl.wawra.notes.utils.onBg
import java.io.ByteArrayOutputStream
import java.util.*


class NewNoteViewModel : ViewModel() {

    private var noteDao: NoteDao = Db.noteDao

    fun addNote(
        title: String,
        body: String,
        date: Calendar,
        isProtected: Boolean,
        toSync: Boolean
    ) {
        onBg {
            noteDao.insert(
                Note(
                    title = title,
                    body = body,
                    date = date.timeInMillis,
                    isProtected = isProtected
                )
            )
            if (toSync) {
                // TODO: send note to Google calendar
            }
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
                    VisionRequestInitializer("AIzaSyBt33ULbyfY80MyqfPvrHxZQJUxFoBuX_c")
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