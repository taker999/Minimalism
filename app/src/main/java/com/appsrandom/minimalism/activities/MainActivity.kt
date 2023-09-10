package com.appsrandom.minimalism.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.adapters.RVFoldersAdapter
import com.appsrandom.minimalism.databinding.ActivityMainBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.fragments.NoteFragment
import com.appsrandom.minimalism.fragments.SettingsFragment
import com.appsrandom.minimalism.models.Folder
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
import com.thebluealliance.spectrum.SpectrumPalette

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        loadFragment(NoteFragment(), true)
        binding.bottomNavigationView.selectedItemId = R.id.navNote

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

        if (isDarkModeOn as Boolean) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navNote -> {
                    loadFragment(NoteFragment(), false)
                    return@setOnItemSelectedListener true
                }
                R.id.navSettings -> {
                    val fm = supportFragmentManager
                    val ft = fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)

                    ft.replace(R.id.container, SettingsFragment())
                    ft.commit()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }

        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]
//        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[NoteViewModel::class.java]
//        viewModel.allNotes.observe(this, Observer { list ->
//            list?.let {
//
//            }
//        })
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferencesAppLock = getSharedPreferences("sharedPrefsAppLock", 0)
        val isAppLockOn = sharedPreferencesAppLock?.getBoolean("appLock", false)

        val isAppUnlocked = intent.getBooleanExtra("appUnlocked", false)

        if (isAppLockOn as Boolean && !isAppUnlocked) {
            val intent = Intent(this, InputPasswordActivity::class.java)
            intent.putExtra("whichActivity", "Main")
            startActivity(intent)
            finish()
        }
    }

    private fun loadFragment(fragment: Fragment, flag: Boolean) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)

        if (flag) {
            ft.add(R.id.container, fragment)
        } else {
            ft.replace(R.id.container, fragment)
        }
        ft.commit()
    }
}