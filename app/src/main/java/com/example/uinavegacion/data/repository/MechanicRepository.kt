package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.mechanic.MechanicDao
import com.example.uinavegacion.data.local.mechanic.MechanicEntity
import kotlinx.coroutines.flow.Flow

class MechanicRepository(private val mechanicDao: MechanicDao) {
    
    fun getAllMechanics(): Flow<List<MechanicEntity>> = 
        mechanicDao.getAllMechanics()
    
    fun getAvailableMechanics(): Flow<List<MechanicEntity>> = 
        mechanicDao.getAvailableMechanics()
    
    suspend fun getMechanicById(id: String): MechanicEntity? = 
        mechanicDao.getMechanicById(id)
    
    fun getMechanicsBySpecialty(specialty: String): Flow<List<MechanicEntity>> = 
        mechanicDao.getMechanicsBySpecialty(specialty)
    
    suspend fun insertMechanic(mechanic: MechanicEntity) = 
        mechanicDao.insert(mechanic)
    
    suspend fun insertAllMechanics(mechanics: List<MechanicEntity>) = 
        mechanicDao.insertAll(mechanics)
    
    suspend fun updateMechanic(mechanic: MechanicEntity) = 
        mechanicDao.update(mechanic)
    
    suspend fun deleteMechanic(mechanic: MechanicEntity) = 
        mechanicDao.delete(mechanic)
    
    suspend fun deleteAllMechanics() = 
        mechanicDao.deleteAll()
    
    suspend fun getMechanicCount(): Int = 
        mechanicDao.count()
}
