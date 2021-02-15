package com.example.androideats.database.fav_rest_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class ClearFavDatabase(val context: Context) : AsyncTask<Void, Void, Boolean>()  {
    override fun doInBackground(vararg p0: Void?): Boolean {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        db.restDao().deleteRestaurants()
        db.close()
        return true
    }
}