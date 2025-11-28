package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * REQUESTFORMVIEWMODEL - MANEJO DE ESTADO DEL FORMULARIO
 * 
 * PUNTO CLAVE: Este ViewModel mantiene el estado del formulario de solicitud
 * - Preserva los datos cuando navegas entre pantallas
 * - Maneja el estado de la cámara y las imágenes
 * - Permite limpiar el formulario cuando se completa
 * 
 * ESTADOS PRINCIPALES:
 * - selectedService: Tipo de servicio seleccionado
 * - selectedVehicle: Vehículo seleccionado
 * - description: Descripción del problema
 * - selectedImages: Lista de imágenes capturadas
 * - isFormValid: Si el formulario está completo
 * 
 * FUNCIONES PRINCIPALES:
 * - updateService() → Actualiza tipo de servicio
 * - updateVehicle() → Actualiza vehículo
 * - updateDescription() → Actualiza descripción
 * - addImage() → Agrega imagen capturada
 * - clearForm() → Limpia todo el formulario
 */
class RequestFormViewModel : ViewModel() {
    
    // Estados del formulario
    private val _selectedService = MutableStateFlow("")
    val selectedService: StateFlow<String> = _selectedService.asStateFlow()
    
    private val _selectedVehicle = MutableStateFlow("")
    val selectedVehicle: StateFlow<String> = _selectedVehicle.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _selectedImages = MutableStateFlow<List<String>>(emptyList())
    val selectedImages: StateFlow<List<String>> = _selectedImages.asStateFlow()
    
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()
    
    // Función para actualizar tipo de servicio
    fun updateService(service: String) {
        _selectedService.value = service
        updateFormValidity()
    }
    
    // Función para actualizar vehículo
    fun updateVehicle(vehicle: String) {
        _selectedVehicle.value = vehicle
        updateFormValidity()
    }
    
    // Función para actualizar descripción
    fun updateDescription(description: String) {
        _description.value = description
        updateFormValidity()
    }
    
    // Función para agregar imagen
    fun addImage(imagePath: String) {
        val currentImages = _selectedImages.value.toMutableList()
        currentImages.add(imagePath)
        _selectedImages.value = currentImages
    }
    
    // Función para remover imagen
    fun removeImage(imagePath: String) {
        val currentImages = _selectedImages.value.toMutableList()
        currentImages.remove(imagePath)
        _selectedImages.value = currentImages
    }
    
    // Función para limpiar todo el formulario
    fun clearForm() {
        _selectedService.value = ""
        _selectedVehicle.value = ""
        _description.value = ""
        _selectedImages.value = emptyList()
        _isFormValid.value = false
    }
    
    // Función para obtener todos los datos del formulario
    fun getFormData(): RequestFormData {
        return RequestFormData(
            service = _selectedService.value,
            vehicle = _selectedVehicle.value,
            description = _description.value,
            images = _selectedImages.value
        )
    }
    
    // Función para cargar datos en el formulario
    fun loadFormData(data: RequestFormData) {
        _selectedService.value = data.service
        _selectedVehicle.value = data.vehicle
        _description.value = data.description
        _selectedImages.value = data.images
        updateFormValidity()
    }
    
    // Función privada para actualizar la validez del formulario
    private fun updateFormValidity() {
        _isFormValid.value = _selectedService.value.isNotEmpty() && 
                            _selectedVehicle.value.isNotEmpty() && 
                            _description.value.isNotEmpty()
    }
}

/**
 * Clase de datos para el formulario
 */
data class RequestFormData(
    val service: String,
    val vehicle: String,
    val description: String,
    val images: List<String>
)
