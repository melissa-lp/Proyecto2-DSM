package com.example.proyecto2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.proyecto2.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    // Listener de cambios en autenticación
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        // Agregar listener
        auth.addAuthStateListener(authStateListener)
        // Establecer usuario actual
        _currentUser.value = auth.currentUser
    }

    override fun onCleared() {
        super.onCleared()
        // Remover listener cuando se destruye el ViewModel
        auth.removeAuthStateListener(authStateListener)
    }

    fun signUpWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email y contraseña no pueden estar vacíos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUpWithEmail(email, password)
            _authState.value = if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al registrarse")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email y contraseña no pueden estar vacíos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
            _authState.value = if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signInWithGoogle(idToken)

            // Agregar logs
            println("🔍 Google Sign-In Result: ${result.isSuccess}")
            println("🔍 User: ${result.getOrNull()?.email}")

            _authState.value = if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión con Google")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}