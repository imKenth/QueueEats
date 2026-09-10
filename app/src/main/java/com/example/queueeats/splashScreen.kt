package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class splashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display for the splash screen background.
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Part 1: Immersive Mode logic. 
        // This hides the status bar and navigation bar for a completely clean splash experience.
        //val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        //windowInsetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        //windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Part 2: Window Insets Listener.
        // This ensures the decorative background circles can bleed all the way to the screen edges.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        // Navigation delay: Waits 3 seconds before moving to the main sign-in screen.
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}