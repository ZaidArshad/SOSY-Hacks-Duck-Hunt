package ca.sfu.duckhunt.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
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
import ca.sfu.duckhunt.model.NearbyBodyReceiver
import ca.sfu.duckhunt.model.Route
import ca.sfu.duckhunt.model.WaterBodyAdapter
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult


import com.google.android.gms.location.LocationServices



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userPosition: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        /*val listView = findViewById<ListView>(R.id.list)
        val waterList = ArrayList<WaterBody>()
        waterList.add(WaterBody(true, "Bear Creek", 520, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Hunt Brook", 857, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Enver Creek", 900, LatLng(0.0,0.0)))
        waterList.add(WaterBody(true, "Surrey Lake", 1000, LatLng(0.0,0.0)))
        val waterBodyAdapter = WaterBodyAdapter(this, R.layout.adapter_place, waterList, activity = MapsActivity())
        listView.adapter = waterBodyAdapter*/
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

        val button = findViewById<Button>(R.id.button)
        val listView = findViewById<ListView>(R.id.list)
        val list = NearbyBodyReceiver.getBodies(this)
        val waterBodyAdapter = WaterBodyAdapter(this, R.layout.adapter_place, list, activity = MapsActivity())
        listView.adapter = waterBodyAdapter

        button.setOnClickListener {
            waterBodyAdapter.notifyDataSetChanged()
        }

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
        var permission = true
        while (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (permission) {
                permission = false
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    10
                )
            }
        }
    }
}