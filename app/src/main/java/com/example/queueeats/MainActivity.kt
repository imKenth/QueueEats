package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display, allowing the app content to draw behind system bars.
        enableEdgeToEdge()
        setContentView(R.layout.signinscreen)

        // Part 2: Window Insets Listener. 
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Logic to switch to Sign Up screen
        findViewById<Button>(R.id.proceed_signupscreen).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val usernameInput = findViewById<EditText>(R.id.usernameInput)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)






    }
}