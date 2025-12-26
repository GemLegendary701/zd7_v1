package com.example.zd7_v1.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Long)

    @Query("SELECT * FROM orders WHERE clientId = :clientId ORDER BY orderDate DESC")
    fun getOrdersByClient(clientId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE tourId = :tourId ORDER BY orderDate DESC")
    fun getOrdersByTour(tourId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): OrderEntity?

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("SELECT COUNT(*) FROM orders WHERE clientId = :clientId")
    suspend fun getOrdersCountByClient(clientId: Long): Int

    @Query("SELECT SUM(totalPrice) FROM orders WHERE clientId = :clientId")
    suspend fun getTotalSpentByClient(clientId: Long): Double?

    @Query("SELECT COUNT(*) FROM orders WHERE tourId = :tourId")
    suspend fun getOrdersCountByTour(tourId: Long): Int

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY orderDate DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("""
        SELECT t.countryCode 
        FROM orders o 
        JOIN tours t ON o.tourId = t.id 
        WHERE o.clientId = :clientId 
        GROUP BY t.countryCode 
        ORDER BY COUNT(*) DESC 
        LIMIT 1
    """)
    suspend fun getFavoriteCountryByClient(clientId: Long): String?

    @Query("SELECT COUNT(*) FROM orders WHERE clientId = :clientId AND status = :status")
    suspend fun getOrdersCountByClientAndStatus(clientId: Long, status: String): Int
}