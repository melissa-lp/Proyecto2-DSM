package com.udb.proyecto2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udb.proyecto2.ui.screens.EventListScreen
import com.udb.proyecto2.ui.screens.LoginScreen
import com.udb.proyecto2.ui.theme.proyecto2Theme
import com.udb.proyecto2.viewmodel.AuthViewModel
import com.udb.proyecto2.viewmodel.EventViewModel
import com.udb.proyecto2.ui.screens.CreateEventScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            proyecto2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Proyecto2App()
                }
            }
        }
    }
}

@Composable
fun Proyecto2App() {
    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    val currentUser by authViewModel.currentUser.collectAsState()

    // Variable para controlar qué pantalla mostrar
    var currentScreen by remember { mutableStateOf("home") }

    if (currentUser == null) {
        LoginScreen(
            onNavigateToHome = {
                currentScreen = "home"
            },
            viewModel = authViewModel
        )
    } else {
        when (currentScreen) {
            "home" -> {
                EventListScreen(
                    onNavigateToDetail = { eventId ->
                        // TODO: Navegar a detalle
                    },
                    onNavigateToCreate = {
                        currentScreen = "create"
                    },
                    onSignOut = {
                        authViewModel.signOut()
                    },
                    viewModel = eventViewModel
                )
            }
            "create" -> {
                CreateEventScreen(
                    onNavigateBack = {
                        currentScreen = "home"
                    },
                    viewModel = eventViewModel
                )
            }
        }
    }
}
