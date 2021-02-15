package com.example.androideats.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.adapter.OrderHistoryRecyclerAdapter
import com.example.androideats.database.menu_database.MenuEntity
import com.example.androideats.model.Orders
import com.example.androideats.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    lateinit var orderRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    private lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    var orderList = arrayListOf<Orders>()
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        initialize(view)


        return view
    }

    private fun initialize(view: View) {
        orderRecyclerView = view.findViewById(R.id.orderRecycler)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun fetchData() {

        val userId = sharedPreferences?.getString("user_id", "100")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val orderArray = data.getJSONArray("data")
                            for (i in 0 until orderArray.length()) {
                                val orderJsonObject = orderArray.getJSONObject(i)
                                val foodItems = ArrayList<MenuEntity>()
                                val foodJsonArray = orderJsonObject.getJSONArray("food_items")

                                for (j in 0 until foodJsonArray.length()) {
                                    val foodJsonObject = foodJsonArray.getJSONObject(j)
                                    val foodObject = MenuEntity(
                                        foodJsonObject.getString("food_item_id").toInt(),
                                        foodJsonObject.getString("name"),
                                        foodJsonObject.getString("cost"),
                                        "100"
                                    )
                                    foodItems.add(foodObject)
                                }

                                val orderObject = Orders(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("total_cost"),
                                    orderJsonObject.getString("order_placed_at"),
                                    foodItems,
                                )

                                orderList.add(orderObject)
                            }

                            layoutManager = LinearLayoutManager(activity)
                            recyclerAdapter =
                                OrderHistoryRecyclerAdapter(activity as Context, orderList)
                            orderRecyclerView.layoutManager = layoutManager
                            orderRecyclerView.adapter = recyclerAdapter

                        } else {
                            val errorMessage = data.getString("errorMessage")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }

                    } catch (e: JSONException) {
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Some ERROR Occurred $e",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            if (orderList.isEmpty())
                fetchData()
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        super.onResume()
    }

}