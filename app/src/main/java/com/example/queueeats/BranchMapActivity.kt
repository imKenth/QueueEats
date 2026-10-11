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

/**
 * BranchMapActivity displays the interactive OpenStreetMap location for a selected food stall branch.
 * Uses OSMDroid for rendering interactive tiles, markers, zooming, and panning.
 */
class BranchMapActivity : AppCompatActivity() {

    companion object {
        // Intent Extra Keys passed from DashboardActivity
        const val EXTRA_BRANCH_NAME = "EXTRA_BRANCH_NAME"
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"

        // Sentinel value used when coordinate extras are missing or default
        const val INVALID_COORDINATE = -999.0

        /**
         * Public OpenStreetMap regional tile mirrors (a.tile.openstreetmap.fr & tile.openstreetmap.de).
         * Free, open-source, requires no API key, and accepts mobile client connections without 403 blocks.
         */
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

        // ---------------------------------------------------------------------
        // OSMDROID CONFIGURATION
        // ---------------------------------------------------------------------
        val ctx = applicationContext
        val config = Configuration.getInstance()

        // Configure custom User-Agent to conform to OpenStreetMap foundation usage policy
        config.userAgentValue = "QueueEatsApp/1.0 (Android Mobile)"
        config.load(ctx, ctx.getSharedPreferences("osmdroid_config", MODE_PRIVATE))

        // Clear local disk tile cache directory on launch to purge any stale/error tiles
        try {
            val cacheDir = File(ctx.cacheDir, "osmdroid")
            if (cacheDir.exists()) {
                cacheDir.deleteRecursively()
            }
        } catch (_: Exception) {
            // Ignore cache cleanup errors
        }

        setContentView(R.layout.activity_branch_map)

        // Handle window system bar insets (edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mapRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize MapView and configure tile source & gestures
        branchMap = findViewById(R.id.branchMap)
        branchMap.setTileSource(PUBLIC_OSM_TILE_SOURCE)
        branchMap.setMultiTouchControls(true) // Enables pinch-to-zoom and multi-touch panning

        // UI references
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val tvMapBranchName = findViewById<TextView>(R.id.tv_map_branch_name)
        val tvNoLocationMsg = findViewById<TextView>(R.id.tv_no_location_msg)

        // Back button listener -> returns user to previous screen
        btnBack?.setOnClickListener {
            finish()
        }

        // ---------------------------------------------------------------------
        // INTENT EXTRAS & MAP MARKER SETUP
        // ---------------------------------------------------------------------
        val branchName = intent.getStringExtra(EXTRA_BRANCH_NAME) ?: "QueueEats Branch"
        val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, INVALID_COORDINATE)
        val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, INVALID_COORDINATE)

        // Set header label
        tvMapBranchName?.text = branchName

        // Validate coordinates before centering and placing marker
        if (isValidCoordinate(latitude, longitude)) {
            val branchLocation = GeoPoint(latitude, longitude)

            // Center map on branch location and set zoom level (16.5 = street/building level)
            branchMap.controller.setZoom(16.5)
            branchMap.controller.setCenter(branchLocation)

            // Add marker at branch coordinates
            val marker = Marker(branchMap)
            marker.position = branchLocation
            marker.title = branchName
            marker.snippet = branchName
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            branchMap.overlays.add(marker)
            marker.showInfoWindow() // Automatically display marker label
            tvNoLocationMsg?.visibility = View.GONE
        } else {
            // Display friendly fallback message if coordinates are invalid
            tvNoLocationMsg?.text = "Location coordinates are currently unavailable for $branchName."
            tvNoLocationMsg?.visibility = View.VISIBLE
        }
    }

    /**
     * Helper method to verify if a latitude and longitude pair represents a valid geographic position.
     */
    private fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return (!lat.isNaN()) && (!lng.isNaN()) &&
                (lat >= -90.0) && (lat <= 90.0) &&
                (lng >= -180.0) && (lng <= 180.0)
    }

    // -------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE MANAGEMENT FOR OSMDROID
    // -------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()
        branchMap.onResume() // Resume tile downloading and map rendering
    }

    override fun onPause() {
        super.onPause()
        branchMap.onPause() // Pause tile downloading when activity is paused
    }

    override fun onDestroy() {
        super.onDestroy()
        branchMap.onDetach() // Free map resources and avoid memory leaks
    }
}
