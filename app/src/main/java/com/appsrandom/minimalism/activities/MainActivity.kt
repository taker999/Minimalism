package com.appsrandom.minimalism.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.databinding.ActivityMainBinding
import com.appsrandom.minimalism.db.NoteDatabase
import com.appsrandom.minimalism.fragments.NoteFragment
import com.appsrandom.minimalism.fragments.SettingsFragment
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(NoteFragment(), true)
        binding.bottomNavigationView.selectedItemId = R.id.navNote

        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val isDarkModeOn = sharedPreferences?.getBoolean("isDarkModeOn", false)

        if (isDarkModeOn as Boolean) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navNote -> {
                    loadFragment(NoteFragment(), false)
                    return@setOnItemSelectedListener true
                }
                R.id.navSettings -> {
                    loadFragment(SettingsFragment(), false)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
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

    private fun loadFragment(fragment: Fragment, flag: Boolean) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        if (flag) {
            ft.add(R.id.container, fragment)
        } else {
            ft.replace(R.id.container, fragment)
        }
        ft.commit()
    }
}