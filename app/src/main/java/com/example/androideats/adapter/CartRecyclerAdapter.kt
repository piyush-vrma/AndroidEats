package com.example.androideats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androideats.R
import com.example.androideats.database.menu_database.MenuEntity

class CartRecyclerAdapter(
    private val listItem: ArrayList<MenuEntity>,
) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_tile, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = listItem[position]
        holder.itemName.text = item.menuName
        holder.itemPrice.text = "Rs.${item.cost}"
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemPrice: TextView = view.findViewById(R.id.itemPrice)
    }
}