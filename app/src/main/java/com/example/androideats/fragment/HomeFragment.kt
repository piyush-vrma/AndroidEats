package com.example.androideats.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.adapter.HomeRecyclerAdapter
import com.example.androideats.model.Restaurant
import com.example.androideats.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var homeRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    var restaurantDisplayList = arrayListOf<Restaurant>()
    var restaurantList = arrayListOf<Restaurant>()

    private val ratingComparator = Comparator<Restaurant>{ rest1, rest2 ->
        if(rest1.rating.compareTo(rest2.rating,true) == 0){
            rest1.restName.compareTo(rest2.restName,true)
        }else{
            rest1.rating.compareTo(rest2.rating,true)
        }
    }

    private val costComparator = Comparator<Restaurant>{ rest1, rest2 ->
        if(rest1.cost.compareTo(rest2.cost,true) == 0){
            rest1.rating.compareTo(rest2.rating,true)
        }else{
            rest1.cost.compareTo(rest2.cost,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        homeRecyclerView = view.findViewById(R.id.HomeRecycler)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        getSearchView()

        return view
    }

    private fun fetchData() {

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val restArray = data.getJSONArray("data")
                            for (i in 0 until restArray.length()) {
                                val restJsonObject = restArray.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restJsonObject.getString("id"),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("rating"),
                                    restJsonObject.getString("cost_for_one"),
                                    restJsonObject.getString("image_url"),
                                )
                                restaurantDisplayList.add(restaurantObject)
                            }

                            restaurantList.addAll(restaurantDisplayList)
                            layoutManager = LinearLayoutManager(activity)
                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, restaurantDisplayList)
                            homeRecyclerView.layoutManager = layoutManager
                            homeRecyclerView.adapter = recyclerAdapter

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
            if (restaurantDisplayList.isEmpty())
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

    private fun getSearchView() {
        val searchView = activity?.findViewById<SearchView>(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                if (nextText!!.isNotBlank()) {
                    restaurantDisplayList.clear()
                    val search = nextText.toLowerCase()
                    restaurantList.forEach {
                        if (it.restName.toLowerCase().contains(search)) {
                            restaurantDisplayList.add(it)
                        }
                    }
                    if (restaurantDisplayList.isEmpty()) {
                        Toast.makeText(context, "Restaurant Not Found!!", Toast.LENGTH_SHORT).show()
                    }
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    restaurantDisplayList.clear()
                    restaurantDisplayList.addAll(restaurantList)
                    recyclerAdapter.notifyDataSetChanged()
                }
                return true
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rating -> {
                Collections.sort(restaurantDisplayList,ratingComparator)
                restaurantDisplayList.reverse()
            }
            R.id.lowToHigh -> {
                Collections.sort(restaurantDisplayList,costComparator)
            }
            R.id.highToLow -> {
                Collections.sort(restaurantDisplayList,costComparator)
                restaurantDisplayList.reverse()
            }
        }

        // to tell the adapter class to change the order
        // of the list displayed we are writing the below code
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}