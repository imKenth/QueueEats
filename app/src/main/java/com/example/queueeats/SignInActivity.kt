package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signinscreen)

        // Inside onCreate
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val formScroll = findViewById<android.view.View>(R.id.form_scroll)
            formScroll?.setPadding(0, 0, 0, systemBars.bottom)

            insets
        }

        // Logic to switch to Sign Up screen
        findViewById<Button>(R.id.proceed_signupscreen).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val usernameInput = findViewById<EditText>(R.id.usernameInput)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)


        // Logic to switch to dashboard screen
        findViewById<Button>(R.id.signin_button).setOnClickListener {
            if(usernameInput.text.toString() == "kenthdaryl@gmail.com" && passwordInput.text.toString() == "12345678") {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } else {

            }
        }
    }
}