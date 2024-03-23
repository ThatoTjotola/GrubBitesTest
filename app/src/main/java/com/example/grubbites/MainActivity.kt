package com.example.grubbites

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grubbites.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil.decode
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import io.radar.sdk.Radar
import io.radar.sdk.Radar.startTracking
import io.radar.sdk.RadarTrackingOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var currentLocationMarker: Marker? = null
    private var polyline: Polyline? = null
    private val foodIcon = R.drawable.food
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Radar.initialize(this, "prj_test_pk_6a7e3d168b9e9840628bb4d3b9be0428456dbf2c")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeScreen::class.java))
                    true
                }
                R.id.cart -> {
                    startActivity(Intent(applicationContext, CartScreen::class.java))
                    true
                }
                R.id.profile -> true
                R.id.search -> true
                else -> false
            }
        }
    }

    private fun startLiveTracking() {
        val origin = Location("mock")
        origin.latitude = -25.984081
        origin.longitude = 28.095185

        val destination = Location("mock")
        destination.latitude = -25.944710
        destination.longitude = 28.116150

        Radar.mockTracking(
            generateMockOriginLocation(),
            generateMockDestinationLocation(),
            Radar.RadarRouteMode.CAR,
            1,
            2
        ) { status, location, _, _ ->
            if (status == Radar.RadarStatus.SUCCESS) {
                updateMapWithLocation(location!!)
            } else {
                println("Error with tracking: ${status}")
            }
        }
    }

    private fun updateMapWithLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        if (currentLocationMarker != null) {
            currentLocationMarker?.position = currentLatLng
        } else {
            val foodIconBitmap = BitmapFactory.decodeResource(resources, foodIcon)
            val scaledFoodIconBitmap = Bitmap.createScaledBitmap(foodIconBitmap, 100, 100, false)
            val theFoodIcon = BitmapDescriptorFactory.fromBitmap(scaledFoodIconBitmap)
            val anchorX = 0.5f
            val anchorY = 1.0f
            currentLocationMarker = mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location").icon(theFoodIcon).anchor(anchorX, anchorY))
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))

        val destinationLatLng =
            LatLng(generateMockDestinationLocation().latitude, generateMockDestinationLocation().longitude)

        val distanceToDestination = FloatArray(1)
        Location.distanceBetween(
            currentLatLng.latitude,
            currentLatLng.longitude,
            destinationLatLng.latitude,
            destinationLatLng.longitude,
            distanceToDestination
        )

        if (distanceToDestination[0] < 30) {
            openFeedbackActivity()
        }
    }

    private fun openFeedbackActivity() {
        val intent = Intent(applicationContext, FeedBackActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun generateMockOriginLocation(): Location {
        val origin = Location("mock")
        origin.latitude = -26.022573522942356
        origin.longitude = 28.132459026235427
        return origin
    }

    private fun generateMockDestinationLocation(): Location {
        val receivedLat = intent.getDoubleExtra("LATITUDE_KEY", 0.0)
        val receivedLng = intent.getDoubleExtra("LONGITUDE_KEY", 0.0)
        val destination = Location("mock")
        destination.latitude = -26.023097925294163
        destination.longitude = 28.13265788869197
        return destination
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialLatLng =
            LatLng(generateMockOriginLocation().latitude, generateMockOriginLocation().longitude)
        mMap.addMarker(MarkerOptions().position(initialLatLng).title("Order Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15f))

        val destinationLatLng =
            LatLng(generateMockDestinationLocation().latitude, generateMockDestinationLocation().longitude)
        mMap.addMarker(MarkerOptions().position(destinationLatLng).title("Destination Location"))

        startLiveTracking()
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val apiKey = "AIzaSyAcnPpN2K87hgK2IIjDyqReQlIPjU41kvo"

        val directionsResult = getDirectionsResult(origin, destination, apiKey)
        val route = extractRoute(directionsResult)
        drawPolyline(route)
    }

    private fun getDirectionsResult(origin: LatLng, destination: LatLng, apiKey: String): DirectionsResult {
        return com.google.maps.DirectionsApi.newRequest(GeoApiContext.Builder().apiKey(apiKey).build())
            .mode(TravelMode.DRIVING)
            .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
            .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
            .await()
    }

    private fun extractRoute(directionsResult: DirectionsResult): List<LatLng> {
        val route = mutableListOf<LatLng>()
        val legs = directionsResult.routes[0].legs

        for (leg in legs) {
            val steps = leg.steps
            for (step in steps) {
                val path = PolylineOptions().addAll(decode(step.polyline.encodedPath))
                mMap.addPolyline(path)
            }
        }

        return route
    }

    private fun drawPolyline(route: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(route)
            .color(Color.BLUE)
            .width(5f)

        polyline?.remove()
        polyline = mMap.addPolyline(polylineOptions)
    }
}
