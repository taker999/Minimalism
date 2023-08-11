package com.appsrandom.minimalism.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appsrandom.minimalism.databinding.ActivityPrivacyAndPolicyBinding
import com.r0adkll.slidr.Slidr

class PrivacyAndPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyAndPolicyBinding
    private val fileName = "privacy_policy.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyAndPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Slidr.attach(this)

        binding.webView.loadUrl("file:///android_asset/$fileName")

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}