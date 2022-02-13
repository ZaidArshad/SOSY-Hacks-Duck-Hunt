package ca.sfu.duckhunt.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import ca.sfu.duckhunt.R
import ca.sfu.duckhunt.view.MapsActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds


class WaterBodyAdapter(context: Context, resource: Int, objects: ArrayList<WaterBody>, activity: MapsActivity, map: GoogleMap):
    ArrayAdapter<WaterBody>(context, resource, objects) {
    private val mContext = context
    private val mResource = resource
    private val quackSound : MediaPlayer = MediaPlayer.create(getContext(), R.raw.duck_sound)
    private val mActivity = activity
    private val mMap = map

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val name = getItem(position)?.getName()
        val distance = getItem(position)?.getDistance()
        val pos = getItem(position)!!.getPosition()

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(mResource, parent, false)
        val nameView = view.findViewById<TextView>(R.id.place_name)
        val distanceView = view.findViewById<TextView>(R.id.place_distance)
        val duckButton = view.findViewById<ImageView>(R.id.duckButton)

        nameView.text = name

        if (distance!! >= 1000) distanceView.text = (distance!!/1000).toString() + "km"
        else distanceView.text = distance.toString() + "m"


        if (getItem(position)?.hasDuck() == false) {
            duckButton.setImageResource(R.drawable.duck_pic_black)
        }
        else {
            duckButton.setImageResource(R.drawable.duck_pic)
        }

        duckButton.setOnClickListener {
            mMap.clear()
            if (getItem(position)?.hasDuck() == false) {
                getItem(position)?.setHasDuck(true)
                duckButton.setImageResource(R.drawable.duck_pic_black)
            }
            else {
                getItem(position)?.setHasDuck(false)
                duckButton.setImageResource(R.drawable.duck_pic)
                quackSound.start()
            }
            mActivity.updateList()
            mActivity.drawAllMarkers()
        }

        view.setOnClickListener {
            Animations.fadeOut(mActivity.listView, context)
            Animations.fadeIn(mActivity.button, context)
            mMap.clear()
            val bound = getBounds(arrayOf(pos, mActivity.userPosition))
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 100))
            mActivity.generateRouteTo(mMap, pos)
        }

        return view
    }

    private fun getBounds(points: Array<LatLng>): LatLngBounds {

        // Setting up variables
        var north = points.first().latitude
        var south = points.last().latitude
        var west = points.first().longitude
        var east = points.first().longitude

        // Comparing with the extreme points
        for (point in points) {
            if (point.latitude > north) north = point.latitude
            else if (point.latitude < south) south = point.latitude

            if (point.longitude > east) east = point.longitude
            else if (point.longitude < west) west = point.longitude
        }

        // Sets the bounds
        return LatLngBounds(LatLng(south,west), LatLng(north, east))
    }

}