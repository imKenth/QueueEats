package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Branch1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_branch_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.branch1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.location_row)?.setOnClickListener {
            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, DashboardActivity.BRANCH_1_NAME)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, DashboardActivity.CANTEEN_1_LATITUDE)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, DashboardActivity.CANTEEN_1_LONGITUDE)
            }
            startActivity(intent)
        }
    }
}
