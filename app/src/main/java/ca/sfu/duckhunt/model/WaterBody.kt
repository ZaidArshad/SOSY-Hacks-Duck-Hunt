package ca.sfu.duckhunt.model

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class WaterBody(hasDuck: Boolean, name: String, distance: Int, position: LatLng) {
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

    fun setDistance(userPos : LatLng) {
        val routeString = ("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                userPos.latitude + "," + userPos.longitude + "&destination=" +
                mPosition.latitude + "," + mPosition.longitude +
                "&key=" + "AIzaSyALu3YZDlIvwdYwkEiVsYVu5vqK9cRonxA")

        val urlDirections = (routeString)
        var value = 0

        object : StringRequest(Method.GET, urlDirections, Response.Listener {
                response ->
            val jsonResponse = JSONObject(response)
            val routes = jsonResponse.getJSONArray("routes")
            val obj = routes.getJSONObject(0)
            val legs = obj.getJSONArray("legs")
            val legsObj = legs.getJSONObject(0)
            val distance = legsObj.getJSONObject("distance")
            value = distance.getString("value").toInt()
        }, Response.ErrorListener {
        }){}

        mDistance = value
    }
}