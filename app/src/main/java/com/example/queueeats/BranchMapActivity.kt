package com.example.queueeats

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

class BranchMapActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BRANCH_NAME = "EXTRA_BRANCH_NAME"
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
        const val INVALID_COORDINATE = -999.0

        // Public OpenStreetMap tile servers (Free, No API key, No 403 blocks)
        val PUBLIC_OSM_TILE_SOURCE = XYTileSource(
            "OSM_Public",
            0, 19, 256, ".png",
            arrayOf(
                "https://a.tile.openstreetmap.fr/osmfr/",
                "https://b.tile.openstreetmap.fr/osmfr/",
                "https://c.tile.openstreetmap.fr/osmfr/",
                "https://tile.openstreetmap.de/",
            ),
        )
    }

    private lateinit var branchMap: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ctx = applicationContext
        val config = Configuration.getInstance()
        config.userAgentValue = "QueueEatsApp/1.0 (Android Mobile)"
        config.load(ctx, ctx.getSharedPreferences("osmdroid_config", MODE_PRIVATE))

        // Clear local cache directory to delete any previously cached 403 error tiles
        try {
            val cacheDir = File(ctx.cacheDir, "osmdroid")
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        } catch (_: Exception) {
            // Ignore cache cleanup errors
        }

        setContentView(R.layout.activity_branch_map)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        branchMap = findViewById(R.id.branchMap)
        branchMap.setTileSource(PUBLIC_OSM_TILE_SOURCE)
        branchMap.setMultiTouchControls(true)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val tvMapBranchName = findViewById<TextView>(R.id.tv_map_branch_name)
        val tvNoLocationMsg = findViewById<TextView>(R.id.tv_no_location_msg)

        btnBack?.setOnClickListener {
            finish()
        }

        val branchName = intent.getStringExtra(EXTRA_BRANCH_NAME) ?: "QueueEats Branch"
        val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, INVALID_COORDINATE)
        val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, INVALID_COORDINATE)

        tvMapBranchName?.text = branchName

        if (isValidCoordinate(latitude, longitude)) {
            val branchLocation = GeoPoint(latitude, longitude)

            branchMap.controller.setZoom(16.5)
            branchMap.controller.setCenter(branchLocation)

            val marker = Marker(branchMap)
            marker.position = branchLocation
            marker.title = branchName
            marker.snippet = branchName
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            branchMap.overlays.add(marker)
            marker.showInfoWindow()
            tvNoLocationMsg?.visibility = View.GONE
        } else {
            tvNoLocationMsg?.text = "Location coordinates are currently unavailable for $branchName."
            tvNoLocationMsg?.visibility = View.VISIBLE
        }
    }

    private fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return (!lat.isNaN()) && (!lng.isNaN()) &&
                (lat >= -90.0) && (lat <= 90.0) &&
                (lng >= -180.0) && (lng <= 180.0)
    }

    override fun onResume() {
        super.onResume()
        branchMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        branchMap.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        branchMap.onDetach()
    }
}
