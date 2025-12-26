package com.example.zd7_v1.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v1.R

data class OrderItem(
    val id: Int,
    val tourName: String,
    val orderDate: String,
    val price: String,
    val status: String
)

class OrderAdapter(
    private val orders: List<OrderItem>,
    private val onItemClick: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tourNameTextView: TextView = view.findViewById(R.id.orderTourNameTextView)
        val orderDateTextView: TextView = view.findViewById(R.id.orderDateTextView)
        val priceTextView: TextView = view.findViewById(R.id.orderPriceTextView)
        val statusTextView: TextView = view.findViewById(R.id.orderStatusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tourNameTextView.text = order.tourName
        holder.orderDateTextView.text = "Дата: ${order.orderDate}"
        holder.priceTextView.text = "Стоимость: ${order.price}"
        holder.statusTextView.text = order.status

        // Цвет статуса
        when (order.status.uppercase()) {
            "NEW" -> holder.statusTextView.setBackgroundResource(R.color.primary_blue)
            "CONFIRMED" -> holder.statusTextView.setBackgroundResource(R.color.primary_green)
            "CANCELLED" -> holder.statusTextView.setBackgroundResource(R.color.primary_red)
            "COMPLETED" -> holder.statusTextView.setBackgroundResource(R.color.primary_orange)
        }

        holder.itemView.setOnClickListener { onItemClick(order) }
    }

    override fun getItemCount(): Int = orders.size
}