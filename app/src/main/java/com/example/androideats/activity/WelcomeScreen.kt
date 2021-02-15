package com.example.androideats.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.example.androideats.R

class WelcomeScreen : AppCompatActivity() {

    lateinit var topAnim:Animation
    lateinit var leftAnim:Animation
    lateinit var rightAnim:Animation
    lateinit var logo:ImageView
    lateinit var android:TextView
    lateinit var eats:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome_screen)

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation)
        leftAnim = AnimationUtils.loadAnimation(this,R.anim.left_animation)
        rightAnim = AnimationUtils.loadAnimation(this,R.anim.right_animation)

        logo = findViewById(R.id.logoLogo)
        android = findViewById(R.id.android)
        eats = findViewById(R.id.eats)

        logo.animation = topAnim
        android.animation = leftAnim
        eats.animation = rightAnim

        Handler().postDelayed({
            var intent = Intent(this,LoginRegistration::class.java)
            startActivity(intent)
            finish()
        },5000)
    }
}