package com.example.androideats.database.menu_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.example.androideats.database.fav_rest_database.RestaurantDatabase

class GetCartItemCount(val context: Context) : AsyncTask<Void, Void, Int>()  {
    override fun doInBackground(vararg p0: Void?): Int{
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        val count = db.menuDao().getCount()
        db.close()
        return count
    }
}