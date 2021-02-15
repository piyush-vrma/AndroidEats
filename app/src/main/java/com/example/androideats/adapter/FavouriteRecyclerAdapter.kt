package com.example.androideats.adapter

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
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(
    private val context: Context,
    private val listItem: ArrayList<RestEntity>,
) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_recycler_tile, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = listItem[position]
        holder.restTitle.text = restaurant.restName
        holder.price.text = "Rs.${restaurant.cost}/person"
        holder.rating.text = restaurant.rating
        Picasso.get().load(restaurant.restImage).error(R.drawable.default_book_cover)
            .into(holder.image);

        holder.llContext.setOnClickListener {
            val intent = Intent(context, RestaurantMenu::class.java)
            intent.putExtra("rest_id", restaurant.rest_id.toString())
            intent.putExtra("rest_name", restaurant.restName)
            context.startActivity(intent)
        }

        val restEntity = RestEntity(
            restaurant.rest_id,
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
                    listItem.remove(restEntity)
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restTitle: TextView = view.findViewById(R.id.txtRestTitle)
        val price: TextView = view.findViewById(R.id.txtRestPrice)
        val rating: TextView = view.findViewById(R.id.txtRating)
        val favourite: ImageView = view.findViewById(R.id.txtFavourite)
        val image: ImageView = view.findViewById(R.id.imgRestLogo)
        val llContext: CardView = view.findViewById(R.id.llContent)
    }
}