package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.address.AddressDao
import com.example.uinavegacion.data.local.address.AddressEntity
import kotlinx.coroutines.flow.Flow

class AddressRepository(private val addressDao: AddressDao) {
    
    fun getAddressesByUser(userId: Long): Flow<List<AddressEntity>> = 
        addressDao.getAddressesByUser(userId)
    
    suspend fun getDefaultAddress(userId: Long): AddressEntity? = 
        addressDao.getDefaultAddress(userId)
    
    suspend fun getAddressById(addressId: Long): AddressEntity? = 
        addressDao.getAddressById(addressId)
    
    suspend fun insertAddress(address: AddressEntity): Long = 
        addressDao.insert(address)
    
    suspend fun updateAddress(address: AddressEntity) = 
        addressDao.update(address)
    
    suspend fun deleteAddress(addressId: Long) = 
        addressDao.deleteById(addressId)
    
    suspend fun setAsDefaultAddress(userId: Long, addressId: Long) {
        addressDao.clearDefaultForUser(userId)
        addressDao.setAsDefault(addressId)
    }
    
    suspend fun getAddressCount(userId: Long): Int = 
        addressDao.countByUser(userId)
}
