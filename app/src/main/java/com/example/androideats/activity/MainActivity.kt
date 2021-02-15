package com.example.androideats.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.androideats.R
import com.example.androideats.database.fav_rest_database.ClearFavDatabase
import com.example.androideats.fragment.*
import com.google.android.material.navigation.NavigationView
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var searchView: SearchView
    lateinit var sharedPreferences: SharedPreferences

    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        searchView = findViewById(R.id.searchView)
        getHeaderValues()

        setUpToolbar()

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.Home -> {
                    openHome()
                }
                R.id.MyProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                    searchView.visibility = View.GONE
                }
                R.id.favRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouriteFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                    searchView.visibility = View.GONE
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                    searchView.visibility = View.GONE
                }

                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FaqsFragment())
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                    searchView.visibility = View.GONE
                }
                R.id.logOut -> {
                    drawerLayout.closeDrawers()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("YES") { text, listener ->
                        // Do Nothing
                        ClearFavDatabase(this).execute().get()
                        sharedPreferences?.edit()?.clear()?.apply()
                        val intent = Intent(this,LoginRegistration::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("NO") { text, listener ->
                        // Do Nothing
                        openHome()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getHeaderValues() {
        val headerView = navigationView.getHeaderView(0)
        val userName: TextView = headerView.findViewById(R.id.txtUserName) as TextView
        val userMBNO = headerView.findViewById(R.id.userMobileNumber) as TextView
        userName.text = sharedPreferences.getString("name", "UserName")?.toUpperCase(Locale.ROOT)
        userMBNO.text = "+91-" + sharedPreferences.getString("mobile_number", "9999999999")
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment).commit()
        supportActionBar?.title = "All Restaurants"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.Home)
        searchView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}