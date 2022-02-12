package ca.sfu.duckhunt.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ca.sfu.duckhunt.R
import ca.sfu.duckhunt.view.MapsActivity
import com.google.android.libraries.places.api.model.Place

class WaterBodyAdapter(context: Context, resource: Int, objects: ArrayList<WaterBody>, activity: MapsActivity):
    ArrayAdapter<WaterBody>(context, resource, objects) {
    private val mContext = context
    private val mResource = resource
    private val mActivity = activity

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val name = getItem(position)?.getName()
        val distance = getItem(position)?.getDistance()

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(mResource, parent, false)
        val nameView = view.findViewById<TextView>(R.id.place_name)
        val distanceView = view.findViewById<TextView>(R.id.place_distance)

        nameView.text = name
        distanceView.text = distance.toString() + "m"

        return view
    }

}