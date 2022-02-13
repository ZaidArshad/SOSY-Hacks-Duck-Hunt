package ca.sfu.duckhunt.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.app.ActivityCompat
import ca.sfu.duckhunt.R

/**
 * This class handles the welcome screen.
 */
class WelcomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        supportActionBar?.hide()

        fadeIn(findViewById<TextView>(R.id.welcome_screen_title))
        fadeIn(findViewById<TextView>(R.id.welcome_screen_author))

        val intent = Intent(this, MapsActivity::class.java)
        locationPermissionCheck()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 4000)
    }

    private fun fadeIn(view : View) {
        view.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        view.animate()
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
