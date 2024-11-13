package com.example.finalapplication

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ManualInputActivity : AppCompatActivity() {
    private lateinit var qrInputLink: TextView
    private lateinit var inputEditText: EditText
    private lateinit var switchInputButton: ImageButton

    private var isNameInput: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        qrInputLink = findViewById(R.id.QRInputLink)
        inputEditText = findViewById(R.id.inputEditText)
        switchInputButton = findViewById(R.id.switchInputButton)

        inputEditText.hint = "Enter Name"

        qrInputLink.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        switchInputButton.setOnClickListener {
            inputEditText.text.clear()
            isNameInput = !isNameInput
            if (isNameInput) {
                inputEditText.hint = "Enter Name"
            } else {
                inputEditText.hint = "Enter ID"
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, StudentLogsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}