package com.example.coursework_1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        val btnLogIn = findViewById<Button>(R.id.loginToMainButton)
        val btnBack = findViewById<Button>(R.id.loginBackButton)
        btnLogIn.setOnClickListener{v -> login(v)}
        btnBack.setOnClickListener{back()}
    }

    private fun login(view : View) {
        val userName = findViewById<EditText>(R.id.logInUserNameEntry)
        val password = findViewById<EditText>(R.id.logInPasswordEntry)
        if (nullCheck(listOf<EditText>(userName, password))) {
            UserInformation.getAuth().signInWithEmailAndPassword(userName.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        UserInformation.getFromDatabase()?.addOnCompleteListener { task ->
                            val snapshot = task.result
                            val resetTime = snapshot.child("resetTime").getValue(String::class.java)

                            DailyResetNotification.scheduleNotification(this, resetTime)
                        }

                        UserInformation.getFromDatabase()?.addOnCompleteListener { result ->
                            val timeCheck = result.result.child("resetTime").getValue(String::class.java)
                            DailyResetNotification.scheduleNotification(this, timeCheck)

                            startActivity(Intent(this, MainScreen::class.java))
                        }
                    } else {
                        displayMessage(view, "Incorrect User Details")
                    }
                }
        } else {
            displayMessage(view, "Please fill out all fields")
        }
    }

    private fun nullCheck(fields: List<EditText>): Boolean {
        for (field in fields) {
            if (field.text.toString().trim().isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun displayMessage(view: View, text: String) {
        val sb = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    private fun back() {
        startActivity(Intent(this, LoginOrSignUp::class.java))
    }
}