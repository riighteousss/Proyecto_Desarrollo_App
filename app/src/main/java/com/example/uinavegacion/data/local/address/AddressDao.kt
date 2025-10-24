package com.example.uinavegacion.data.local.address

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    fun getAddressesByUser(userId: Long): Flow<List<AddressEntity>>
    
    @Query("SELECT * FROM addresses WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultAddress(userId: Long): AddressEntity?
    
    @Query("SELECT * FROM addresses WHERE id = :addressId")
    suspend fun getAddressById(addressId: Long): AddressEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: AddressEntity): Long
    
    @Update
    suspend fun update(address: AddressEntity)
    
    @Delete
    suspend fun delete(address: AddressEntity)
    
    @Query("DELETE FROM addresses WHERE id = :addressId")
    suspend fun deleteById(addressId: Long)
    
    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultForUser(userId: Long)
    
    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :addressId")
    suspend fun setAsDefault(addressId: Long)
    
    @Query("SELECT COUNT(*) FROM addresses WHERE userId = :userId")
    suspend fun countByUser(userId: Long): Int
}
