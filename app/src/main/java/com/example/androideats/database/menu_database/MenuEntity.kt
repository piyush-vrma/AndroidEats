package com.example.androideats.database.menu_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class MenuEntity(
    @PrimaryKey val menu_id:Int,
    @ColumnInfo(name = "menu_name") val menuName:String,
    @ColumnInfo(name = "menu_cost") val cost:String,
    @ColumnInfo(name = "rest_id") val restId:String,
)
