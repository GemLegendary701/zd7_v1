package com.example.zd7_v1.ui.tourlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.zd7_v1.TourApplication

class TourListViewModel(application: TourApplication) : ViewModel() {

    // Сохраняем репозиторий из application
    private val repository = application.repository

    // Получаем все туры из репозитория как LiveData
    val allTours = repository.getAllTours().asLiveData()

    // Получаем только доступные туры
    val availableTours = repository.getAvailableTours().asLiveData()

    // Функция поиска туров
    fun searchTours(query: String) = repository.searchTours(query).asLiveData()

    // Получаем туры по стране
    fun getToursByCountry(countryCode: String) =
        repository.getToursByCountry(countryCode).asLiveData()

    // Получаем туры по цене (по возрастанию)
    fun getToursByPriceAsc() = repository.getToursByPriceAsc().asLiveData()

    // Получаем туры по цене (по убыванию)
    fun getToursByPriceDesc() = repository.getToursByPriceDesc().asLiveData()
}