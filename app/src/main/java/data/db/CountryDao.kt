package com.example.zd7_v1.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCountries(countries: List<CountryEntity>)

    @Update
    suspend fun updateCountry(country: CountryEntity)

    @Delete
    suspend fun deleteCountry(country: CountryEntity)

    @Query("DELETE FROM countries WHERE countryCode = :code")
    suspend fun deleteCountryByCode(code: String)

    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE countryCode = :code")
    suspend fun getCountryByCode(code: String): CountryEntity?

    @Query("SELECT * FROM countries WHERE region = :region ORDER BY name ASC")
    fun getCountriesByRegion(region: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCountries(query: String): Flow<List<CountryEntity>>

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountriesCount(): Int

    @Query("SELECT * FROM countries WHERE countryCode IN (:codes) ORDER BY name ASC")
    fun getCountriesByCodes(codes: List<String>): Flow<List<CountryEntity>>
}