package com.appsrandom.minimalism.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appsrandom.minimalism.activities.MainActivity
import com.appsrandom.minimalism.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        binding.shareApp.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Minimalism")
                var shareMessage = "Hey, I'm using Minimalism to take my notes and plan my day. It's really a simple and easy to use notes app. Download it from here: "
                shareMessage =
                    """
                    ${shareMessage + "https://play.google.com/store/apps/details?id=com.appsrandom.minimalism"}""".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one..."))
            }
        }

        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

        binding.switchNightMode.isChecked = isDarkModeOn as Boolean

        binding.switchNightMode.setOnClickListener {
            // When user taps the enable/disable
            // dark mode button
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            if (isDarkModeOn) {

                // if dark mode is on it
                // will turn it off
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // it will set isDarkModeOn
                // boolean to false
                editor?.putBoolean("isDarkModeOn", false);
                editor?.apply();

                // change text of Button
                binding.switchNightMode.isChecked = false
            }
            else {

                // if dark mode is off
                // it will turn it on
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                // it will set isDarkModeOn
                // boolean to true
                editor?.putBoolean("isDarkModeOn", true);
                editor?.apply();

                // change text of Button
                binding.switchNightMode.isChecked = true
            }
        }

        return binding.root
    }
}