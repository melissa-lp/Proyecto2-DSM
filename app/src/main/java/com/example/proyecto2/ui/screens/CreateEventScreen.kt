package com.example.proyecto2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.example.proyecto2.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Deportes") }
    var customCategory by remember { mutableStateOf("") }
    var maxAttendees by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    val categories = listOf(
        "Deportes",
        "Música",
        "Educación",
        "Tecnología",
        "Arte y Cultura",
        "Gastronomía",
        "Negocios",
        "Salud y Bienestar",
        "Entretenimiento",
        "Otro"
    )

    // DatePicker Dialog
    val datePickerState = rememberDatePickerState()
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker Dialog
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título del evento
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del evento *") },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción *") },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            // Selector de Fecha
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Column {
                            Text(
                                text = "Fecha del evento *",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (selectedDate != null) {
                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        .format(Date(selectedDate!!))
                                } else {
                                    "Seleccionar fecha"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            }

            // Selector de Hora
            OutlinedCard(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Column {
                            Text(
                                text = "Hora del evento *",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format("%02d:%02d", selectedHour, selectedMinute),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                }
            }

            // Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación *") },
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Parque Cuscatlan") },
                singleLine = true
            )

            // Dropdown de Categoría
            // Dropdown de Categoría
            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                showCategoryDropdown = false
                                if (category != "Otro") {
                                    customCategory = ""
                                }
                            },
                            leadingIcon = {
                                if (selectedCategory == category) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }

            // Campo personalizado si selecciona "Otro"
            if (selectedCategory == "Otro") {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Especifica la categoría") },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Voluntariado") },
                    singleLine = true
                )
            }

            // Máximo de asistentes (opcional)
            OutlinedTextField(
                value = maxAttendees,
                onValueChange = { maxAttendees = it.filter { char -> char.isDigit() } },
                label = { Text("Máximo de asistentes (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.People, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Dejar vacío para sin límite") },
                singleLine = true
            )

            // Mensaje de error
            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón crear
            Button(
                onClick = {
                    // Validar campos
                    when {
                        title.isBlank() -> {
                            errorMessage = "El título es obligatorio"
                            showError = true
                        }
                        description.isBlank() -> {
                            errorMessage = "La descripción es obligatoria"
                            showError = true
                        }
                        selectedDate == null -> {
                            errorMessage = "Debes seleccionar una fecha"
                            showError = true
                        }
                        location.isBlank() -> {
                            errorMessage = "La ubicación es obligatoria"
                            showError = true
                        }
                        selectedCategory == "Otro" && customCategory.isBlank() -> {
                            errorMessage = "Debes especificar la categoría"
                            showError = true
                        }
                        else -> {
                            try {
                                // Crear timestamp con fecha y hora seleccionadas
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = selectedDate!!
                                    set(Calendar.HOUR_OF_DAY, selectedHour)
                                    set(Calendar.MINUTE, selectedMinute)
                                    set(Calendar.SECOND, 0)
                                }

                                val timestamp = Timestamp(calendar.time)
                                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                                val maxAttendeesInt = maxAttendees.toIntOrNull()
                                val finalCategory = if (selectedCategory == "Otro") customCategory else selectedCategory

                                viewModel.createEvent(
                                    title = title,
                                    description = description,
                                    date = timestamp,
                                    time = timeString,
                                    location = location,
                                    category = finalCategory,
                                    maxAttendees = maxAttendeesInt,
                                    onSuccess = {
                                        onNavigateBack()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        showError = true
                                    }
                                )
                                showError = false
                            } catch (e: Exception) {
                                errorMessage = "Error al crear el evento: ${e.message}"
                                showError = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Evento", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Nota sobre campos obligatorios
            Text(
                text = "* Campos obligatorios",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}