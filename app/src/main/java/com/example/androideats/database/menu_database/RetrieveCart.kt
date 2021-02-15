package com.example.androideats.database.menu_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.example.androideats.database.fav_rest_database.RestaurantDatabase

class RetrieveCart(val context: Context) : AsyncTask<Void, Void, List<MenuEntity>>()  {
    override fun doInBackground(vararg p0: Void?): List<MenuEntity> {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        val allMenu = db.menuDao().getAllMenu()
        db.close()
        return allMenu
    }
}