package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.UserRepository
import com.example.queueeats.models.User

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signupButton = findViewById<Button>(R.id.signup_button)
        val feedbackText = findViewById<TextView>(R.id.signup_feedback)
        val termsCheckbox = findViewById<CheckBox>(R.id.terms_checkbox)
        val termsText = findViewById<TextView>(R.id.terms_text)

        // Set terms text with HTML styling
        termsText.text = Html.fromHtml(getString(R.string.terms_agreement), Html.FROM_HTML_MODE_LEGACY)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Logic for Sign Up
        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            // Reset feedback state
            feedbackText.visibility = View.GONE

            // 1. Validate Name
            if (name.isEmpty()) {
                showError("Please fill the name field")
                return@setOnClickListener
            }
            if (name.any { it.isDigit() }) {
                showError("Name cannot contain a number")
                return@setOnClickListener
            }

            // 2. Validate Email
            if (email.isEmpty()) {
                showError("Please fill the email field")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Please enter a valid email address")
                return@setOnClickListener
            }

            // 3. Validate Password
            if (password.isEmpty()) {
                showError("Password cannot be empty")
                return@setOnClickListener
            }
            if (password.length < 8) {
                showError("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            // 4. Validate Terms Agreement
            if (!termsCheckbox.isChecked) {
                showError(getString(R.string.must_agree_terms))
                return@setOnClickListener
            }

            // 5. Success Case
            val newUser = User(name, email, password)
            UserRepository.addUser(newUser)
            
            feedbackText.text = "Registration Successful for ${newUser.name}"
            feedbackText.visibility = View.VISIBLE
            feedbackText.setTextColor(Color.GREEN)



            //Navigate to Sign In after a short delay
            Toast.makeText(this, "Success! Redirecting...", Toast.LENGTH_SHORT).show()
            signupButton.postDelayed({
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)
        }

        // Logic to switch back to Sign In screen
        findViewById<Button>(R.id.goto_signin).setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showError(message: String) {
        val feedbackText = findViewById<TextView>(R.id.signup_feedback)
        feedbackText.text = message
        feedbackText.visibility = View.VISIBLE
        feedbackText.setTextColor(Color.RED)
    }
}
