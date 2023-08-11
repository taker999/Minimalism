package com.appsrandom.minimalism.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.andrognito.patternlockview.PatternLockView.Dot
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.databinding.ActivityCreatePasswordBinding


class CreatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePasswordBinding
    private lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        drawPattern()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun drawPattern() {
        binding.confirmPatternTV.text = "Draw an unlock pattern"
        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {
                binding.patternLockView.correctStateColor = ResourcesCompat.getColor(resources, R.color.darkPurple, null)
                handler.removeCallbacksAndMessages(null)
            }
            override fun onProgress(progressPattern: List<Dot>) {}
            override fun onComplete(pattern: List<Dot>) {
                // Shared Preferences to save state
                if (pattern.size < 4) {
                    binding.patternLockView.correctStateColor = ResourcesCompat.getColor(resources, R.color.orangeRed, null)
                    handler.postDelayed({
                        binding.patternLockView.clearPattern()
                    }, 2000)

                    Toast.makeText(this@CreatePasswordActivity, "Connect at least 4 dots...", Toast.LENGTH_SHORT).show()
                } else {
                    binding.patternLockView.removePatternLockListener(this)
                    binding.confirmPatternTV.text = "Draw pattern again to confirm"
                    val sharedPreferences = getSharedPreferences("sharedPrefsConfirmPattern", 0)
                    val editor = sharedPreferences.edit()
                    editor.putString(
                        "confirmPassword",
                        PatternLockUtils.patternToString(binding.patternLockView, pattern)
                    )
                    editor.apply()
                    confirmPattern()
                    handler.postDelayed({
                        binding.patternLockView.clearPattern()
                    }, 2000)

                }


                // Intent to navigate to home screen when password added is true
//                val intent = Intent(applicationContext, ProgramActivity::class.java)
//                startActivity(intent)
//                finish()
            }

            override fun onCleared() {
//                binding.patternLockView.removePatternLockListener(this)
            }
        })
    }

    private fun confirmPattern() {
        val sharedPreferences = getSharedPreferences("sharedPrefsConfirmPattern", 0)
        val originalPattern = sharedPreferences.getString("confirmPassword", "0")
        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {
                handler.removeCallbacksAndMessages(null)
            }

            override fun onProgress(progressPattern: List<Dot>?) {

            }

            override fun onComplete(pattern: List<Dot>?) {
                val editor = sharedPreferences.edit()
                binding.patternLockView.removePatternLockListener(this)
                if (PatternLockUtils.patternToString(binding.patternLockView, pattern) == originalPattern) {
                    binding.patternLockView.correctStateColor = ResourcesCompat.getColor(resources, R.color.darkPurple, null)
                    val whichActivity = intent.getStringExtra("whichActivity")
                    if (whichActivity == "ChangePassword" || whichActivity == "ForgotPassword") {
                        val sharedPreferencesPattern = getSharedPreferences("sharedPrefsPattern", 0)
                        val editorPattern = sharedPreferencesPattern.edit()
                        editorPattern.putString("password", PatternLockUtils.patternToString(binding.patternLockView, pattern))
                        editorPattern.apply()
                        Toast.makeText(this@CreatePasswordActivity, "Pattern changed successfully...", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this@CreatePasswordActivity, SetEmailActivity::class.java)
                        intent.putExtra("password", PatternLockUtils.patternToString(binding.patternLockView, pattern))
                        startActivity(intent)
                    }
                    finish()
                } else {
                    binding.patternLockView.correctStateColor = ResourcesCompat.getColor(resources, R.color.orangeRed, null)
//                    editor.remove("confirmPassword")
//                    editor.apply()
                    drawPattern()
                    handler.postDelayed({
                        binding.patternLockView.clearPattern()
                    }, 2000)
                }
                editor.clear()
                editor.apply()
            }

            override fun onCleared() {

            }

        })
    }
}