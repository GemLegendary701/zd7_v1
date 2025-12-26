package com.example.zd7_v1.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TourDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTour(tour: TourEntity): Long

    @Update
    suspend fun updateTour(tour: TourEntity)

    @Delete
    suspend fun deleteTour(tour: TourEntity)

    @Query("DELETE FROM tours WHERE id = :tourId")
    suspend fun deleteTourById(tourId: Long)

    @Query("SELECT * FROM tours ORDER BY startDate ASC")
    fun getAllTours(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE id = :tourId")
    suspend fun getTourById(tourId: Long): TourEntity?

    @Query("SELECT * FROM tours WHERE countryCode = :countryCode ORDER BY startDate ASC")
    fun getToursByCountry(countryCode: String): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE isAvailable = 1 ORDER BY startDate ASC")
    fun getAvailableTours(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY startDate ASC")
    fun searchTours(query: String): Flow<List<TourEntity>>

    @Query("UPDATE tours SET isAvailable = :isAvailable WHERE id = :tourId")
    suspend fun updateTourAvailability(tourId: Long, isAvailable: Boolean)

    @Query("UPDATE tours SET currentParticipants = :participants WHERE id = :tourId")
    suspend fun updateTourParticipants(tourId: Long, participants: Int)

    @Query("SELECT COUNT(*) FROM tours WHERE countryCode = :countryCode")
    suspend fun getToursCountByCountry(countryCode: String): Int

    @Query("SELECT * FROM tours WHERE startDate >= :startDate AND endDate <= :endDate ORDER BY startDate ASC")
    fun getToursByDateRange(startDate: Long, endDate: Long): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours ORDER BY price ASC")
    fun getToursByPriceAsc(): Flow<List<TourEntity>>

    @Query("SELECT * FROM tours ORDER BY price DESC")
    fun getToursByPriceDesc(): Flow<List<TourEntity>>
}