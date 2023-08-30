package com.example.wastewarrior.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wastewarrior.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        sharedPreferences = getSharedPreferences("MapPrefs", MODE_PRIVATE)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add marker on map click
        mMap.setOnMapClickListener { latLng ->
            mMap.clear() // Clear existing markers
            mMap.addMarker(MarkerOptions().position(latLng))
            saveLocationToSharedPreferences(latLng)
        }

        // Save location to Firebase
        this.findViewById<Button>(R.id.saveButton).setOnClickListener {
            val selectedLatLng = mMap.cameraPosition.target
            startActivity(Intent(this, RestaurantInfoActivity::class.java))
        }
    }
    private fun saveLocationToSharedPreferences(latLng: LatLng) {
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", latLng.latitude.toFloat())
        editor.putFloat("longitude", latLng.longitude.toFloat())
        Log.i("app",latLng.toString())
        editor.apply()
    }
}
