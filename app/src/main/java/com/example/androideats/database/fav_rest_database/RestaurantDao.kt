package com.example.androideats.database.fav_rest_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRest(restEntity: RestEntity)

    @Delete
    fun deleteRest(restEntity: RestEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants():List<RestEntity>

    @Query("DELETE FROM restaurants")
    fun deleteRestaurants()

    @Query("SELECT * FROM restaurants WHERE rest_id = :restId")
    fun getRestaurantById(restId:String) : RestEntity

}