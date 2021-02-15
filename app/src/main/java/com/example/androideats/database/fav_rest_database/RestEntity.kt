package com.example.androideats.database.fav_rest_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestEntity(
    @PrimaryKey val rest_id:Int,
    @ColumnInfo(name = "rest_name") val restName:String,
    @ColumnInfo(name = "rest_rating") val rating:String,
    @ColumnInfo(name = "rest_cost") val cost:String,
    @ColumnInfo(name = "rest_image") val restImage:String
)