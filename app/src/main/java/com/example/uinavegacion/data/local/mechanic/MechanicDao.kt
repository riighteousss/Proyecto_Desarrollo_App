package com.example.uinavegacion.data.local.mechanic

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MechanicDao {
    @Query("SELECT * FROM mechanics")
    fun getAllMechanics(): Flow<List<MechanicEntity>>
    
    @Query("SELECT * FROM mechanics WHERE isAvailable = 1")
    fun getAvailableMechanics(): Flow<List<MechanicEntity>>
    
    @Query("SELECT * FROM mechanics WHERE id = :id")
    suspend fun getMechanicById(id: String): MechanicEntity?
    
    @Query("SELECT * FROM mechanics WHERE specialty LIKE '%' || :specialty || '%'")
    fun getMechanicsBySpecialty(specialty: String): Flow<List<MechanicEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mechanic: MechanicEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mechanics: List<MechanicEntity>)
    
    @Update
    suspend fun update(mechanic: MechanicEntity)
    
    @Delete
    suspend fun delete(mechanic: MechanicEntity)
    
    @Query("DELETE FROM mechanics")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM mechanics")
    suspend fun count(): Int
}

