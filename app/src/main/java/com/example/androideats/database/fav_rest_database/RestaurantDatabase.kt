package com.example.androideats.database.fav_rest_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androideats.database.menu_database.MenuDao
import com.example.androideats.database.menu_database.MenuEntity

@Database(entities = [RestEntity::class, MenuEntity::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract fun restDao(): RestaurantDao
    abstract fun menuDao(): MenuDao
}
