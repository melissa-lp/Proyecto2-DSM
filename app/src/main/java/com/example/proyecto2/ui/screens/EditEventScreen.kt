package com.example.proyecto2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyecto2.data.Event
import com.example.proyecto2.viewmodel.EventViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    event: Event,
    onNavigateBack: () -> Unit,
    onDelete: () -> Unit,
    viewModel: EventViewModel
) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var location by remember { mutableStateOf(event.location) }
    var category by remember { mutableStateOf(event.category) }
    var time by remember { mutableStateOf(event.time) }
    var maxAttendees by remember { mutableStateOf(event.maxAttendees?.toString() ?: "") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var dateString by remember { mutableStateOf(dateFormat.format(event.date.toDate())) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val categories = listOf(
        "Conferencia",
        "Taller",
        "Networking",
        "Deportes",
        "Cultura",
        "Tecnología",
        "Educación",
        "Otro"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del evento") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                singleLine = true
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                maxLines = 4
            )

            // Fecha
            OutlinedTextField(
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Fecha (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                placeholder = { Text("01/01/2025") },
                singleLine = true
            )

            // Hora
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Hora (HH:MM)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                placeholder = { Text("14:00") },
                singleLine = true
            )

            // Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                singleLine = true
            )

            // Categoría
            var expandedCategory by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) }
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Capacidad máxima
            OutlinedTextField(
                value = maxAttendees,
                onValueChange = { maxAttendees = it.filter { char -> char.isDigit() } },
                label = { Text("Capacidad máxima (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Ej: 50") },
                singleLine = true
            )

            // Información de asistentes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Asistentes confirmados:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "${event.attendees.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Mensaje de error
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón guardar cambios
            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank() || dateString.isBlank() ||
                        time.isBlank() || location.isBlank() || category.isBlank()
                    ) {
                        errorMessage = "Por favor completa todos los campos"
                        return@Button
                    }

                    try {
                        val date = dateFormat.parse(dateString)
                        val timestamp = Timestamp(date ?: Date())

                        viewModel.updateEvent(
                            eventId = event.id,
                            title = title,
                            description = description,
                            date = timestamp,
                            time = time,
                            location = location,
                            category = category,
                            maxAttendees = maxAttendees.toIntOrNull(),
                            onSuccess = {
                                onNavigateBack()
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    } catch (e: Exception) {
                        errorMessage = "Formato de fecha inválido (DD/MM/AAAA)"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar cambios")
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("¿Eliminar evento?")
                }
            },
            text = {
                Column {
                    Text("Esta acción no se puede deshacer.")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (event.attendees.isNotEmpty()) {
                        Text(
                            "⚠️ Hay ${event.attendees.size} personas confirmadas para este evento.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(
                            eventId = event.id,
                            onSuccess = {
                                showDeleteDialog = false
                                onDelete()
                            },
                            onError = { error ->
                                errorMessage = error
                                showDeleteDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}