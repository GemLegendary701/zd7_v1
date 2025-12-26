package com.example.zd7_v1.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    val countryCode: String,
    val name: String,
    val flagUrl: String,
    val capital: String? = null,
    val population: Long? = null,
    val region: String? = null,
    val subregion: String? = null
)