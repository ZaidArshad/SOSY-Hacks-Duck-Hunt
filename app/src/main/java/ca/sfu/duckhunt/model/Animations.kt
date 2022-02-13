package ca.sfu.duckhunt.model

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import ca.sfu.duckhunt.R

/**
 * This class handles the animations of the Map Screen.
 */
class Animations {

    companion object {
        fun fadeOut(v: View, context: Context) {
            v.animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            v.animate()
            v.visibility = View.INVISIBLE
        }

        fun fadeIn(v: View, context: Context) {
            v.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            v.animate()
            v.visibility = View.VISIBLE
        }
    }
}