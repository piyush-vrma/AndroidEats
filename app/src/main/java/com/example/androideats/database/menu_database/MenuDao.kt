package com.example.androideats.database.menu_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {
    @Insert
    fun insertMenu(menuEntity: MenuEntity)

    @Delete
    fun deleteMenu(menuEntity: MenuEntity)

    @Query("SELECT * FROM menu")
    fun getAllMenu():List<MenuEntity>

    @Query("SELECT * FROM menu WHERE menu_id = :menuId")
    fun getMenuById(menuId:String) : MenuEntity

    @Query("DELETE FROM menu")
    fun deleteOrders()

    @Query("SELECT COUNT(menu_name) FROM menu")
    fun getCount():Int
}