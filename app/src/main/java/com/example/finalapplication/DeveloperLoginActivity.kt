package com.example.finalapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DeveloperLoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_login)

        dbHelper = DatabaseHelper(this)

        // Insert an initial developer account if it doesn't exist
        if (!dbHelper.isDeveloperExists("D202501", "daveharlskie@gmail.com")) {
            dbHelper.insertDeveloper("D202501", "Admin@2025", "daveharlskie@gmail.com")
        }

        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (dbHelper.checkDeveloper(username, password)) {
                // Correct developer credentials; proceed to QRCodeGeneratorActivity
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, QRCodeGeneratorActivity::class.java)
                startActivity(intent)
                finish() // Close the login activity
            } else {
                // Incorrect credentials; show error
                Toast.makeText(this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
