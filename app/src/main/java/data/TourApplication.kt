package com.example.zd7_v1

import android.app.Application
import com.example.zd7_v1.data.db.AppDatabase
import com.example.zd7_v1.data.db.DatabaseInitializer
import com.example.zd7_v1.data.repository.TourRepository

class TourApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TourRepository(database) }

    override fun onCreate() {
        super.onCreate()
        DatabaseInitializer.initialize(database)
    }
}