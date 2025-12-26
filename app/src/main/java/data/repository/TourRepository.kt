package com.example.zd7_v1.data.repository

import com.example.zd7_v1.data.db.AppDatabase
import com.example.zd7_v1.data.db.ClientEntity
import com.example.zd7_v1.data.db.CountryEntity
import com.example.zd7_v1.data.db.OrderEntity
import com.example.zd7_v1.data.db.TourEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TourRepository(private val database: AppDatabase) {

    // ========== СТРАНЫ ==========
    fun getAllCountries(): Flow<List<CountryEntity>> = database.countryDao().getAllCountries()

    suspend fun getCountryByCode(code: String): CountryEntity? {
        return database.countryDao().getCountryByCode(code)
    }

    suspend fun insertCountry(country: CountryEntity) {
        database.countryDao().insertCountry(country)
    }

    suspend fun insertAllCountries(countries: List<CountryEntity>) {
        database.countryDao().insertAllCountries(countries)
    }

    suspend fun deleteCountryByCode(code: String) {
        database.countryDao().deleteCountryByCode(code)
    }

    fun searchCountries(query: String): Flow<List<CountryEntity>> {
        return database.countryDao().searchCountries(query)
    }

    fun getCountriesByRegion(region: String): Flow<List<CountryEntity>> {
        return database.countryDao().getCountriesByRegion(region)
    }

    suspend fun getCountriesCount(): Int {
        return database.countryDao().getCountriesCount()
    }

    // ========== ТУРЫ ==========
    fun getAllTours(): Flow<List<TourEntity>> = database.tourDao().getAllTours()

    fun getAvailableTours(): Flow<List<TourEntity>> = database.tourDao().getAvailableTours()

    fun getToursByCountry(countryCode: String): Flow<List<TourEntity>> {
        return database.tourDao().getToursByCountry(countryCode)
    }

    suspend fun getTourById(id: Long): TourEntity? {
        return database.tourDao().getTourById(id)
    }

    suspend fun insertTour(tour: TourEntity): Long {
        return database.tourDao().insertTour(tour)
    }

    suspend fun updateTour(tour: TourEntity) {
        database.tourDao().updateTour(tour)
    }

    suspend fun deleteTour(id: Long) {
        database.tourDao().deleteTourById(id)
    }

    fun searchTours(query: String): Flow<List<TourEntity>> {
        return database.tourDao().searchTours(query)
    }

    suspend fun updateTourAvailability(tourId: Long, isAvailable: Boolean) {
        database.tourDao().updateTourAvailability(tourId, isAvailable)
    }

    suspend fun updateTourParticipants(tourId: Long, participants: Int) {
        database.tourDao().updateTourParticipants(tourId, participants)
    }

    suspend fun getToursCountByCountry(countryCode: String): Int {
        return database.tourDao().getToursCountByCountry(countryCode)
    }

    fun getToursByPriceAsc(): Flow<List<TourEntity>> {
        return database.tourDao().getToursByPriceAsc()
    }

    fun getToursByPriceDesc(): Flow<List<TourEntity>> {
        return database.tourDao().getToursByPriceDesc()
    }

    // ========== КЛИЕНТЫ ==========
    fun getAllClients(): Flow<List<ClientEntity>> = database.clientDao().getAllClients()

    suspend fun getClientById(id: Long): ClientEntity? {
        return database.clientDao().getClientById(id)
    }

    suspend fun getClientByEmail(email: String): ClientEntity? {
        return database.clientDao().getClientByEmail(email)
    }

    suspend fun insertClient(client: ClientEntity): Long {
        return database.clientDao().insertClient(client)
    }

    suspend fun updateClient(client: ClientEntity) {
        database.clientDao().updateClient(client)
    }

    suspend fun deleteClient(id: Long) {
        database.clientDao().deleteClientById(id)
    }

    fun searchClients(query: String): Flow<List<ClientEntity>> {
        return database.clientDao().searchClients(query)
    }

    fun getClientsByDiscount(): Flow<List<ClientEntity>> {
        return database.clientDao().getClientsByDiscount()
    }

    suspend fun updateClientDiscount(clientId: Long, discountRate: Int) {
        database.clientDao().updateClientDiscount(clientId, discountRate)
    }

    suspend fun getClientsCount(): Int {
        return database.clientDao().getClientsCount()
    }

    fun getClientsByRegistrationDate(): Flow<List<ClientEntity>> {
        return database.clientDao().getClientsByRegistrationDate()
    }

    // ========== ЗАКАЗЫ ==========
    fun getOrdersByClient(clientId: Long): Flow<List<OrderEntity>> {
        return database.orderDao().getOrdersByClient(clientId)
    }

    fun getOrdersByTour(tourId: Long): Flow<List<OrderEntity>> {
        return database.orderDao().getOrdersByTour(tourId)
    }

    fun getAllOrders(): Flow<List<OrderEntity>> = database.orderDao().getAllOrders()

    suspend fun getOrderById(orderId: Long): OrderEntity? {
        return database.orderDao().getOrderById(orderId)
    }

    suspend fun insertOrder(order: OrderEntity): Long {
        return database.orderDao().insertOrder(order)
    }

    suspend fun updateOrder(order: OrderEntity) {
        database.orderDao().updateOrder(order)
    }

    suspend fun deleteOrder(orderId: Long) {
        database.orderDao().deleteOrderById(orderId)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        database.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun getOrdersCountByClient(clientId: Long): Int {
        return database.orderDao().getOrdersCountByClient(clientId)
    }

    suspend fun getTotalSpentByClient(clientId: Long): Double {
        return database.orderDao().getTotalSpentByClient(clientId) ?: 0.0
    }

    suspend fun getFavoriteCountryByClient(clientId: Long): String? {
        return database.orderDao().getFavoriteCountryByClient(clientId)
    }

    suspend fun getOrdersCountByTour(tourId: Long): Int {
        return database.orderDao().getOrdersCountByTour(tourId)
    }

    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
        return database.orderDao().getOrdersByStatus(status)
    }

    // ========== СТАТИСТИКА И РАСЧЕТЫ ==========
    data class ClientStatistics(
        val totalOrders: Int,
        val totalSpent: Double,
        val favoriteCountry: String?,
        val averageOrderValue: Double,
        val discountRate: Int
    )

    suspend fun calculateClientDiscount(clientId: Long): Int {
        val baseDiscount = getClientById(clientId)?.discountRate ?: 0
        val orderCount = getOrdersCountByClient(clientId)

        // Логика расчета скидки:
        // 1. Базовая скидка клиента
        // 2. +5% за каждые 3 заказа
        // 3. Максимальная скидка 30%
        val additionalDiscount = (orderCount / 3) * 5
        val totalDiscount = baseDiscount + additionalDiscount

        return if (totalDiscount > 30) 30 else totalDiscount
    }

    suspend fun getClientStatistics(clientId: Long): ClientStatistics? {
        val client = getClientById(clientId) ?: return null

        val totalOrders = getOrdersCountByClient(clientId)
        val totalSpent = getTotalSpentByClient(clientId)
        val favoriteCountry = getFavoriteCountryByClient(clientId)
        val discountRate = calculateClientDiscount(clientId)

        val averageOrderValue = if (totalOrders > 0) {
            totalSpent / totalOrders
        } else {
            0.0
        }

        return ClientStatistics(
            totalOrders = totalOrders,
            totalSpent = totalSpent,
            favoriteCountry = favoriteCountry,
            averageOrderValue = averageOrderValue,
            discountRate = discountRate
        )
    }

    suspend fun calculateOrderPriceWithDiscount(tourId: Long, clientId: Long): Double {
        val tour = getTourById(tourId) ?: return 0.0
        val discountRate = calculateClientDiscount(clientId)

        return tour.price * (100 - discountRate) / 100
    }

    // ========== СВЯЗАННЫЕ ДАННЫЕ ==========
    data class TourWithCountry(
        val tour: TourEntity,
        val country: CountryEntity?
    )

    suspend fun getTourWithCountry(tourId: Long): TourWithCountry? {
        val tour = getTourById(tourId) ?: return null
        val country = tour.countryCode.let { getCountryByCode(it) }

        return TourWithCountry(tour, country)
    }

    data class OrderWithDetails(
        val order: OrderEntity,
        val client: ClientEntity?,
        val tour: TourEntity?
    )

    suspend fun getOrderWithDetails(orderId: Long): OrderWithDetails? {
        val order = getOrderById(orderId) ?: return null
        val client = getClientById(order.clientId)
        val tour = getTourById(order.tourId)

        return OrderWithDetails(order, client, tour)
    }

    data class ClientWithOrders(
        val client: ClientEntity,
        val orders: List<OrderEntity>
    )

    suspend fun getClientWithOrders(clientId: Long): ClientWithOrders? {
        val client = getClientById(clientId) ?: return null
        val ordersFlow = getOrdersByClient(clientId)
        val orders = ordersFlow.firstOrNull() ?: emptyList()

        return ClientWithOrders(client, orders)
    }

    data class CountryWithTours(
        val country: CountryEntity,
        val tours: List<TourEntity>
    )

    suspend fun getCountryWithTours(countryCode: String): CountryWithTours? {
        val country = getCountryByCode(countryCode) ?: return null
        val toursFlow = getToursByCountry(countryCode)
        val tours = toursFlow.firstOrNull() ?: emptyList()

        return CountryWithTours(country, tours)
    }
}