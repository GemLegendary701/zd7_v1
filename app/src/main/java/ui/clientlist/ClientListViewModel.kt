package com.example.zd7_v1.ui.clientlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.zd7_v1.TourApplication

class ClientListViewModel(application: TourApplication) : ViewModel() {

    // Сохраняем репозиторий из application
    private val repository = application.repository

    // Получаем всех клиентов
    val allClients = repository.getAllClients().asLiveData()

    // Получаем клиентов отсортированных по скидке
    val clientsByDiscount = repository.getClientsByDiscount().asLiveData()

    // Получаем клиентов по дате регистрации
    val clientsByRegistrationDate = repository.getClientsByRegistrationDate().asLiveData()

    // Функция поиска клиентов
    fun searchClients(query: String) = repository.searchClients(query).asLiveData()
}