package com.example.proyecto2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto2.data.Event
import com.example.proyecto2.data.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getEvents().fold(
                onSuccess = { eventList ->
                    _events.value = eventList
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al cargar eventos"
                    _isLoading.value = false
                }
            )
        }
    }

    fun confirmAttendance(eventId: String) {
        viewModelScope.launch {
            repository.confirmAttendance(eventId).fold(
                onSuccess = {
                    loadEvents() // Recargar para actualizar la lista
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al confirmar asistencia"
                }
            )
        }
    }

    fun cancelAttendance(eventId: String) {
        viewModelScope.launch {
            repository.cancelAttendance(eventId).fold(
                onSuccess = {
                    loadEvents()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al cancelar asistencia"
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun createEvent(
        title: String,
        description: String,
        date: Timestamp,
        time: String,
        location: String,
        category: String,
        maxAttendees: Int?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                onError("Debes iniciar sesión para crear eventos")
                _isLoading.value = false
                return@launch
            }

            val event = Event(
                title = title,
                description = description,
                date = date,
                time = time,
                location = location,
                category = category,
                organizerId = currentUser.uid,
                organizerName = currentUser.email ?: "Usuario",
                maxAttendees = maxAttendees,
                isActive = true,
                createdAt = Timestamp.now()
            )

            repository.createEvent(event).fold(
                onSuccess = {
                    _isLoading.value = false
                    loadEvents()
                    onSuccess()
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                    onError(exception.message ?: "Error al crear evento")
                }
            )
        }
    }

    fun loadMyEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getUserEvents().fold(
                onSuccess = { eventList ->
                    _events.value = eventList
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al cargar mis eventos"
                    _isLoading.value = false
                }
            )
        }
    }
    fun cancelAttendanceFromMyEvents(eventId: String) {
        viewModelScope.launch {
            repository.cancelAttendance(eventId).fold(
                onSuccess = {
                    loadMyEvents()
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al cancelar asistencia"
                }
            )
        }
    }
}