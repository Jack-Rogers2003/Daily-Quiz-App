package com.example.coursework_1

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity


class LoginOrSignUp: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UserInformation.getCurrentUser() != null) {
            startActivity(Intent(this, MainScreen::class.java))
        }
        setContentView(R.layout.login_or_signup_activity)
        val btnSignUp = findViewById<Button>(R.id.signUpButton)
        btnSignUp.setOnClickListener{v -> signUpButtonClicked()}
        val btnLogin = findViewById<Button>(R.id.loginButton)
        btnLogin.setOnClickListener{v -> loginButtonClicked()}
    }

    private fun signUpButtonClicked() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun loginButtonClicked() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}