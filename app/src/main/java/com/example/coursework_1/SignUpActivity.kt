package com.example.coursework_1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class SignUpActivity : AppCompatActivity() {
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        val btnSignUp = findViewById<Button>(R.id.signUpButton)
        val btnBack = findViewById<Button>(R.id.signUpBackButton)
        val topicsSpinner = findViewById<Spinner>(R.id.selectTopic)
        val btnTime = findViewById<Button>(R.id.dailyResetTimeSignUpButton)
        val numOfQuestionsSpinner = findViewById<Spinner>(R.id.selectNumOfQuestions)

        val topicsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.topics))
        topicsSpinner.adapter = topicsAdapter
        val numOfQuestionsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            (1..15).toList())
        numOfQuestionsSpinner.adapter = numOfQuestionsAdapter

        btnTime.setOnClickListener{showTimePicker()}
        btnSignUp.setOnClickListener{signUp()}
        btnBack.setOnClickListener{backButtonClicked()}
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun signUp() {
        val userName = findViewById<EditText>(R.id.userNameEntry)
        val email = findViewById<EditText>(R.id.emailEntry)
        val password = findViewById<EditText>(R.id.enterPassword)
        val numOfQuestions = findViewById<Spinner>(R.id.selectNumOfQuestions)
        val topic = findViewById<Spinner>(R.id.selectTopic)
        val textFields = listOf<EditText>(userName, password)
        val nullCheck = nullCheck(textFields)

        if(password.text.toString().trim().length < 6 ) {
            buildAlertBox("Error", "Password must be a length of 6 or more")
        } else if (nullCheck && topic.selectedItem != null && numOfQuestions.selectedItem != null) {
            UserInformation.getAuth().createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val timeString = if (selectedHour < 10 && selectedMinute > 10) {
                            "0$selectedHour:$selectedMinute"
                        } else if (selectedMinute < 10 && selectedHour > 10) {
                            "$selectedHour:0$selectedMinute"
                        } else if (selectedHour < 10 && selectedMinute < 10) {
                            "0$selectedHour:0$selectedMinute"
                        } else {
                            "$selectedHour:$selectedMinute"
                        }
                        val stringMap = mapOf(
                            "username" to userName.text.toString(),
                            "numQuestions" to numOfQuestions.selectedItem.toString(),
                            "topic" to topic.selectedItem.toString(),
                            "type" to "True or False",
                            "resetTime" to timeString,
                            "lastPlayed" to LocalDate.now().format(
                                DateTimeFormatter.
                                ofPattern("yyyy-MM-dd")),
                            "playedToday" to "false"
                        )
                        addToDatabase(stringMap)
                        DailyResetNotification.scheduleNotification(this, timeString)
                        startActivity(Intent(this, MainScreen::class.java))
                    } else {
                        buildAlertBox("Error", "Username already in use")
                    }
            }

        } else {
            buildAlertBox("Error", "All fields must be filled in")
        }
    }

    private fun addToDatabase(stringMap : Map<String, String>) {
        val database = UserInformation.getDatabase()
        val userID = UserInformation.getCurrentUserID()
        val ref = database.getReference(userID.toString())
        ref.setValue(stringMap)
    }

    private fun nullCheck(fields: List<EditText>): Boolean {
        for (field in fields) {
            if (field.text.toString().trim().isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Store the selected time in variables
                selectedHour = hourOfDay
                selectedMinute = minute
            },
            currentHour,
            currentMinute,
            true // true for 24-hour format, false for 12-hour format
        )

        timePickerDialog.show()
    }

    private fun backButtonClicked() {
        val newIntent = Intent(this, LoginOrSignUp::class.java)
        startActivity(newIntent)
    }

    private fun buildAlertBox(title: String, text: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(text)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


}