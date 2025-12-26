package com.example.zd7_v1.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7_v1.R
import com.squareup.picasso.Picasso

data class TourItem(
    val id: Int,
    val name: String,
    val country: String,
    val dates: String,
    val price: String,
    val isAvailable: Boolean
)

class TourAdapter(
    private val tours: List<TourItem>,
    private val onItemClick: (TourItem) -> Unit
) : RecyclerView.Adapter<TourAdapter.TourViewHolder>() {

    class TourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tourNameTextView)
        val countryTextView: TextView = view.findViewById(R.id.tourCountryTextView)
        val datesTextView: TextView = view.findViewById(R.id.tourDatesTextView)
        val priceTextView: TextView = view.findViewById(R.id.tourPriceTextView)
        val statusTextView: TextView = view.findViewById(R.id.tourStatusTextView)
        val imageView: ImageView = view.findViewById(R.id.tourImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = tours[position]

        holder.nameTextView.text = tour.name
        holder.countryTextView.text = "Страна: ${tour.country}"
        holder.datesTextView.text = tour.dates
        holder.priceTextView.text = "Цена: ${tour.price}"

        if (tour.isAvailable) {
            holder.statusTextView.text = "Доступен"
            holder.statusTextView.setBackgroundResource(R.color.primary_green)
        } else {
            holder.statusTextView.text = "Не доступен"
            holder.statusTextView.setBackgroundResource(R.color.primary_red)
        }

        // Загрузка флага страны (демо-изображение)
        val countryCode = tour.country.lowercase()
        Picasso.get()
            .load("https://flagcdn.com/w320/${countryCode}.png")
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imageView)

        holder.itemView.setOnClickListener { onItemClick(tour) }
    }

    override fun getItemCount(): Int = tours.size
}