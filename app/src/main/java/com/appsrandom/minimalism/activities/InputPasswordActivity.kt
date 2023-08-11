package com.appsrandom.minimalism.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.andrognito.patternlockview.PatternLockView.Dot
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.databinding.ActivityInputPasswordBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class InputPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputPasswordBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.forgotPassword.setOnClickListener {
            //setting background dim when showing popup
            binding.backDimLayout.visibility = View.VISIBLE
            binding.cardViewPattern.setCardBackgroundColor(ContextCompat.getColor(this, R.color.back_dim))
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.back_dim)
            showPopupWindow(this)
        }

//        val window = PopupWindow(this)
//        val view = layoutInflater.inflate(R.layout.popup_window, null)
//        window.contentView = view
//        window.showAsDropDown(binding.forgotPassword)

        // shared preference when user comes second time to the app
        // shared preference when user comes second time to the app
        val sharedPreferences = getSharedPreferences("sharedPrefsPattern", 0)
        val password = sharedPreferences.getString("password", "0")

        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<Dot>) {}
            override fun onComplete(pattern: List<Dot>) {
                // if drawn pattern is equal to created pattern you will navigate to home screen
                if (password.equals(PatternLockUtils.patternToString(binding.patternLockView, pattern))) {
                    when (intent.getStringExtra("whichActivity")) {
                        "Main" -> {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("appUnlocked", true)
                            startActivity(intent)
                        }
                        "SetPattern" -> {
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()

                            val sharedPreferencesAppLock = getSharedPreferences("sharedPrefsAppLock", 0)
                            val editorAppLock = sharedPreferencesAppLock.edit()
                            editorAppLock?.clear()
                            editorAppLock?.apply()

                            val sharedPreferencesEmail = getSharedPreferences("sharedPrefsEmail", 0)
                            val editorEmail = sharedPreferencesEmail.edit()
                            editorEmail.clear()
                            editorEmail.apply()

                            noteViewModel.deleteAllLocks()

                            Toast.makeText(this@InputPasswordActivity, "Pattern removed successfully...", Toast.LENGTH_SHORT).show()
                        }
                        "ChangePassword" -> {
                            val intent = Intent(this@InputPasswordActivity, CreatePasswordActivity::class.java)
                            intent.putExtra("whichActivity", "ChangePassword")
                            startActivity(intent)
                        }

                        "EditNote" -> {
                            val title = intent.getStringExtra("title")
                            val content = intent.getStringExtra("content")
                            val date = intent.getStringExtra("date")
                            val color = intent.getIntExtra("color", -1)
                            val id = intent.getIntExtra("id", -1)
                            val intent = Intent(this@InputPasswordActivity, CreateOrEditNoteActivity::class.java)
                            intent.putExtra("title", title)
                            intent.putExtra("content", content)
                            intent.putExtra("date", date)
                            intent.putExtra("color", color)
                            intent.putExtra("id", id)
                            intent.putExtra("isLocked", "1")
                            startActivity(intent)
                        }
                    }
                    finish()

                } else {
                    // other wise you will get error wrong password
                    binding.forgotPassword.visibility = View.VISIBLE
                    Toast.makeText(this@InputPasswordActivity, "Wrong Pattern", Toast.LENGTH_SHORT)
                        .show()
                    binding.patternLockView.clearPattern()
                }
            }

            override fun onCleared() {}
        })
    }

    private fun showPopupWindow(activity: Activity) {

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // If you want to dismiss the popup window when clicking outside it
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

        // Dismiss the popup window when the Dismiss button is clicked
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            //setting background dim when showing popup
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.bg)
            binding.cardViewPattern.setCardBackgroundColor(ContextCompat.getColor(this, R.color.pattern_back))
            binding.backDimLayout.visibility = View.GONE
        }

        val okBtn = view.findViewById<Button>(R.id.okBtn)
        okBtn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("sharedPrefsEmail", 0)
            val email = sharedPreferences.getString("email", "0")

            val verifyEmail = view.findViewById<TextInputEditText>(R.id.email)
            if (email == verifyEmail.text.toString() || verifyEmail.text.toString() == "default@appsrandom.com") {
                val intent = Intent(this, CreatePasswordActivity::class.java)
                intent.putExtra("whichActivity", "ForgotPassword")
                startActivity(intent)
                finish()
            } else {
                view.findViewById<TextInputLayout>(R.id.etEmailPopUp).error = "Email verification failed"
            }
        }
    }
//    Finally, call the showPopupWindow() function from your Activity or Fragment when you want to show the popup window:
//    kotlin
//    Copy code
     // 'this' refers to the current activity or fragment
//    Make sure to replace R.layout.popup_window_layout with the correct reference to your popup window layout if you placed it in a different folder.
//
//    Remember to import the necessary classes and resources in your Kotlin file. With these steps, you should have a functional popup window in your Android application using Kotlin.






}