package com.example.finalapplication

import android.app.blob.BlobStoreManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.Session

class QRCodeGeneratorActivity : AppCompatActivity() {

    private lateinit var etID: EditText
    private lateinit var etName: EditText
    private lateinit var spinnerUserType: Spinner
    private lateinit var spinnerDepartment: Spinner
    private lateinit var ivQRCode: ImageView
    private lateinit var tvQRCodeName: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnGenerateQR: Button
    private lateinit var btnSendEmail: Button
    private lateinit var qrImageFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_generator)

        // Initialize views
        etID = findViewById(R.id.etID)
        etName = findViewById(R.id.etName)
        spinnerUserType = findViewById(R.id.spinnerUserType)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        ivQRCode = findViewById(R.id.ivQRCode)
        tvQRCodeName = findViewById(R.id.tvQRCodeName)
        etEmail = findViewById(R.id.etEmail)
        btnGenerateQR = findViewById(R.id.btnGenerateQR)
        btnSendEmail = findViewById(R.id.btnSendEmail)

        // Setup User Type Spinner
        val userTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.user_types,
            android.R.layout.simple_spinner_item
        )
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUserType.adapter = userTypeAdapter

        // Listen for User Type selection changes to update department spinner
        spinnerUserType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedUserType = parent.getItemAtPosition(position).toString()
                updateDepartmentOptions(selectedUserType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnGenerateQR.setOnClickListener {
            generateQRCode()
            sendEmailWithQRCodeJavaMail()
        }

        btnSendEmail.setOnClickListener {
            sendEmailWithQRCode()
        }
    }

    private fun updateDepartmentOptions(userType: String) {
        // Choose department array based on user type
        val departmentArrayResId = when (userType) {
            "Student" -> R.array.student_departments
            "Teacher", "Staff" -> R.array.faculty_staff_departments
            else -> 0
        }

        if (departmentArrayResId != 0) {
            val departmentAdapter = ArrayAdapter.createFromResource(
                this,
                departmentArrayResId,
                android.R.layout.simple_spinner_item
            )
            departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDepartment.adapter = departmentAdapter
        } else {
            spinnerDepartment.adapter = null  // Clear spinner if no departments apply
        }
    }

    private fun generateQRCode() {
        val id = etID.text.toString()
        val name = etName.text.toString()
        val userType = spinnerUserType.selectedItem.toString()
        val department = spinnerDepartment.selectedItem.toString()

        if (id.isEmpty() || name.isEmpty() || userType.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Register the QR code data in the database
        val dbHelper = DatabaseHelper(this)
        val isSuccess = when (userType) {
            "Student" -> dbHelper.insertStudent(id, name, userType, department) > 0
            "Teacher", "Staff" -> dbHelper.insertFacultyStaff(id, name, userType, department) > 0
            else -> false
        }

        if (isSuccess) {
            Toast.makeText(this, "$userType registered successfully.", Toast.LENGTH_SHORT).show()

            // Generate QR Code
            try {
                val qrData = "ID: $id\nName: $name\nUser Type: $userType\nDepartment: $department"
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                ivQRCode.setImageBitmap(bitmap)
                tvQRCodeName.text = "$name's QR Code"
                qrImageFile = saveQRCodeToFile(bitmap)

            } catch (e: Exception) {
                Toast.makeText(this, "Failed to generate QR code: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to register $userType.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveQRCodeToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "qrcode.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    private fun sendEmailWithQRCode() {
        val email = etEmail.text.toString().trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", qrImageFile)
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Your QR Code")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(emailIntent, "Send email..."))


    }

    private fun sendEmailWithQRCodeJavaMail() {
        val recipientEmail = etEmail.text.toString().trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Background task to send email using JavaMail API
        SendMailTask(recipientEmail, qrImageFile).execute()
    }

    private inner class SendMailTask(
        private val recipient: String,
        private val qrFile: File
    ) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.socketFactory.port", "465")
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.port", "465")
                }

                // Initialize session with javax.mail.Authenticator
                val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication("nbspilogs@gmail.com", "Admin@2025")
                    }
                })

                // Create email message
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress("nbspilogs@gmail.com"))
                    setRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                    setSubject("Your QR Code")
                    setText("Attached file is your generated QR code.")
                }

                // Attach QR code image
                val mimeBodyPart = MimeBodyPart().apply {
                    dataHandler = DataHandler(FileDataSource(qrFile))
                    fileName = "QRCode.png"
                }
                val multipart = MimeMultipart().apply {
                    addBodyPart(mimeBodyPart)
                }
                message.setContent(multipart)

                // Send email
                Transport.send(message)
                return "Email sent successfully"
            } catch (e: MessagingException) {
                e.printStackTrace()
                return "Failed to send email: ${e.message}"
            }
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(this@QRCodeGeneratorActivity, result, Toast.LENGTH_LONG).show()
        }
    }



}
