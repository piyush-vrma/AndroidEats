package com.example.androideats.database.fav_rest_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBAsyncTask(
    val context: Context,
    private val restEntity: RestEntity,
    private val mode: Int
) : AsyncTask<Void, Void, Boolean>() {

    /*
      Mode 1-> Check DB if the restaurant is favourite or not
      Mode 2-> Save the restaurant into DB as Favourite
      Mode 3-> Remove the favourite restaurant
     */
    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {
            1 -> {
                val restaurant: RestEntity? = db.restDao().getRestaurantById(restEntity.rest_id.toString())
                db.close()
                return restaurant != null
            }
            2 -> {
                // save
                db.restDao().insertRest(restEntity)
                db.close()
                return true
            }
            3 -> {
                // Remove
                db.restDao().deleteRest(restEntity)
                db.close()
                return true
            }
        }
        return false
    }
}