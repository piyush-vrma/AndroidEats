package com.example.androideats.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.adapter.CartRecyclerAdapter
import com.example.androideats.database.menu_database.ClearDataBase
import com.example.androideats.database.menu_database.MenuEntity
import com.example.androideats.database.menu_database.RetrieveCart
import com.example.androideats.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class Cart : AppCompatActivity() {

    lateinit var cartRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var placeOrder: Button
    lateinit var restaurantName: TextView
    lateinit var itemList: ArrayList<MenuEntity>
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    private var restName: String? = ""
    private var totalCost: String? = ""
    lateinit var userId: String
    lateinit var restId: String
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initiateValues()
        setUpToolbar()
        attachAdapter()
        placeOrder.setOnClickListener {
            placingOrder()
        }
    }

    private fun getFoodJsonArray(): JSONArray {
        val food = JSONArray()
        for (item in itemList) {
            val foodObject = JSONObject()
            foodObject.put("food_item_id", item.menu_id)
            food.put(foodObject)
        }
        return food
    }

    private fun placingOrder() {
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", restId)
        jsonParams.put("total_cost", totalCost)
        jsonParams.put("food", getFoodJsonArray())

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            placeOrder.visibility = View.GONE
                            val clearDataBase = ClearDataBase(this).execute().get()
                            if (clearDataBase) {
                                val intent = Intent(this, OrderPlaced::class.java)
                                startActivity(intent)
                                createNotification()
                                finishAffinity()
                            } else {
                                Toast.makeText(this, "Some Error occurred!", Toast.LENGTH_LONG).show()
                            }

                        } else {
                            val errorMessage = data.getString("errorMessage")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                            finish()
                        }

                    } catch (e: Exception) {
                        progressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Some Error $e occurred!", Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {
                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Volley Error Occurred", Toast.LENGTH_LONG).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }
            queue.add(jsonRequest)

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
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun attachAdapter() {
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = CartRecyclerAdapter( itemList)
        cartRecyclerView.layoutManager = layoutManager
        cartRecyclerView.adapter = recyclerAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun initiateValues() {
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        toolbar = findViewById(R.id.toolbar)
        placeOrder = findViewById(R.id.btnPlaceOrder)
        restaurantName = findViewById(R.id.restName)
        cartRecyclerView = findViewById(R.id.cartRecycler)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        if (intent != null) {
            restName = intent.getStringExtra("rest_name")
            restaurantName.text = restName
            totalCost = intent.getStringExtra("total_cost")
            placeOrder.text = "Place Order (Total: Rs.$totalCost)"
        } else {
            Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
        }

        itemList = RetrieveCart(this).execute().get() as ArrayList<MenuEntity>

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        userId = sharedPreferences.getString("user_id", "100").toString()
        restId = itemList[0].restId
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun createNotification() {
        val notificationId = 1;
        val channelId = "personal_notification"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.setContentTitle("Order Placed")
        notificationBuilder.setContentText("Your order has been successfully placed!")
        notificationBuilder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Your order has been successfully placed! \nPaid Rs.$totalCost to ${restName}.")
        )

        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Order Placed"
            val description = "Your order has been successfully placed!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, name, importance)
            notificationChannel.description = description

            val notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}