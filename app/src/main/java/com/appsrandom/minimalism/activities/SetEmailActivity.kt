package com.appsrandom.minimalism.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.appsrandom.minimalism.databinding.ActivitySetEmailBinding
import com.r0adkll.slidr.Slidr

class SetEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetEmailBinding

    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Slidr.attach(this)

        password = intent.getStringExtra("password")!!

        binding.backButton.setOnClickListener {
            finish()
        }

        enterEmail()
    }

    private fun enterEmail() {
        binding.nextBtn.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInp.text.toString()).matches()) {
                binding.etEmail.isErrorEnabled = true
                binding.etEmail.error = "Please enter a valid Email"
            } else {
                val email = binding.emailInp.text.toString()
                binding.etEmail.isErrorEnabled = false
                binding.etEmail.error = null
                confirmEmail(email)
            }
        }
    }

    private fun confirmEmail(email: String) {
        binding.emailInp.text?.clear()
        binding.nextBtn.text = "DONE"
        binding.tVEmail.text = "Enter your email again"
        binding.nextBtn.setOnClickListener {
            if (email == binding.emailInp.text.toString()) {

                val sharedPreferencesPattern = getSharedPreferences("sharedPrefsPattern", 0)
                val editorPattern = sharedPreferencesPattern.edit()
                editorPattern.putString("password", password)
                editorPattern.apply()

                val sharedPreferences = getSharedPreferences("sharedPrefsEmail", 0)
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.apply()
                Toast.makeText(this, "Pattern set successfully...", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                binding.etEmail.error = "Emails do not match, please try again"
                binding.tVEmail.text = "Add an email to help you reset the password if you forget it"
                binding.nextBtn.text = "NEXT"
                enterEmail()
            }
        }
    }
}