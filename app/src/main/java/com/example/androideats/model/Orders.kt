package com.example.androideats.model

import com.example.androideats.database.menu_database.MenuEntity

class Orders(val orderId:String,val restName:String,val totalCost:String,val dateTime:String,val foodItems:ArrayList<MenuEntity>) {
}