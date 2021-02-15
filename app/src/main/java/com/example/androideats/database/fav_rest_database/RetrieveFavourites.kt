package com.example.androideats.database.fav_rest_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<RestEntity>>() {
    override fun doInBackground(vararg p0: Void?): List<RestEntity> {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        val allRest = db.restDao().getAllRestaurants()
        db.close()
        return allRest
    }
}