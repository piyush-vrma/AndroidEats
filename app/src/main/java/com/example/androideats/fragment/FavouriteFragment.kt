package com.example.androideats.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androideats.R
import com.example.androideats.database.fav_rest_database.RestEntity
import com.example.androideats.database.fav_rest_database.RetrieveFavourites
import com.example.androideats.adapter.FavouriteRecyclerAdapter

class FavouriteFragment : Fragment() {

    lateinit var favouritesRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var noFav:RelativeLayout
    private lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbRestaurantList = arrayListOf<RestEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        favouritesRecyclerView = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        noFav = view.findViewById(R.id.noFavourite)
        progressLayout.visibility = View.VISIBLE
        noFav.visibility = View.GONE
        layoutManager = LinearLayoutManager(activity as Context)
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get() as ArrayList<RestEntity>

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
            favouritesRecyclerView.adapter = recyclerAdapter
            favouritesRecyclerView.layoutManager = layoutManager
            if(dbRestaurantList.isEmpty()){
                noFav.visibility = View.VISIBLE
                recyclerAdapter.notifyDataSetChanged()            }
        }
        return view
    }
}