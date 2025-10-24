package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.vehicle.VehicleEntity
import com.example.uinavegacion.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    
    private val _vehicles = MutableStateFlow<List<VehicleEntity>>(emptyList())
    val vehicles: StateFlow<List<VehicleEntity>> = _vehicles.asStateFlow()
    
    private val _defaultVehicle = MutableStateFlow<VehicleEntity?>(null)
    val defaultVehicle: StateFlow<VehicleEntity?> = _defaultVehicle.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadVehicles(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                vehicleRepository.getVehiclesByUser(userId).collect { vehicleList ->
                    _vehicles.value = vehicleList
                    _defaultVehicle.value = vehicleList.firstOrNull { it.isDefault }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar vehículos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            try {
                vehicleRepository.insertVehicle(vehicle)
                loadVehicles(vehicle.userId)
            } catch (e: Exception) {
                _error.value = "Error al agregar vehículo: ${e.message}"
            }
        }
    }
    
    fun updateVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            try {
                vehicleRepository.updateVehicle(vehicle)
                loadVehicles(vehicle.userId)
            } catch (e: Exception) {
                _error.value = "Error al actualizar vehículo: ${e.message}"
            }
        }
    }
    
    fun deleteVehicle(vehicleId: Long, userId: Long) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicle(vehicleId)
                loadVehicles(userId)
            } catch (e: Exception) {
                _error.value = "Error al eliminar vehículo: ${e.message}"
            }
        }
    }
    
    fun setAsDefault(userId: Long, vehicleId: Long) {
        viewModelScope.launch {
            try {
                vehicleRepository.setAsDefaultVehicle(userId, vehicleId)
                loadVehicles(userId)
            } catch (e: Exception) {
                _error.value = "Error al establecer vehículo por defecto: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}