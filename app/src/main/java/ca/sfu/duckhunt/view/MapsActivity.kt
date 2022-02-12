package ca.sfu.duckhunt.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.ListView
import androidx.core.app.ActivityCompat
import ca.sfu.duckhunt.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ca.sfu.duckhunt.databinding.ActivityMapsBinding
import ca.sfu.duckhunt.model.WaterBody
import ca.sfu.duckhunt.model.WaterBodyAdapter
import ca.sfu.duckhunt.model.Route
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.Places

import okhttp3.OkHttpClient
import okhttp3.Request


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userPosition : LatLng
    private lateinit var currentDestination : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val listView = findViewById<ListView>(R.id.list)
        val waterList = ArrayList<WaterBody>()
        waterList.add(WaterBody(true, "Bear Creek", 520, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Hunt Brook", 857, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Enver Creek", 900, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Surrey Lake", 1000, LatLng(0.0,0.0)))

        /*
        Places.initialize(applicationContext, R.string.api_key.toString())
        val placesClient = Places.createClient(this)
        val placeList = ArrayList<Place>()
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/place/textsearch/json?query=123%20main%20street&location=42.3675294%2C-71.186966&radius=10000&key=C6:1A:9D:9F:15:E5:21:E1:02:FF:24:46:89:01:22:B8:AF:92:CA:2C")
            .method("GET", null)
            .build()
        val response = client.newCall(request).execute()*/

        val waterBodyAdapter = WaterBodyAdapter(this, R.layout.adapter_place, waterList, activity = MapsActivity())
        listView.adapter = waterBodyAdapter
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationPermissionCheck()
        mMap.isMyLocationEnabled = true

        //Erase later on
        val exampleDestination = LatLng(49.19233877677021, -122.77337166712296)

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 30000

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    userPosition = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 13F))
                    generateRouteTo(mMap, userPosition, exampleDestination)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun generateRouteTo(map : GoogleMap, userPosition : LatLng, destination : LatLng) {
        mMap.addMarker(MarkerOptions().position(userPosition).title("Origin"))
        mMap.addMarker(MarkerOptions().position(destination).title("Destination"))
        val currentRoute = Route(userPosition, destination, map, this)
        currentRoute.generateRoute()
    }

    private fun locationPermissionCheck() {
        while (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                10
            )
        }
    }
}