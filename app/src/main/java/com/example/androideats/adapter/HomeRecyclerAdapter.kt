package com.example.androideats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.androideats.R
import com.example.androideats.activity.RestaurantMenu
import com.example.androideats.database.fav_rest_database.DBAsyncTask
import com.example.androideats.database.fav_rest_database.RestEntity
import com.example.androideats.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(
    private val context: Context,
    private val listItem: ArrayList<Restaurant>
) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_recycler_tile, parent, false)
        return HomeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = listItem[position]
        holder.restTitle.text = restaurant.restName
        holder.price.text = "Rs.${restaurant.cost}/person"
        holder.rating.text = restaurant.rating
        Picasso.get().load(restaurant.restImage).error(R.drawable.default_book_cover)
            .into(holder.image);

        val restEntity = RestEntity(
            restaurant.restId.toInt(),
            restaurant.restName,
            restaurant.rating,
            restaurant.cost,
            restaurant.restImage
        )

        val checkFav = DBAsyncTask(context, restEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.favourite.setImageResource(R.drawable.ic_favourite_fill)
        } else {
            holder.favourite.setImageResource(R.drawable.ic_favourite)
        }

        holder.favourite.setOnClickListener {
            if (!DBAsyncTask(context, restEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favourite.setImageResource(R.drawable.ic_favourite_fill)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DBAsyncTask(context, restEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.favourite.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.llContext.setOnClickListener {
            val intent = Intent(context, RestaurantMenu::class.java)
            intent.putExtra("rest_id", restaurant.restId)
            intent.putExtra("rest_name", restaurant.restName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restTitle: TextView = view.findViewById(R.id.txtRestTitle)
        val price: TextView = view.findViewById(R.id.txtRestPrice)
        val rating: TextView = view.findViewById(R.id.txtRating)
        val favourite: ImageView = view.findViewById(R.id.txtFavourite)
        val image: ImageView = view.findViewById(R.id.imgRestLogo)
        val llContext: CardView = view.findViewById(R.id.llContent)
    }
}