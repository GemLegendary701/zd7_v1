package com.example.zd7_v1.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val tourId: Long,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "NEW", // NEW, CONFIRMED, CANCELLED, COMPLETED
    val totalPrice: Double,
    val discountApplied: Double = 0.0,
    val notes: String? = null
)