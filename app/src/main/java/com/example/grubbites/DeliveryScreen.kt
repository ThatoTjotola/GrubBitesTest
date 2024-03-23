package com.example.grubbites

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.grubbites.databinding.ActivityDeliveryScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class DeliveryScreen : AppCompatActivity() {
    private lateinit var binding:ActivityDeliveryScreenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lng: Double?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.UserAddress.setOnClickListener {
            getLocation()
            useLocation()

        }
        binding.btnConfirmDelivery.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Set the location in the EditText
                location?.let {
                    Log.d("Location","${location.latitude} & ${location.longitude}")
                    lat = location.latitude
                    lng = location.longitude
                    updateUIWithLocation(location)
                }
            }
            .addOnFailureListener { e: Exception ->
                // Handle location retrieval failure
                Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun useLocation() {
        val latitude = lat
        val longitude = lng

        // Use the latitude and longitude as needed
        Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT)
            .show()
    }
    private fun updateUIWithLocation(location: Location) {
        val geocoder = Geocoder(this)
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        lat = location.latitude
        lng = location.longitude
        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val locationText = address.getAddressLine(0)
            binding.UserAddress.setText(locationText)
        }
    }
}