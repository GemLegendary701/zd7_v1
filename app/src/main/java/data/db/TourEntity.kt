package com.example.zd7_v1.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tours")
data class TourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val countryCode: String,
    val name: String,
    val description: String,
    val price: Double,
    val startDate: Long, // timestamp
    val endDate: Long,   // timestamp
    val isAvailable: Boolean = true,
    val imageUrl: String? = null,
    val maxParticipants: Int? = null,
    val currentParticipants: Int = 0
)