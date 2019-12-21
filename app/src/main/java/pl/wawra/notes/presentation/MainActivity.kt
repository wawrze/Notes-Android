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
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.CalendarList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import pl.wawra.notes.R
import pl.wawra.notes.base.Navigation
import pl.wawra.notes.base.ToolbarInteraction
import pl.wawra.notes.calendar.CalendarClient
import pl.wawra.notes.database.Db
import pl.wawra.notes.database.entities.GoogleUser
import pl.wawra.notes.utils.onBg
import pl.wawra.notes.utils.onUi

class MainActivity : AppCompatActivity(), ToolbarInteraction, Navigation {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private val googleUserDao = Db.googleUserDao

    var refreshNotesListCallBack: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureGoogleSignIn()
        firebaseAuth = FirebaseAuth.getInstance()

        onBg {
            val user = googleUserDao.getUser()
            if (user != null) {
                firebaseAuthWithGoogle(user.token, user.accountName)
            }
        }
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
            onBg {
                val user = googleUserDao.getUser()
                onUi {
                    if (user != null) {
                        activity_main_top_bar_left_button.setOnClickListener {
                            signOut()
                        }
                    } else {
                        activity_main_top_bar_left_button.setOnClickListener {
                            signIn()
                        }
                    }
                }
            }
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        onBg { googleUserDao.delete() }
        setLeftButtonIcon(null)
        refreshNotesListCallBack?.invoke()
        setLeftButtonAction(null)
        CalendarClient.closeCalendar()
        Toast.makeText(
            this,
            getString(R.string.google_log_out),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun setRightButtonAction(action: () -> Any) {
        activity_main_top_bar_right_button.setOnClickListener { action.invoke() }
    }

    override fun setLeftButtonIcon(res: Int?) {
        if (res != null) {
            activity_main_top_bar_left_button.setImageResource(res)
        } else {
            onBg {
                val user = googleUserDao.getUser()
                onUi {
                    activity_main_top_bar_left_button.setImageResource(
                        if (user != null) R.drawable.ic_google_dark else R.drawable.ic_google_light
                    )
                }
            }
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
                RC_SIGN_IN -> {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        task.getResult(ApiException::class.java)?.let {
                            firebaseAuthWithGoogle(it.idToken.orEmpty(), it.account?.name.orEmpty())
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(
                            this,
                            getString(R.string.google_sign_in_failed),
                            Toast.LENGTH_LONG
                        ).show()
                        activity_main_progress_bar.visibility = View.GONE
                        setLeftButtonAction(null)
                        onBg { googleUserDao.delete() }
                    }
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
        when (requestCode) {
            CAMERA_PERM_REQUEST -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto { cameraCallBack }
            } else {
                Toast.makeText(this, getString(R.string.no_camera_permission), Toast.LENGTH_LONG)
                    .show()
            }
            CALENDAR_PERM_REQUEST -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMainCalendarId()
            } else {
                Toast.makeText(this, getString(R.string.no_calendar_permission), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        activity_main_progress_bar.visibility = View.VISIBLE
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun getMainCalendarId() {
        try {
            val list =
                CalendarClient.instance?.calendarList()?.list()?.setFields("items(id)")?.execute()
                    ?: CalendarList()
            val mainCalendar = list.items[0].id
            googleUserDao.updateMainCalendar(mainCalendar)
        } catch (e: UserRecoverableAuthIOException) {
            startActivityForResult(e.intent, CALENDAR_PERM_REQUEST)
        }
    }

    private fun firebaseAuthWithGoogle(token: String, accountName: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                CalendarClient.initCalendar(accountName, this)
                onBg {
                    googleUserDao.insert(
                        GoogleUser().apply {
                            this.token = token
                            this.accountName = accountName
                        }
                    )
                    getMainCalendarId()
                }
                refreshNotesListCallBack?.invoke()
                Toast.makeText(this, getString(R.string.google_log_in_success), Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_LONG)
                    .show()
                onBg { googleUserDao.delete() }
            }
            activity_main_progress_bar.visibility = View.GONE
            setLeftButtonIcon(null)
            setLeftButtonAction(null)
        }
    }

    companion object {
        private const val RC_SIGN_IN: Int = 1
        private const val CAMERA_PERM_REQUEST = 111
        private const val CALENDAR_PERM_REQUEST = 222
        private const val CAMERA_RC = 333
        private const val SPEECH_REQUEST_CODE = 666
    }

}