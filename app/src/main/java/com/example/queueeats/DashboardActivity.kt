package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logButton = findViewById<Button>(R.id.logButton)

        // Navigation Logic
        val navHome = findViewById<FrameLayout>(R.id.navHome)
        val navMore = findViewById<FrameLayout>(R.id.navMore)

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val moreIcon = findViewById<ImageView>(R.id.moreIcon)

        val homeContent = findViewById<View>(R.id.home_content)
        val profileContent = findViewById<View>(R.id.profile_content)

        navHome?.setOnClickListener {
            navHome.setBackgroundResource(R.drawable.bg_navbar_selected)
            navMore?.background = null

            homeIcon?.setColorFilter(Color.WHITE)
            moreIcon?.setColorFilter(Color.BLACK)

            homeContent?.visibility = View.VISIBLE
            profileContent?.visibility = View.GONE
        }

        navMore?.setOnClickListener {
            navMore.setBackgroundResource(R.drawable.bg_navbar_selected)
            navHome?.background = null

            homeIcon?.setColorFilter(Color.BLACK)
            moreIcon?.setColorFilter(Color.WHITE)

            homeContent?.visibility = View.GONE
            profileContent?.visibility = View.VISIBLE
        }

        logButton?.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}