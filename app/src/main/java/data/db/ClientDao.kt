package com.example.zd7_v1.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClientById(clientId: Long)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: Long): ClientEntity?

    @Query("SELECT * FROM clients WHERE email = :email")
    suspend fun getClientByEmail(email: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients ORDER BY discountRate DESC")
    fun getClientsByDiscount(): Flow<List<ClientEntity>>

    @Query("UPDATE clients SET discountRate = :discountRate WHERE id = :clientId")
    suspend fun updateClientDiscount(clientId: Long, discountRate: Int)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientsCount(): Int

    @Query("SELECT * FROM clients ORDER BY registrationDate DESC")
    fun getClientsByRegistrationDate(): Flow<List<ClientEntity>>
}