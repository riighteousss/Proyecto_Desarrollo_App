package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.mechanic.MechanicEntity
import com.example.uinavegacion.data.repository.MechanicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MechanicViewModel(
    private val mechanicRepository: MechanicRepository
) : ViewModel() {
    
    private val _allMechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val allMechanics: StateFlow<List<MechanicEntity>> = _allMechanics.asStateFlow()
    
    private val _availableMechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val availableMechanics: StateFlow<List<MechanicEntity>> = _availableMechanics.asStateFlow()
    
    private val _filteredMechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val filteredMechanics: StateFlow<List<MechanicEntity>> = _filteredMechanics.asStateFlow()
    
    private val _selectedMechanic = MutableStateFlow<MechanicEntity?>(null)
    val selectedMechanic: StateFlow<MechanicEntity?> = _selectedMechanic.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadAllMechanics()
        loadAvailableMechanics()
    }
    
    private fun loadAllMechanics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mechanicRepository.getAllMechanics().collect { mechanicList ->
                    _allMechanics.value = mechanicList
                    _filteredMechanics.value = mechanicList
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar mecánicos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadAvailableMechanics() {
        viewModelScope.launch {
            try {
                mechanicRepository.getAvailableMechanics().collect { mechanicList ->
                    _availableMechanics.value = mechanicList
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar mecánicos disponibles: ${e.message}"
            }
        }
    }
    
    fun getMechanicById(id: String) {
        viewModelScope.launch {
            try {
                val mechanic = mechanicRepository.getMechanicById(id)
                _selectedMechanic.value = mechanic
            } catch (e: Exception) {
                _error.value = "Error al obtener mecánico: ${e.message}"
            }
        }
    }
    
    fun filterBySpecialty(specialty: String) {
        viewModelScope.launch {
            try {
                mechanicRepository.getMechanicsBySpecialty(specialty).collect { mechanicList ->
                    _filteredMechanics.value = mechanicList
                }
            } catch (e: Exception) {
                _error.value = "Error al filtrar mecánicos: ${e.message}"
            }
        }
    }
    
    fun clearFilters() {
        _filteredMechanics.value = _allMechanics.value
    }
    
    fun refreshMechanics() {
        loadAllMechanics()
        loadAvailableMechanics()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSelectedMechanic() {
        _selectedMechanic.value = null
    }
}
