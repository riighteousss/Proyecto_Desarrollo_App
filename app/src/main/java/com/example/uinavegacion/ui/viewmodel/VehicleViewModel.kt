package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val year: String,
    val plate: String,
    val color: String
)

class VehicleViewModel : ViewModel() {
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    init {
        // Cargar vehículos de ejemplo
        loadSampleVehicles()
    }

    private fun loadSampleVehicles() {
        val sampleVehicles = listOf(
            Vehicle(
                id = "1",
                brand = "Toyota",
                model = "Corolla",
                year = "2020",
                plate = "ABC-123",
                color = "Blanco"
            ),
            Vehicle(
                id = "2",
                brand = "Honda",
                model = "Civic",
                year = "2019",
                plate = "XYZ-789",
                color = "Negro"
            )
        )
        _vehicles.value = sampleVehicles
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            _vehicles.value = _vehicles.value + vehicle
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            _vehicles.value = _vehicles.value.map { 
                if (it.id == vehicle.id) vehicle else it 
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            _vehicles.value = _vehicles.value.filter { it.id != vehicleId }
        }
    }
}

