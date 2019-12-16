package pl.wawra.notes.presentation

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import pl.wawra.notes.R
import pl.wawra.notes.base.Navigation
import pl.wawra.notes.base.ToolbarInteraction

class MainActivity : AppCompatActivity(), ToolbarInteraction, Navigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getNavigationController() = findNavController(R.id.nav_host_fragment)

    override fun onSupportNavigateUp() = getNavigationController().navigateUp()

    override fun setTopBarTitle(title: String) {
        activity_main_top_bar_title.text = title
    }

    override fun setLeftButtonAction(action: (() -> Any)?) {
        if (action != null) {
            activity_main_top_bar_left_button.setOnClickListener { action.invoke() }
        } else {
            // TODO: Google login / logout
        }
    }

    override fun setRightButtonAction(action: () -> Any) {
        activity_main_top_bar_right_button.setOnClickListener { action.invoke() }
    }

    override fun setLeftButtonIcon(res: Int?) {
        if (res != null) {
            activity_main_top_bar_left_button.setImageResource(res)
        } else {
            // TODO: check if user is logged in to Google
            activity_main_top_bar_left_button.setImageResource(R.drawable.ic_google_light)
        }
    }

    override fun setRightButtonIcon(res: Int) {
        activity_main_top_bar_right_button.setImageResource(res)
    }

    private var editToAppendFromMicrophone: EditText? = null
    fun voiceToText(editText: EditText) {
        try {
            editToAppendFromMicrophone = editText
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startActivityForResult(
                intent,
                SPEECH_REQUEST_CODE
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.voice_recognition_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private var cameraCallBack: ((Bitmap) -> Unit)? = null
    fun takePhoto(callBack: (Bitmap) -> Unit) {
        cameraCallBack = callBack

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERM_REQUEST
            )
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, CAMERA_RC)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SPEECH_REQUEST_CODE -> {
                    val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    editToAppendFromMicrophone?.setText(
                        results?.elementAtOrNull(0).orEmpty(), TextView.BufferType.EDITABLE
                    )
                    editToAppendFromMicrophone = null
                }
                CAMERA_RC -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    cameraCallBack?.invoke(imageBitmap)
                    cameraCallBack = null
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERM_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto { cameraCallBack }
            } else {
                // TODO: no permission message
            }
        }
    }

    companion object {
        private const val CAMERA_PERM_REQUEST = 111
        private const val CAMERA_RC = 333
        private const val SPEECH_REQUEST_CODE = 666
    }

}