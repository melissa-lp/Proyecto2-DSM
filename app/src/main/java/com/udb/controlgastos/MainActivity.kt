package com.udb.proyecto2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udb.proyecto2.data.Expense
import com.udb.proyecto2.ui.screens.AddExpenseScreen
import com.udb.proyecto2.ui.screens.EditExpenseScreen
import com.udb.proyecto2.ui.screens.HomeScreen
import com.udb.proyecto2.ui.screens.LoginScreen
import com.udb.proyecto2.ui.theme.proyecto2Theme
import com.udb.proyecto2.viewmodel.AuthViewModel

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
                    proyecto2App()
                }
            }
        }
    }
}

@Composable
fun proyecto2App(viewModel: AuthViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    val currentUser by viewModel.currentUser.collectAsState()

    // Actualizar pantalla basado en el usuario actual
    LaunchedEffect(currentUser) {
        if (currentUser != null && currentScreen == Screen.Login) {
            currentScreen = Screen.Home
        } else if (currentUser == null && currentScreen != Screen.Login) {
            currentScreen = Screen.Login
        }
    }

    when (currentScreen) {
        Screen.Login -> {
            LoginScreen(
                onNavigateToHome = { currentScreen = Screen.Home },
                viewModel = viewModel
            )
        }
        Screen.Home -> {
            HomeScreen(
                onSignOut = { currentScreen = Screen.Login },
                onNavigateToAddExpense = { currentScreen = Screen.AddExpense },
                onNavigateToEditExpense = { expense ->
                    currentScreen = Screen.EditExpense(expense)
                },
                authViewModel = viewModel
            )
        }
        Screen.AddExpense -> {
            AddExpenseScreen(
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }
        is Screen.EditExpense -> {
            EditExpenseScreen(
                expense = (currentScreen as Screen.EditExpense).expense,
                onNavigateBack = { currentScreen = Screen.Home }
            )
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object AddExpense : Screen()
    data class EditExpense(val expense: Expense) : Screen()
}