package com.appsrandom.minimalism.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.appsrandom.minimalism.databinding.ActivityFeedbackBinding
import com.r0adkll.slidr.Slidr


class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private var imageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            contentResolver.takePersistableUriPermission(imageUri as Uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Use the selectedImageUri for further processing
            binding.imageView.visibility = View.VISIBLE
            binding.imageView.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Slidr.attach(this)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.imageBtn.setOnClickListener {

            openGallery()

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
//                    openGallery()
//                } else {
//                    requestStoragePermission()
//                }
//            } else {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    openGallery()
//                } else {
//                    requestStoragePermission()
//                }
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
//                    galleryLauncher.launch("image/*")
//                } else {
//                    requestStoragePermission()
//                }
//            } else {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    galleryLauncher.launch("image/*")
//                } else {
//                    requestStoragePermission()
//                }
//            }
        }

        binding.submitBtn.setOnClickListener {
            var title = ""
            var flag = false

            if (binding.fewFeature.isChecked) {
                binding.errorTv.visibility = View.GONE
                title = binding.fewFeature.text.toString()
                flag = true
            }
            if (binding.bugs.isChecked) {
                binding.errorTv.visibility = View.GONE
                title+=", "+binding.bugs.text
                flag = true
            }
            if (binding.others.isChecked) {
                binding.errorTv.visibility = View.GONE
                title+=", "+binding.others.text
                flag = true
            }

            val body = binding.editText.text.toString().trim()

            if (!flag) {
                binding.errorTv.visibility = View.VISIBLE
            } else if (body.isBlank()) {
                Toast.makeText(this, "Please write about your problem...", Toast.LENGTH_SHORT).show()
            } else {
                if (imageUri != null) {
                    mailWithImage(imageUri as Uri, listOf("appsrandom6@gmail.com"), title, body)
                } else {
                    mailWithText(listOf("appsrandom6@gmail.com"), title, body)
                }
            }
        }

        binding.removeImg.setOnClickListener {
            binding.imageView.visibility = View.GONE
            imageUri = null
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImageLauncher.launch(intent)
    }

//    private fun requestStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        }
//    }

    private fun mailWithText(emailReceiver: List<String>, title: String, message: String) {
        val email = Intent(Intent.ACTION_SEND)
        email.type = "plain/text"
        email.putExtra(Intent.EXTRA_EMAIL, emailReceiver.toTypedArray())
        email.putExtra(Intent.EXTRA_SUBJECT, title)
        email.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(email, "choose mail app..."))
    }

    private fun mailWithImage(uri: Uri, emailReceiver: List<String>, title: String, message: String) {
        val email = Intent(Intent.ACTION_SEND)
        email.type = "application/octet-stream"
        email.putExtra(Intent.EXTRA_EMAIL, emailReceiver.toTypedArray())
        email.putExtra(Intent.EXTRA_SUBJECT, title)
        email.putExtra(Intent.EXTRA_TEXT, message)
        email.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(email, "choose mail app..."))
    }
}