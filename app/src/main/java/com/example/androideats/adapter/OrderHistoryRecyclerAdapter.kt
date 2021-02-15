package com.example.androideats.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androideats.R
import com.example.androideats.model.Orders
import org.w3c.dom.Text
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class OrderHistoryRecyclerAdapter(
    private val context: Context,
    private val listItem: ArrayList<Orders>
) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.order_history_tile, parent, false)
        return OrderHistoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = listItem[position]
        holder.restName.text = order.restName

        val f: DateFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss")
        val d: Date = f.parse(order.dateTime)
        val date: DateFormat = SimpleDateFormat("dd/MM/yy")
        val time: DateFormat = SimpleDateFormat("hh:mm a")


        holder.time.text = "Order Placed on " + date.format(d).toString() + " at " + time.format(d).toString()
        holder.totalCost.text = "Rs."+order.totalCost

        val layoutManager = LinearLayoutManager(context)
        val recyclerAdapter = CartRecyclerAdapter(order.foodItems)
        holder.foodRecycler.layoutManager = layoutManager
        holder.foodRecycler.adapter = recyclerAdapter
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restName: TextView = view.findViewById(R.id.restName)
        val foodRecycler: RecyclerView = view.findViewById(R.id.foodRecycler)
        val totalCost: TextView = view.findViewById(R.id.cost)
        val time: TextView = view.findViewById(R.id.time)
    }
}