package com.example.zd7_v1.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v1.R

data class ClientItem(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val discount: Int
)

class ClientAdapter(
    private val clients: List<ClientItem>,
    private val onItemClick: (ClientItem) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.clientNameTextView)
        val emailTextView: TextView = view.findViewById(R.id.clientEmailTextView)
        val phoneTextView: TextView = view.findViewById(R.id.clientPhoneTextView)
        val discountTextView: TextView = view.findViewById(R.id.clientDiscountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.nameTextView.text = client.name
        holder.emailTextView.text = "Email: ${client.email}"
        holder.phoneTextView.text = "Телефон: ${client.phone}"
        holder.discountTextView.text = "Скидка: ${client.discount}%"
        holder.itemView.setOnClickListener { onItemClick(client) }
    }

    override fun getItemCount(): Int = clients.size
}