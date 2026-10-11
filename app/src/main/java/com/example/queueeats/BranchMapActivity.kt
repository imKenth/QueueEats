package com.example.queueeats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class BranchMapActivity : AppCompatActivity() {

    private lateinit var branchMap: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_branch_map)

        branchMap = findViewById(R.id.branchMap)

        branchMap.setMultiTouchControls(true)

        // Example location: Davao City
        val davaoLocation = GeoPoint(7.0731, 125.6128)

        branchMap.controller.setZoom(14.0)
        branchMap.controller.setCenter(davaoLocation)

        // Example restaurant branch marker
        val marker = Marker(branchMap)
        marker.position = davaoLocation
        marker.title = "QueueEats Branch"
        marker.snippet = "Example restaurant location"

        branchMap.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        branchMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        branchMap.onPause()
    }
}