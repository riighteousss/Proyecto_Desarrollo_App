package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Address(
    val id: String,
    val name: String,
    val address: String,
    val isDefault: Boolean
)

class AddressViewModel : ViewModel() {
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    init {
        // Cargar direcciones de ejemplo
        loadSampleAddresses()
    }

    private fun loadSampleAddresses() {
        val sampleAddresses = listOf(
            Address(
                id = "1",
                name = "Casa",
                address = "Av. Principal 123, Santiago",
                isDefault = true
            ),
            Address(
                id = "2",
                name = "Trabajo",
                address = "Calle Secundaria 456, Providencia",
                isDefault = false
            ),
            Address(
                id = "3",
                name = "Casa de mis padres",
                address = "Plaza Mayor 789, Las Condes",
                isDefault = false
            )
        )
        _addresses.value = sampleAddresses
    }

    fun addAddress(address: Address) {
        viewModelScope.launch {
            _addresses.value = _addresses.value + address
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch {
            _addresses.value = _addresses.value.map { 
                if (it.id == address.id) address else it 
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            _addresses.value = _addresses.value.filter { it.id != addressId }
        }
    }

    fun setDefaultAddress(addressId: String) {
        viewModelScope.launch {
            _addresses.value = _addresses.value.map { address ->
                address.copy(isDefault = address.id == addressId)
            }
        }
    }
}

