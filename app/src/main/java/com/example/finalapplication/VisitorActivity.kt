package com.example.finalapplication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import android.os.Handler
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class VisitorActivity : AppCompatActivity() {

    private var tableLayout: TableLayout? = null
    private var searchEditText: EditText? = null
    private var filterSpinner: Spinner? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var burgerMenu: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000

    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            updateDateTime()
            handler.postDelayed(this, 100, 0) // Update every second
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor) // Set the new layout

        tableLayout = findViewById(R.id.table_layout)
        searchEditText = findViewById(R.id.search_edit_text)
        filterSpinner = findViewById(R.id.filter_spinner)
        drawerLayout = findViewById(R.id.drawer_layout)
        burgerMenu = findViewById(R.id.burger_menu)
        navigationView = findViewById(R.id.nav_view)
        dateText = findViewById(R.id.date_text)
        timeText = findViewById(R.id.time_text)

        handler.post(runnable)

        burgerMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT)
            } else {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            try {
                val intent = Intent(this, QRScannerActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error opening QRScannerActivity", Toast.LENGTH_SHORT).show()
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToPage("Home")
                    true
                }
                R.id.nav_student -> {
                    navigateToPage("StudentLogs")
                    true
                }
                R.id.nav_faculty -> {
                    navigateToPage("FacultyLogs")
                    true
                }
                R.id.nav_visitors -> {
                    navigateToPage("VisitorLogs")
                    true
                }
                R.id.nav_incident_reports -> {
                    navigateToPage("IncidentReports")
                    true
                }
                R.id.nav_tools -> {
                    navigateToPage("ToolsEquipment")
                    true
                }
                R.id.nav_logs -> {
                    navigateToPage("AllLogs")
                    true
                }
                R.id.nav_dev -> {
                    navigateToPage("ForDevelopers")
                    true
                }
                R.id.nav_logout -> {
                    // Handle logout if necessary
                    true
                }
                else -> {
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }

        navigationView.setCheckedItem(R.id.nav_home)
        navigationView.setCheckedItem(R.id.nav_visitors)

        setupSearch()
        setupFilter()
    }

    private fun updateDateTime() {
        val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())

        dateText.text = "Date: $currentDate"
        timeText.text = "Time: $currentTime"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < backPressedInterval) {
            showLogoutConfirmationDialog() // Show logout dialog on double back press
        } else {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "Press back again to log out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Do you want to log out?")
        builder.setPositiveButton("Yes") { dialog, which ->
            logout()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun logout() {

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
        startActivity(intent)
        finish()
    }

    private fun navigateToPage(page: String) {
        when (page) {
            "Home" -> {
                val intent = Intent(this, StudentLogsActivity::class.java)
                startActivity(intent)
            }
            "StudentLogs" -> {
                val intent = Intent(this, StudentLogsActivity::class.java)
                startActivity(intent)
            }
            "FacultyLogs" -> {
                val intent = Intent(this, FacultyStaffLogsActivity::class.java)
                startActivity(intent)
            }
            "VisitorLogs" -> {
                // Stay in VisitorActivity
            }
            "IncidentReports" -> {
                // Navigate to IncidentReportsActivity
            }
            "ToolsEquipment" -> {
                // Navigate to ToolsEquipmentActivity
            }
            "AllLogs" -> {
                // Navigate to AllLogsActivity
            }
            "ForDevelopers" -> {
                val intent = Intent(this, DeveloperLoginActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers()
    }

    private fun setupSearch() {
        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = charSequence.toString().lowercase(Locale.ROOT)
                filterTableRows(searchText)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun filterTableRows(searchText: String) {
        for (i in 0 until tableLayout!!.childCount) {
            val row = tableLayout!!.getChildAt(i) as TableRow
            val nameTextView = row.getChildAt(1) as TextView
            row.visibility = if (nameTextView.text.toString().lowercase(Locale.ROOT).contains(searchText)) View.VISIBLE else View.GONE
        }
    }

    private fun setupFilter() {
        filterSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFilter = parent.getItemAtPosition(position).toString()
                filterTableByDepartment(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterTableByDepartment(department: String) {
        for (i in 0 until tableLayout!!.childCount) {
            val row = tableLayout!!.getChildAt(i) as TableRow
            val departmentTextView = row.getChildAt(2) as TextView

            row.visibility = if (department == "All" || departmentTextView.text.toString() == department) View.VISIBLE else View.GONE
        }
    }


}