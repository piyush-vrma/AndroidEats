package com.example.androideats.database.menu_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.example.androideats.database.fav_rest_database.RestaurantDatabase

class ClearDataBase(val context: Context) : AsyncTask<Void, Void, Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        db.menuDao().deleteOrders()
        db.close()
        return true
    }
}