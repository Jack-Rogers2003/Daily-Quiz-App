package com.example.coursework_1

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val resetTimeButton = findViewById<Button>(R.id.changeResetTimeSetting)
        resetTimeButton.setOnClickListener{showTimePicker()}

        val passwordResetButton = findViewById<Button>(R.id.changePasswordSettings)
        passwordResetButton.setOnClickListener{showTextInputDialog()}

        val backButton = findViewById<Button>(R.id.settingsExit)
        backButton.setOnClickListener{backButton()}

    }

    private fun backButton() {
        startActivity(Intent(this, MainScreen::class.java))
    }

    private fun showTextInputDialog() {
        val firstInput = EditText(this)
        val secondInput = EditText(this)

        // Set hints for the EditText fields
        firstInput.hint = "Enter text"
        secondInput.hint = "Confirm text"

        // Create a LinearLayout to hold the two EditText fields
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.addView(firstInput)
        layout.addView(secondInput)

        // Build the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("Please your new password twice")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                // Get the entered text
                val pass1 = firstInput.text.toString()
                val pass2 = secondInput.text.toString()

                // Check if the texts match
                if (pass1 == pass2) {
                    UserInformation.getCurrentUser()?.updatePassword(pass1)?.addOnCompleteListener {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                } else {
                    Toast.makeText(this, "Text does not match!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                showConfirmationDialog(hour, minute)
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun showConfirmationDialog(selectedHour: Int, selectedMinute: Int) {
        val message = buildString {
        append("You selected ")
        append(selectedHour)
        append(":")
        append(String.format("%02d", selectedMinute))
        append(". Do you want to confirm?")
    }

        val confirmationDialog = AlertDialog.Builder(this)
            .setTitle("Confirm Time Selection")
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed the time selection
                // Proceed with the selected time or further actions
                val timeString = if (selectedHour < 10 && selectedMinute > 10) {
                    "0$selectedHour:$selectedMinute"
                } else if (selectedMinute < 10 && selectedHour > 10) {
                    "$selectedHour:0$selectedMinute"
                } else if (selectedHour < 10 && selectedMinute < 10) {
                    "0$selectedHour:0$selectedMinute"
                } else {
                    "$selectedHour:$selectedMinute"
                }

                UserInformation.setDatabase("resetTime", timeString)
                DailyResetNotification.updateAlarm(this, timeString)
                UserInformation.setDatabase("resetTime", timeString)
            }
            .setNegativeButton("No") { _, _ ->
                // User declined, handle accordingly
                println("Time selection canceled")
            }
            .create()

        confirmationDialog.show()
    }
}