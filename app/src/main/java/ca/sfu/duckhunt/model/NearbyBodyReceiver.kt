package ca.sfu.duckhunt.model

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class NearbyBodyReceiver {

    companion object {
        private fun processResponse(response: String, bodiesList: ArrayList<WaterBody>): ArrayList<WaterBody> {
            val bodies = JSONObject(response).getJSONArray("results")

            // Go through bodies
            for (i in 0 until bodies.length()) {
                val body = bodies.getJSONObject(i)
                val types = body.getJSONArray("types")

                // Go through types
                for (k in 0 until types.length()) {
                    if (types.get(k) == "natural_feature") {
                        val name = body.get("name").toString()
                        val distance = 0
                        val position = LatLng(
                            body.getJSONObject("geometry").getJSONObject("location")
                                .get("lat")
                                .toString().toDouble(),
                            body.getJSONObject("geometry").getJSONObject("location")
                                .get("lng")
                                .toString().toDouble())
                        bodiesList.add(WaterBody(false, name, distance, position))
                    }
                }
            }
            return bodiesList
        }

        fun getBodies(context: Context): ArrayList<WaterBody> {
            val queue = Volley.newRequestQueue(context)
            val typesOfBodies: Array<String> = arrayOf("creek", "pond", "bay", "canal", "wetland", "river", "lake", "ocean")
            var bodiesList = ArrayList<WaterBody>()
            for (typeOfBody in typesOfBodies) {
                val url =
                    "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$typeOfBody&key=AIzaSyALu3YZDlIvwdYwkEiVsYVu5vqK9cRonxA"
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        bodiesList = processResponse(response.toString(), bodiesList)
                    },
                    { Log.d("request", "error") })
                queue.add(stringRequest)
            }
            return bodiesList
        }


    }
}