package com.jawadhameed.groupie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jawadhameed.groupie.utils.FirebaseUtil


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_splash)



        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (FirebaseAuth.getInstance().currentUser == null || FirebaseAuth.getInstance().currentUser!!.uid.isEmpty()) {
                startActivity(Intent(this@SplashActivity, PhoneActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }, 2000)

    }
}