package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.UserRepository

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signinscreen)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signinButton = findViewById<Button>(R.id.signin_button)
        val feedbackText = findViewById<TextView>(R.id.signin_feedback)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val formScroll = findViewById<View>(R.id.form_scroll)
            formScroll?.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        // Logic to switch to Sign Up screen
        findViewById<Button>(R.id.proceed_signupscreen).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Logic to switch to dashboard screen
        signinButton.setOnClickListener {
            val email = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            // Reset feedback
            feedbackText.visibility = View.GONE

            val user = UserRepository.validateUser(email, password)

            if (user != null) {
                UserRepository.currentUser = user
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                feedbackText.text = "Invalid email or password"
                feedbackText.setTextColor(Color.RED)
                feedbackText.visibility = View.VISIBLE
            }
        }
    }
}
