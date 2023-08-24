package com.appsrandom.minimalism.activities

import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appsrandom.minimalism.R
import com.appsrandom.minimalism.databinding.ActivityPrintBinding
import kotlin.math.max
import kotlin.math.min

class PrintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrintBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val printTitle = intent.getStringExtra("printTitle")
        val printContent = intent.getStringExtra("printContent")
        binding.printTitle.setText(printTitle)
        binding.printContent.setText(printContent)
        showPrint()

        binding.buttonLineBreak.setOnClickListener {
            val textToInsert = "<br>"
            if (binding.printTitle.isFocused) {
                val start = max(binding.printTitle.selectionStart, 0)
                val end = max(binding.printTitle.selectionEnd, 0)
                binding.printTitle.text.replace(
                    min(start, end), max(start, end),
                    textToInsert, 0, textToInsert.length
                )
            }else if (binding.printContent.isFocused) {
                val start = max(binding.printContent.selectionStart, 0)
                val end = max(binding.printContent.selectionEnd, 0)
                binding.printContent.text.replace(
                    min(start, end), max(start, end),
                    textToInsert, 0, textToInsert.length
                )
            }
        }

        binding.printButton.setOnClickListener {
            print()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.printTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val html = """<!DOCTYPE html>
            <html>
            <head>
                <title></title>
                <link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
            </head>
            <body>
            
            <h1 style="font-size: xx-large">$p0</h1>
            <p style="font-size: x-large">${binding.printContent.text}</p>
            
            </body>
            
            </html>"""
                binding.webView2.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.printContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val html = """<!DOCTYPE html>
            <html>
            <head>
                <title></title>
                <link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
            </head>
            <body>
            
            <h1 style="font-size: xx-large">${binding.printTitle.text}</h1>
            <p style="font-size: x-large">$p0</p>
            
            </body>
            
            </html>"""
                binding.webView2.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun showPrint() {
        val printTitle = binding.printTitle.text.toString().trim()
        val printContent = binding.printContent.text.toString().trim()
        val html = """<!DOCTYPE html>
            <html>
            <head>
                <title></title>
                <link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
            </head>
            <body>
            
            <h1 style="font-size: xx-large">$printTitle</h1>
            <p style="font-size: x-large">$printContent</p>
            
            </body>
            
            </html>"""
        binding.webView2.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    private fun print() {
        if (binding.printTitle.text.toString().isBlank() && binding.printContent.text.toString().isBlank()) {
            Toast.makeText(this, "Can't print empty note...", Toast.LENGTH_SHORT).show()
        } else {
            val printTitle = binding.printTitle.text.toString()
            val printContent = binding.printContent.text.toString()



            val html = """<!DOCTYPE html>
            <html>
            <head>
                <title></title>
                <link rel="stylesheet" type="text/css" href="file:android_asset/CreatePdf.css">
            </head>
            <body>
            
            <h1 style="font-size: xx-large">$printTitle</h1>
            <p style="font-size: x-large">$printContent</p>
            
            </body>
            
            </html>"""
            binding.webView2.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

            val printManager = this.getSystemService(PRINT_SERVICE) as PrintManager
            val adapter: PrintDocumentAdapter?
            val jobName = getString(R.string.app_name) + "Document"
            adapter=binding.webView2.createPrintDocumentAdapter(jobName)
            printManager.print(
                jobName,
                adapter, PrintAttributes.Builder().build()
            )
        }
    }
}