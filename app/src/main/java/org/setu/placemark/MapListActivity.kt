package org.setu.placemark

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

class MapListActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // osmdroid requires a user-agent string to download map tiles
        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_list)

        // Initialise views
        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)

        // Configure OpenStreetMap
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Show map attribution
        mapView.overlays.add(CopyrightOverlay(this))

        // Load placemarks onto the map
        loadMarkers()
    }

    private fun loadMarkers() {
        val placemarks = AppData.placedMarkList.findAll()

        placemarks.forEach { placemark ->
            val position = GeoPoint(
                placemark.x,
                placemark.y
            )

            val marker = Marker(mapView)
            marker.position = position
            marker.title = placemark.title
            marker.snippet = placemark.desc
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // Tap a marker to show its details in the panel below the map
            marker.setOnMarkerClickListener { _, _ ->
                displayPlace(placemark)
                true
            }

            mapView.overlays.add(marker)
        }

        // Centre map on the first placemark if any exist
        placemarks.firstOrNull()?.let { first ->
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(first.x, first.y))
        }

        mapView.invalidate()
    }

    private fun displayPlace(placemark: PlacedMark) {
        txtTitle.text = placemark.title
        txtDescription.text = placemark.desc

        if (placemark.imageUri != null) {
            imgPlace.setImageURI(placemark.imageUri!!.toUri())
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Free tile cache when the system is low on memory
        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}
