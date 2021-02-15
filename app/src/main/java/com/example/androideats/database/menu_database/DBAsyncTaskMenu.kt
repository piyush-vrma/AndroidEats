package com.example.androideats.database.menu_database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.example.androideats.database.fav_rest_database.RestaurantDatabase

class DBAsyncTaskMenu(
    val context: Context,
    private val menuEntity: MenuEntity,
    private val mode: Int
) : AsyncTask<Void, Void, Boolean>() {
    /*
     Mode 1-> if present or not
     Mode 2-> Save
     Mode 3-> Remove
    */
    private val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {
            1 -> {
                val menu: MenuEntity? = db.menuDao().getMenuById(menuEntity.menu_id.toString())
                db.close()
                return menu != null
            }
            2 -> {
                // save
                db.menuDao().insertMenu(menuEntity)
                db.close()
                return true
            }
            3 -> {
                // Remove
                db.menuDao().deleteMenu(menuEntity)
                db.close()
                return true
            }

        }
        return false
    }
}