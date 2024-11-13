package com.example.finalapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import android.Manifest
import android.view.Gravity
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.ResultPoint


class QRScannerActivity : AppCompatActivity() {

    private lateinit var idDisplayTextView: TextView
    private lateinit var manualInputLink: TextView
    private lateinit var qrScanner: BarcodeView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var burgerMenu: ImageButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fab: FloatingActionButton


    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        idDisplayTextView = findViewById(R.id.idDisplayTextView)
        manualInputLink = findViewById(R.id.manualInputLink)
        qrScanner = findViewById(R.id.qrCameraBox)
        drawerLayout = findViewById(R.id.drawer_layout)
        burgerMenu = findViewById(R.id.burger_menu)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fab = findViewById(R.id.fab)

        burgerMenu.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fab -> {
                    startActivity(Intent(this, QRScannerActivity::class.java))
                    true
                }
                R.id.nav_logs -> {
                    startActivity(Intent(this, StudentLogsActivity::class.java))
                    true
                }
                R.id.nav_more -> {

                    true
                }
                else -> false
            }
        }

        manualInputLink.setOnClickListener {
            startActivity(Intent(this, ManualInputActivity::class.java))
            overridePendingTransition(0, 0)
        }

        checkCameraPermission()
    }

    private fun startQRCodeScanning() {
        qrScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                // Display the scanned ID
                idDisplayTextView.text = "ID: ${result.text}"
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
                // Handle possible result points if necessary
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Resume scanning
        qrScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        // Pause scanning
        qrScanner.pause()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            startQRCodeScanning() // Start scanning if permission is already granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRCodeScanning() // Start scanning if permission is granted
            } else {
                // Handle the case where permission is denied
                Toast.makeText(this, "Camera permission is needed to scan QR codes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, StudentLogsActivity::class.java) // Replace MainActivity with your homepage activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}
