package ca.sfu.duckhunt.model

import android.content.Context
import android.graphics.Color
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject

/**
 * This class is responsible for generating the route
 * from the origin point to the destination point on the user's map.
 */
class Route (
    origin: LatLng, destination: LatLng,
    private var map: GoogleMap, private var context : Context) {
    private val apiKey : String = "AIzaSyALu3YZDlIvwdYwkEiVsYVu5vqK9cRonxA"
    private val routeString = ("https://maps.googleapis.com/maps/api/directions/json?origin=" +
            origin.latitude + "," + origin.longitude + "&destination=" +
            destination.latitude + "," + destination.longitude +
            "&key=" + apiKey)

    private var timeAway = ""

    fun generateRoute() {
        val path : MutableList<List<LatLng>> = ArrayList()
        val urlDirections = (routeString)
        val directionsRequest = object : StringRequest(Method.GET, urlDirections, Response.Listener {
                response ->
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val legsObj = legs.getJSONObject(0)
            val duration = legsObj.getJSONObject("duration")
            timeAway = duration.getString("text")

            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                map.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }

        }, Response.ErrorListener {
        }){}
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(directionsRequest)
    }
}