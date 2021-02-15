package com.example.androideats.adapter

import com.example.androideats.database.menu_database.DBAsyncTaskMenu
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androideats.R
import com.example.androideats.activity.Cart
import com.example.androideats.database.menu_database.GetCartItemCount
import com.example.androideats.database.menu_database.MenuEntity
import com.example.androideats.model.Menu


class MenuRecyclerAdapter(
    private val context: Context,
    private val listItem: ArrayList<Menu>,
    private val proceed: Button,
    private val title:String?
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    var totalCost = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_menu_tile, parent, false)
        return MenuViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = listItem[position]
        holder.menuName.text = menu.menuName
        holder.cost.text = "Rs.${menu.cost}"

        val menuEntity = MenuEntity(
            menu.menuId.toInt(),
            menu.menuName,
            menu.cost,
            menu.restId
        )

        val checkCart = DBAsyncTaskMenu(context, menuEntity, 1).execute()
        val isPresent = checkCart.get()

        if (isPresent) {
            holder.addButton.text = "Remove"
            val added = ContextCompat.getColor(context, R.color.orange)
            holder.addButton.setBackgroundColor(added)
            getProceedButton()
        } else {
            holder.addButton.text = "Add"
            val remove = ContextCompat.getColor(context, R.color.dark_red)
            holder.addButton.setBackgroundColor(remove)
            getProceedButton()
        }

        holder.addButton.setOnClickListener {
            if (!DBAsyncTaskMenu(context, menuEntity, 1).execute().get()) {
                val async = DBAsyncTaskMenu(context, menuEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.addButton.text = "Remove"
                    val added = ContextCompat.getColor(context, R.color.orange)
                    holder.addButton.setBackgroundColor(added)
                    totalCost += menu.cost.toInt()
                    getProceedButton()
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DBAsyncTaskMenu(context, menuEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.addButton.text = "Add"
                    val remove = ContextCompat.getColor(context, R.color.dark_red)
                    holder.addButton.setBackgroundColor(remove)
                    totalCost -= menu.cost.toInt()
                    getProceedButton()
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.findViewById(R.id.dishName)
        val cost: TextView = view.findViewById(R.id.dishCost)
        val addButton: Button = view.findViewById(R.id.addButton)
    }

    private fun getProceedButton(){
        if (GetCartItemCount(context).execute().get() as Int > 0) {
            proceed.visibility = View.VISIBLE
            proceed.setOnClickListener {
                openCart()
            }
        } else {
            proceed.visibility = View.GONE
        }
    }

    private fun openCart(){
        val intent = Intent(context, Cart::class.java)
        intent.putExtra("rest_name", title)
        intent.putExtra("total_cost",totalCost.toString())
        context.startActivity(intent)
    }
}