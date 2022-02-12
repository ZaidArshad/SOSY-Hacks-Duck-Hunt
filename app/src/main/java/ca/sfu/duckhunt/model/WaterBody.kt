package ca.sfu.duckhunt.model

import com.google.android.gms.maps.model.LatLng

class WaterBody(hasDuck: Boolean, name: String, distance: Int, position: LatLng) {
    private val mHasDuck = hasDuck
    private val mName = name
    private val mDistance = distance
    private val mPosition = position

    fun getHasDuck(): Boolean { return mHasDuck }
    fun getName(): String { return mName }
    fun getDistance(): Int { return mDistance }
    fun getPosition(): LatLng { return mPosition }
}