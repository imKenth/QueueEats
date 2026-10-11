package com.example.queueeats

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class BranchMapActivity : AppCompatActivity() {

    private lateinit var branchMap: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_branch_map)

        findViewById<ImageView>(R.id.mapBack).setOnClickListener {
            finish()
        }

        branchMap = findViewById(R.id.branchMap)
        branchMap.setTileSource(TileSourceFactory.MAPNIK)
        branchMap.setMultiTouchControls(true)

        // City-level preview only. This is NOT an exact QueueEats branch location.
        val davaoCityCenter = GeoPoint(7.0731, 125.6128)
        branchMap.controller.setZoom(13.5)
        branchMap.controller.setCenter(davaoCityCenter)

        // A clearly labelled preview marker avoids presenting unverified coordinates
        // as the real location of either JanPol canteen branch.
        val previewMarker = Marker(branchMap).apply {
            position = davaoCityCenter
            title = "Davao City map preview"
            snippet = "Add verified coordinates for JanPol Canteen 1 and 2."
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        branchMap.overlays.add(previewMarker)
        branchMap.invalidate()
    }

    override fun onResume() {
        super.onResume()
        if (::branchMap.isInitialized) branchMap.onResume()
    }

    override fun onPause() {
        if (::branchMap.isInitialized) branchMap.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        if (::branchMap.isInitialized) branchMap.onDetach()
        super.onDestroy()
    }
}