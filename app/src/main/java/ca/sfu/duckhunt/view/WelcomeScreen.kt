package ca.sfu.duckhunt.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import ca.sfu.duckhunt.R

class WelcomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        supportActionBar?.hide()

        fadeIn(findViewById<TextView>(R.id.welcome_screen_title))
        fadeIn(findViewById<TextView>(R.id.welcome_screen_author))

        val intent = Intent(this, MapsActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 4000)
    }

    private fun fadeIn(view : View) {
        view.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        view.animate()
    }
}
