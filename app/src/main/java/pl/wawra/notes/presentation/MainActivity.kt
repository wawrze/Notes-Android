package pl.wawra.notes.presentation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun setLeftButtonAction(action: () -> Any) {
        activity_main_top_bar_left_button.setOnClickListener { action.invoke() }
    }

    override fun setRightButtonAction(action: () -> Any) {
        activity_main_top_bar_right_button.setOnClickListener { action.invoke() }
    }

    override fun setLeftButtonIcon(res: Int) {
        activity_main_top_bar_left_button.setImageResource(res)
    }

    override fun setRightButtonIcon(res: Int) {
        activity_main_top_bar_right_button.setImageResource(res)
    }

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

    private var editToAppendFromMicrophone: EditText? = null

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
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val SPEECH_REQUEST_CODE = 666
    }

}