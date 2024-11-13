package com.example.finalapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "UserDatabase.db"
        const val DATABASE_VERSION = 3 // Increment version to trigger onUpgrade

        // Table for users
        const val USERS_TABLE = "users"
        const val USER_ID = "id"
        const val USER_USERNAME = "username"
        const val USER_PASSWORD = "password"
        const val USER_EMAIL = "email"

        // Table for developers
        const val DEVELOPERS_TABLE = "developers"
        const val DEV_ID = "id"
        const val DEV_USERNAME = "username"
        const val DEV_PASSWORD = "password"
        const val DEV_EMAIL = "email"

        // Table for students
        const val STUDENTS_TABLE = "students"
        const val STUDENT_ID = "id"
        const val STUDENT_NAME = "name"
        const val STUDENT_USER_TYPE = "user_type"
        const val STUDENT_DEPARTMENT = "department"

        // Table for faculty and staff
        const val FACULTY_STAFF_TABLE = "faculty_staff"
        const val FACULTY_STAFF_ID = "id"
        const val FACULTY_STAFF_NAME = "name"
        const val FACULTY_STAFF_USER_TYPE = "user_type"
        const val FACULTY_STAFF_DEPARTMENT = "department"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the users table
        val createUsersTable = ("CREATE TABLE $USERS_TABLE ("
                + "$USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$USER_USERNAME TEXT, "
                + "$USER_PASSWORD TEXT, "
                + "$USER_EMAIL TEXT)")
        db?.execSQL(createUsersTable)

        // Create the developers table
        val createDevelopersTable = ("CREATE TABLE $DEVELOPERS_TABLE ("
                + "$DEV_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$DEV_USERNAME TEXT, "
                + "$DEV_PASSWORD TEXT, "
                + "$DEV_EMAIL TEXT)")
        db?.execSQL(createDevelopersTable)

        // Create students table
        val createStudentsTable = ("CREATE TABLE $STUDENTS_TABLE ("
                + "$STUDENT_ID TEXT PRIMARY KEY, "
                + "$STUDENT_NAME TEXT, "
                + "$STUDENT_USER_TYPE TEXT, "
                + "$STUDENT_DEPARTMENT TEXT)")
        db?.execSQL(createStudentsTable)

        // Create faculty and staff table
        val createFacultyStaffTable = ("CREATE TABLE $FACULTY_STAFF_TABLE ("
                + "$FACULTY_STAFF_ID TEXT PRIMARY KEY, "
                + "$FACULTY_STAFF_NAME TEXT, "
                + "$FACULTY_STAFF_USER_TYPE TEXT, "
                + "$FACULTY_STAFF_DEPARTMENT TEXT)")
        db?.execSQL(createFacultyStaffTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop tables if they exist and recreate them on version upgrade
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $DEVELOPERS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $STUDENTS_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $FACULTY_STAFF_TABLE")
        onCreate(db)
    }

    fun insertUser(username: String, password: String, email: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(USER_USERNAME, username)
            put(USER_PASSWORD, password)
            put(USER_EMAIL, email)
        }
        return db.insert(USERS_TABLE, null, values)
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $USERS_TABLE WHERE $USER_USERNAME = ? AND $USER_PASSWORD = ?",
            arrayOf(username, password)
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun isUserExists(username: String, email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $USERS_TABLE WHERE $USER_USERNAME = ? OR $USER_EMAIL = ?",
            arrayOf(username, email)
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    // Insert developer into the developers table
    fun insertDeveloper(username: String, password: String, email: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(DEV_USERNAME, username)
            put(DEV_PASSWORD, password)
            put(DEV_EMAIL, email)
        }
        return db.insert(DEVELOPERS_TABLE, null, values)
    }

    // Check developer credentials for login
    fun checkDeveloper(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $DEVELOPERS_TABLE WHERE $DEV_USERNAME = ? AND $DEV_PASSWORD = ?",
            arrayOf(username, password)
        )
        val developerExists = cursor.count > 0
        cursor.close()
        return developerExists
    }

    // Check if a developer exists (used for initial admin account setup)
    fun isDeveloperExists(username: String, email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $DEVELOPERS_TABLE WHERE $DEV_USERNAME = ? OR $DEV_EMAIL = ?",
            arrayOf(username, email)
        )
        val developerExists = cursor.count > 0
        cursor.close()
        return developerExists
    }

    fun insertStudent(id: String, name: String, userType: String, department: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(STUDENT_ID, id)
            put(STUDENT_NAME, name)
            put(STUDENT_USER_TYPE, userType)
            put(STUDENT_DEPARTMENT, department)
        }
        return db.insert(STUDENTS_TABLE, null, values)
    }

    fun insertFacultyStaff(id: String, name: String, userType: String, department: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(FACULTY_STAFF_ID, id)
            put(FACULTY_STAFF_NAME, name)
            put(FACULTY_STAFF_USER_TYPE, userType)
            put(FACULTY_STAFF_DEPARTMENT, department)
        }
        return db.insert(FACULTY_STAFF_TABLE, null, values)
    }

}
