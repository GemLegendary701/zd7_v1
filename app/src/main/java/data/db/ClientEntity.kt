package com.example.zd7_v1.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val discountRate: Int = 0,
    val registrationDate: Long = System.currentTimeMillis(),
    val notes: String? = null
)