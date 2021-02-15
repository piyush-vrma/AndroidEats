package com.example.androideats.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.adapter.MenuRecyclerAdapter
import com.example.androideats.database.menu_database.ClearDataBase
import com.example.androideats.database.menu_database.GetCartItemCount
import com.example.androideats.model.Menu
import com.example.androideats.util.ConnectionManager
import org.json.JSONException

class RestaurantMenu : AppCompatActivity() {

    lateinit var menuRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var proceed: Button
    private lateinit var recyclerAdapter: MenuRecyclerAdapter
    val menuList = arrayListOf<Menu>()
    private var restId: String? = "100"
    private var title: String? = "Menu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        toolbar = findViewById(R.id.toolbar)
        menuRecyclerView = findViewById(R.id.menuRecycler)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        proceed = findViewById(R.id.btnProceedToCart)
        proceed.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        if (intent != null) {
            restId = intent.getStringExtra("rest_id")
            title = intent.getStringExtra("rest_name")
        } else {
            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
        }

        if (restId == "100") {
            finish()
            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
        }

        setUpToolbar()
    }

    private fun fetchData() {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restId}>"

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val menuArray = data.getJSONArray("data")
                            for (i in 0 until menuArray.length()) {
                                print("i am in menuarray")
                                val menuJsonObject = menuArray.getJSONObject(i)
                                val menuObject = Menu(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id"),
                                )
                                menuList.add(menuObject)
                            }

                            layoutManager = LinearLayoutManager(this)
                            recyclerAdapter = MenuRecyclerAdapter(this, menuList, proceed, title)
                            menuRecyclerView.layoutManager = layoutManager
                            menuRecyclerView.adapter = recyclerAdapter


                        } else {
                            val errorMessage = data.getString("errorMessage")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                errorMessage,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some ERROR Occurred $e",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Volley Error Occurred",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            if (menuList.isEmpty())
                fetchData()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
        super.onResume()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (GetCartItemCount(this).execute().get() as Int > 0) {
                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                alterDialog.setTitle("Alert!")
                alterDialog.setMessage("Going back will remove everything from cart")
                alterDialog.setPositiveButton("Okay") { text, listener ->
                    val cleared = ClearDataBase(this).execute().get()
                    if (cleared == true) {
                        finish()
                    } else {
                        Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_LONG).show()
                    }
                }
                alterDialog.setNegativeButton("No") { text, listener ->

                }
                alterDialog.show()
            } else {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (GetCartItemCount(this).execute().get() as Int > 0) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                val cleared = ClearDataBase(this).execute().get()
                if (cleared == true) {
                    super.onBackPressed()
                } else {
                    Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_LONG).show()
                }
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}