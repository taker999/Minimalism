package com.appsrandom.minimalism.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.appsrandom.minimalism.repository.NoteRepository
import com.appsrandom.minimalism.viewModel.NoteViewModel
import com.appsrandom.minimalism.viewModel.NoteViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.thebluealliance.spectrum.SpectrumPalette

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var noteViewModel: NoteViewModel

    private var folderColor = -1
    private var fName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        folderColor = RVFoldersAdapter().folderColor
        fName = RVFoldersAdapter().fName

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
                R.id.navEdit -> {

                    showPopupWindow(this)
                    return@setOnItemSelectedListener false

                }
                R.id.navDelete -> {

                    return@setOnItemSelectedListener false

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

    private fun showPopupWindow(activity: Activity) {

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_create_folder, null)

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
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtnFolder)
        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        val createFolderParentLayout = view.findViewById<LinearLayout>(R.id.createFolderParentLayout)
        createFolderParentLayout.setBackgroundColor(folderColor)

        val colorPickerFolder = view.findViewById<SpectrumPalette>(R.id.colorPickerFolder)
        colorPickerFolder.setSelectedColor(folderColor)
        colorPickerFolder.setOnColorSelectedListener {
            folderColor = it
            createFolderParentLayout.setBackgroundColor(folderColor)
        }

        val folderNameView = view.findViewById<TextInputEditText>(R.id.folderName)
        folderNameView.setText(fName)

        val okBtn = view.findViewById<Button>(R.id.addBtn)
        okBtn.setOnClickListener {
            val folderName = folderNameView.text.toString()
            if (folderName.isNotBlank()) {
//                noteViewModel.insertFolder(Folder(folderName, folderColor))
                popupWindow.dismiss()
            }
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