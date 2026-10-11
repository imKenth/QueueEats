package com.example.queueeats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository

/**
 * DashboardActivity represents the main home screen of QueueEats.
 * Displays available food stall branches, bottom navigation, and profile view.
 */
class DashboardActivity : AppCompatActivity() {

    companion object {
        // =====================================================================
        // BRANCH CONSTANTS & COORDINATES
        // =====================================================================

        /** Branch 1: Janpol Ecoland Stall Orange */
        const val BRANCH_1_NAME = "Janpol Ecoland Stall Orange"
        const val CANTEEN_1_LATITUDE = 7.0531687
        const val CANTEEN_1_LONGITUDE = 125.5963817

        /** Branch 2: Janpol Calinan stall red */
        const val BRANCH_2_NAME = "Janpol Calinan stall red"
        const val CANTEEN_2_LATITUDE = 7.1891016
        const val CANTEEN_2_LONGITUDE = 125.4553135
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        updateProfileUI()

        // Handle edge-to-edge window insets padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logButton = findViewById<Button>(R.id.logButton)

        // Bottom Navigation UI elements
        val navHome = findViewById<FrameLayout>(R.id.navHome)
        val navMore = findViewById<FrameLayout>(R.id.navMore)

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val moreIcon = findViewById<ImageView>(R.id.moreIcon)

        val homeContent = findViewById<View>(R.id.home_content)
        val profileContent = findViewById<View>(R.id.profile_content)

        // Branch Card view references
        val btnBranchPoblacion = findViewById<View>(R.id.btnBranchPoblacion)
        val btnBranchCalinan = findViewById<View>(R.id.btnBranchCalinan)

        // Branch Location Area view references
        val locationContainerPoblacion = findViewById<View>(R.id.locationContainerPoblacion)
        val locationContainerCalinan = findViewById<View>(R.id.locationContainerCalinan)

        // Secret Staff Portal access: Tap app logo 3 times consecutively
        var logoClickCount = 0
        var lastLogoClickTime = 0L
        val appLogoHeader = findViewById<ImageView>(R.id.app_logo_header)
        appLogoHeader?.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastLogoClickTime > 1500) {
                logoClickCount = 1
            } else {
                logoClickCount++
            }
            lastLogoClickTime = currentTime

            if (logoClickCount >= 3) {
                logoClickCount = 0
                showStaffPasswordDialog()
            }
        }

        // Switch to Home Tab
        navHome?.setOnClickListener {
            navHome.setBackgroundResource(R.drawable.bg_navbar_selected)
            navMore?.background = null

            homeIcon?.setColorFilter(Color.WHITE)
            moreIcon?.setColorFilter(Color.BLACK)

            homeContent?.visibility = View.VISIBLE
            profileContent?.visibility = View.GONE
        }

        // Switch to Profile Tab
        navMore?.setOnClickListener {
            navMore.setBackgroundResource(R.drawable.bg_navbar_selected)
            navHome?.background = null

            homeIcon?.setColorFilter(Color.BLACK)
            moreIcon?.setColorFilter(Color.WHITE)

            homeContent?.visibility = View.GONE
            profileContent?.visibility = View.VISIBLE
        }

        // Log out button listener
        logButton?.setOnClickListener {
            UserRepository.currentUser = null
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Profile Screen Action Buttons
        findViewById<View>(R.id.btn_profile_my_orders)?.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        // ---------------------------------------------------------------------
        // BRANCH CARD CLICK LISTENERS -> Opens Branch Details Screen
        // ---------------------------------------------------------------------
        btnBranchPoblacion?.setOnClickListener {
            val intent = Intent(this, Branch1Activity::class.java).apply {
                putExtra(Branch1Activity.EXTRA_BRANCH_ID, CanteenRepository.BRANCH_1_ID)
            }
            startActivity(intent)
        }

        btnBranchCalinan?.setOnClickListener {
            val intent = Intent(this, Branch2Activity::class.java).apply {
                putExtra(Branch2Activity.EXTRA_BRANCH_ID, CanteenRepository.BRANCH_2_ID)
            }
            startActivity(intent)
        }

        // ---------------------------------------------------------------------
        // BRANCH LOCATION AREA CLICK LISTENERS -> Opens Map Screen with Coordinates
        // ---------------------------------------------------------------------

        // Click handler for Janpol Ecoland Stall Orange location area
        locationContainerPoblacion?.setOnClickListener {
            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, BRANCH_1_NAME)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, CANTEEN_1_LATITUDE)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, CANTEEN_1_LONGITUDE)
            }
            startActivity(intent)
        }

        // Click handler for Janpol Calinan stall red location area
        locationContainerCalinan?.setOnClickListener {
            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, BRANCH_2_NAME)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, CANTEEN_2_LATITUDE)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, CANTEEN_2_LONGITUDE)
            }
            startActivity(intent)
        }
    }

    private fun showStaffPasswordDialog() {
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Enter PIN (123)"
            setPadding(48, 24, 48, 24)
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
            addView(TextView(this@DashboardActivity).apply {
                text = "Staff Portal Security\nEnter security password to unlock:"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
            })
            addView(input)
        }

        AlertDialog.Builder(this)
            .setTitle("Staff Portal Access")
            .setView(container)
            .setPositiveButton("Unlock") { _, _ ->
                val pin = input.text.toString().trim()
                if (pin == "123") {
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StaffPortalActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
