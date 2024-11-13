package com.example.finalapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var passwordStrengthBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        dbHelper = DatabaseHelper(this)

        val idEditText: EditText = findViewById(R.id.idEditText)
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val gmailEditText: EditText = findViewById(R.id.gmailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)
        val sendButton: Button = findViewById(R.id.sendButton)
        val existingUserTextView: TextView = findViewById(R.id.existingUserTextView)
        passwordStrengthBar = findViewById(R.id.passwordStrengthBar)

        // Password strength listener
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePasswordStrength(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        sendButton.setOnClickListener {
            val username = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val email = gmailEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() && password == confirmPassword) {
                if (dbHelper.isUserExists(username, email)) {
                    Toast.makeText(this, "Username or Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val result = dbHelper.insertUser(username, password, email)
                    if (result != -1L) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        finish()
                        overridePendingTransition(R.anim.fade_in, R.anim.page_fade_out)
                    } else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill out all fields and ensure passwords match", Toast.LENGTH_SHORT).show()
            }
        }

        existingUserTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.page_fade_out)
    }

    private fun updatePasswordStrength(password: String) {
        val strength = calculatePasswordStrength(password)

        when (strength) {
            1 -> {
                passwordStrengthBar.progress = 33
                passwordStrengthBar.progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
            }
            2 -> {
                passwordStrengthBar.progress = 66
                passwordStrengthBar.progressDrawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN)
            }
            3 -> {
                passwordStrengthBar.progress = 100
                passwordStrengthBar.progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN)
            }
            else -> {
                passwordStrengthBar.progress = 0
                passwordStrengthBar.progressDrawable.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun calculatePasswordStrength(password: String): Int {
        var score = 0
        if (password.length >= 8) score++
        if (password.matches(Regex(".*[A-Z].*"))) score++
        if (password.matches(Regex(".*[0-9].*"))) score++
        return score
    }
}
