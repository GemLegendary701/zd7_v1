package com.example.zd7_v1.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zd7_v1.TourApplication
import com.example.zd7_v1.ui.clientlist.ClientListViewModel
import com.example.zd7_v1.ui.tourlist.TourListViewModel

class TourViewModelFactory(private val application: TourApplication) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TourListViewModel::class.java) -> {
                TourListViewModel(application) as T
            }
            modelClass.isAssignableFrom(ClientListViewModel::class.java) -> {
                ClientListViewModel(application) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}