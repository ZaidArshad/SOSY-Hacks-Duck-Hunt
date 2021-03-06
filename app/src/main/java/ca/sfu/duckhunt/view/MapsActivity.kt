package ca.sfu.duckhunt.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import ca.sfu.duckhunt.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ca.sfu.duckhunt.databinding.ActivityMapsBinding
import ca.sfu.duckhunt.model.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.sqrt

/**
 * This class handles the interactivity and main
 * functionalities of the Map Screen. Supports
 * getting a route from your position to a
 * desired body of water, toggling ducks, etc.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var waterBodyAdapter: WaterBodyAdapter
    private lateinit var waterBodies: ArrayList<WaterBody>
    lateinit var userPosition: LatLng
    lateinit var button: Button
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        button = findViewById(R.id.button)
        waterBodies = ArrayList()
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
        val context = this

        mMap = googleMap
        mMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 10000

        val button = findViewById<Button>(R.id.button)

        var placedDucks = false
        listView = findViewById(R.id.list)
        waterBodies = NearbyBodyReceiver.getBodies(this)
        waterBodyAdapter = WaterBodyAdapter(context, R.layout.adapter_place, waterBodies, this, mMap)
        listView.adapter = waterBodyAdapter

        button.setOnClickListener {
            Animations.fadeIn(listView,this)
            mMap.clear()
            for (water in waterBodies) drawMarker(water.hasDuck(), water.getPosition())
            waterBodyAdapter.notifyDataSetChanged()
            Animations.fadeOut(button, this)
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var oldLocation: LatLng
        userPosition = LatLng(0.0,0.0)

        val handler = Handler()

        fusedLocationClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    oldLocation = userPosition
                    handler.postDelayed(object: Runnable {
                        override fun run() {
                            waterBodyAdapter.notifyDataSetChanged()
                            waterBodies.sort()
                            updateList()
                            handler.postDelayed(this, 1000)
                        }
                    }, 0)

                    if (isGapReached(oldLocation, LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude))) {
                        userPosition = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                        for (body in waterBodies) {
                            body.setDistance(userPosition, context)
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 13F))
                    }

                    if (!placedDucks) {
                        waterBodyAdapter.notifyDataSetChanged()
                        drawAllMarkers()
                        if (waterBodies.size > 0) placedDucks = true
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    fun generateRouteTo(map : GoogleMap, destination : LatLng) {
        val currentRoute = Route(userPosition, destination, map, this)
        currentRoute.generateRoute()
    }

    fun isGapReached(locationA: LatLng, locationB: LatLng): Boolean {
        val dLat = kotlin.math.abs(locationA.latitude - locationB.latitude)
        val dLong = kotlin.math.abs(locationA.longitude - locationB.longitude)
        val radius = sqrt((dLat*dLat) + (dLong*dLong))
        return (radius >= 0.00005)
    }

    fun updateList() {
        waterBodyAdapter.notifyDataSetChanged()
    }

    fun drawAllMarkers() {
        for (water in waterBodies) drawMarker(water.hasDuck(), water.getPosition())
    }

    fun drawMarker(hasDuck: Boolean, position: LatLng) {
        val img: Int = if (hasDuck) R.drawable.duck_pic
        else R.drawable.duck_pic_black

        val duckyImg = AppCompatResources.getDrawable(
            this, img)!!.toBitmap()
        val duckyIcon = Bitmap.createScaledBitmap(duckyImg, 100, 90, false)

        val duckyMarker = MarkerOptions()
        duckyMarker.icon(BitmapDescriptorFactory.fromBitmap(duckyIcon))

        // Puts the created marker on map
        duckyMarker.position(position)
        mMap.addMarker(duckyMarker)!!
    }
}