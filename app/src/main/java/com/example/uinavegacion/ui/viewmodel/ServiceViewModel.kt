package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.service.ServiceRequest
import com.example.uinavegacion.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(private val repo: ServiceRepository) : ViewModel() {
    private val _requests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val requests: StateFlow<List<ServiceRequest>> = _requests

    fun loadAll() {
        viewModelScope.launch {
            _requests.value = repo.getAll()
        }
    }

    fun create(request: ServiceRequest, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.insert(request)
            loadAll()
            onDone(id)
        }
    }
}
