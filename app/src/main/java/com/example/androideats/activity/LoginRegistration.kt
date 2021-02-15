package com.example.androideats.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androideats.R
import com.example.androideats.fragment.LoginFragment

class LoginRegistration : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login_registration)

        if(isLoggedIn){
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            login()
        }
    }

    private fun login() {
        val fragment = LoginFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment).commit()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            is LoginFragment -> finish()
            else -> super.onBackPressed()
        }
    }

}