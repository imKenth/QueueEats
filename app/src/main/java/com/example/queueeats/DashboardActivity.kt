package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.UserRepository

class DashboardActivity : AppCompatActivity() {

    companion object {
        // Branch 1: JanPol Canteen 1 • Orange Stall
        const val BRANCH_1_NAME = "JanPol Canteen 1 • Orange Stall"
        // Placeholders for JanPol Canteen 1 coordinates (Poblacion, Davao City)
        const val CANTEEN_1_LATITUDE = 7.0531687  // TODO: Insert verified latitude for JanPol Canteen 1
        const val CANTEEN_1_LONGITUDE = 125.5963817 // TODO: Insert verified longitude for JanPol Canteen 1

        // Branch 2: JanPol Canteen 2 • Red Stall
        const val BRANCH_2_NAME = "JanPol Canteen 2 • Red Stall"
        // Placeholders for JanPol Canteen 2 coordinates (Calinan, Davao City)
        const val CANTEEN_2_LATITUDE = 7.1833  // TODO: Insert verified latitude for JanPol Canteen 2
        const val CANTEEN_2_LONGITUDE = 125.4556 // TODO: Insert verified longitude for JanPol Canteen 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        updateProfileUI()

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

        val btnBranchPoblacion = findViewById<View>(R.id.btnBranchPoblacion)
        val btnBranchCalinan = findViewById<View>(R.id.btnBranchCalinan)

        val locationContainerPoblacion = findViewById<View>(R.id.locationContainerPoblacion)
        val locationContainerCalinan = findViewById<View>(R.id.locationContainerCalinan)

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
            UserRepository.currentUser = null
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Branch Card Clicks (Opens Branch Details)
        btnBranchPoblacion?.setOnClickListener {
            val intent = Intent(this, Branch1Activity::class.java)
            startActivity(intent)
        }

        btnBranchCalinan?.setOnClickListener {
            val intent = Intent(this, Branch1Activity::class.java)
            startActivity(intent)
        }

        // Branch Location Clicks (Opens Map Screen)
        locationContainerPoblacion?.setOnClickListener {
            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, BRANCH_1_NAME)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, CANTEEN_1_LATITUDE)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, CANTEEN_1_LONGITUDE)
            }
            startActivity(intent)
        }

        locationContainerCalinan?.setOnClickListener {
            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, BRANCH_2_NAME)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, CANTEEN_2_LATITUDE)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, CANTEEN_2_LONGITUDE)
            }
            startActivity(intent)
        }
    }

    private fun updateProfileUI() {
        val user = UserRepository.currentUser
        if (user != null) {
            findViewById<TextView>(R.id.profile_name)?.text = user.name
            findViewById<TextView>(R.id.profile_email)?.text = user.email
            findViewById<TextView>(R.id.profile_phone)?.text = "No phone set"

            findViewById<TextView>(R.id.header_profName)?.text = user.name
        }
    }
}
