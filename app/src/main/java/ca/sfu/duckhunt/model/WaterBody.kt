package ca.sfu.duckhunt.model

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.Serializable

class WaterBody(hasDuck: Boolean, name: String, distance: Int, position: LatLng) : Comparable<WaterBody> {
    private var mHasDuck = hasDuck
    private val mName = name
    private var mDistance = distance
    private val mPosition = position

    fun hasDuck(): Boolean { return mHasDuck }
    fun setHasDuck(bool : Boolean) {
        mHasDuck = bool
    }
    fun getName(): String { return mName }
    fun getDistance(): Int { return mDistance }
    fun getPosition(): LatLng { return mPosition }

    fun setDistance(userPos : LatLng, context: Context) {
        val routeString = ("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                userPos.latitude + "," + userPos.longitude + "&destination=" +
                mPosition.latitude + "," + mPosition.longitude +
                "&key=" + "AIzaSyALu3YZDlIvwdYwkEiVsYVu5vqK9cRonxA")

        val urlDirections = (routeString)
        var value: Int

        val stringRequest = StringRequest(
            Request.Method.GET, urlDirections,
            { response ->
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")
                val obj = routes.getJSONObject(0)
                val legs = obj.getJSONArray("legs")
                val legsObj = legs.getJSONObject(0)
                val distance = legsObj.getJSONObject("distance")
                value = distance.getInt("value")
                mDistance = value
            },
            { Log.d("request", "error") })

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    override fun compareTo(other: WaterBody): Int {
        if (this.mDistance > other.mDistance) {
            return 1
        }
        if (this.mDistance < other.mDistance ) {
            return -1
        }
        return 0
    }
}