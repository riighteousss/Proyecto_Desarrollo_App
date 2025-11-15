package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.address.AddressEntity
import com.example.uinavegacion.data.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val addressRepository: AddressRepository
) : ViewModel() {
    
    private val _addresses = MutableStateFlow<List<AddressEntity>>(emptyList())
    val addresses: StateFlow<List<AddressEntity>> = _addresses.asStateFlow()
    
    private val _defaultAddress = MutableStateFlow<AddressEntity?>(null)
    val defaultAddress: StateFlow<AddressEntity?> = _defaultAddress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadAddresses(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addressRepository.getAddressesByUser(userId).collect { addressList ->
                    _addresses.value = addressList
                    _defaultAddress.value = addressList.firstOrNull { it.isDefault }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar direcciones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            try {
                addressRepository.insertAddress(address)
                loadAddresses(address.userId)
            } catch (e: Exception) {
                _error.value = "Error al agregar dirección: ${e.message}"
            }
        }
    }
    
    fun updateAddress(address: AddressEntity) {
        viewModelScope.launch {
            try {
                addressRepository.updateAddress(address)
                loadAddresses(address.userId)
            } catch (e: Exception) {
                _error.value = "Error al actualizar dirección: ${e.message}"
            }
        }
    }
    
    fun deleteAddress(addressId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                addressRepository.deleteAddress(addressId)
                loadAddresses(userId)
            } catch (e: Exception) {
                _error.value = "Error al eliminar dirección: ${e.message}"
            }
        }
    }
    
    fun setAsDefault(userId: Long, addressId: Long) {
        viewModelScope.launch {
            try {
                addressRepository.setAsDefaultAddress(userId, addressId)
                loadAddresses(userId)
            } catch (e: Exception) {
                _error.value = "Error al establecer dirección por defecto: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}