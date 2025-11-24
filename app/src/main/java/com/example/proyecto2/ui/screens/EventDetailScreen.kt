package com.example.proyecto2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto2.data.Comment
import com.example.proyecto2.data.Event
import com.example.proyecto2.viewmodel.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val event = events.find { it.id == eventId }

    var showCommentDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showShareDialog = true }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Evento no encontrado")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Volver")
                    }
                }
            }
        } else {
            EventDetailContent(
                event = event,
                currentUserId = currentUserId,
                onAttendClick = { viewModel.confirmAttendance(eventId) },
                onCancelAttendanceClick = { viewModel.cancelAttendance(eventId) },
                onCommentClick = { showCommentDialog = true },
                onRateClick = { showRatingDialog = true },
                modifier = Modifier.padding(padding)
            )
        }
    }

    // Diálogo para compartir
    if (showShareDialog && event != null) {
        ShareEventDialog(
            event = event,
            onDismiss = { showShareDialog = false }
        )
    }

    // Diálogo para comentar
    if (showCommentDialog && event != null) {
        CommentDialog(
            onDismiss = { showCommentDialog = false },
            onSubmit = { comment ->
                viewModel.addComment(eventId, comment)
                showCommentDialog = false
            }
        )
    }

    // Diálogo para calificar
    if (showRatingDialog && event != null) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onSubmit = { rating ->
                viewModel.addRating(eventId, rating)
                showRatingDialog = false
            }
        )
    }
}

@Composable
fun EventDetailContent(
    event: Event,
    currentUserId: String,
    onAttendClick: () -> Unit,
    onCancelAttendanceClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    val formattedDate = dateFormat.format(event.date.toDate())

    val isUserAttending = event.attendees.contains(currentUserId)
    val isFull = event.maxAttendees?.let { event.attendees.size >= it } ?: false
    val isPastEvent = event.date.seconds < com.google.firebase.Timestamp.now().seconds

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen del evento (placeholder)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Título y categoría
        item {
            Column {
                AssistChip(
                    onClick = { },
                    label = { Text(event.category) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Descripción
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Información del evento
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    InfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Fecha",
                        value = formattedDate
                    )

                    InfoRow(
                        icon = Icons.Default.Schedule,
                        label = "Hora",
                        value = event.time
                    )

                    InfoRow(
                        icon = Icons.Default.Place,
                        label = "Ubicación",
                        value = event.location
                    )

                    InfoRow(
                        icon = Icons.Default.People,
                        label = "Asistentes",
                        value = "${event.attendees.size}${event.maxAttendees?.let { "/$it" } ?: ""}"
                    )

                    if (event.averageRating > 0) {
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Calificación",
                            value = String.format("%.1f / 5.0", event.averageRating)
                        )
                    }
                }
            }
        }

        // Botones de acción
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isPastEvent) {
                    if (isUserAttending) {
                        OutlinedButton(
                            onClick = onCancelAttendanceClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar asistencia")
                        }
                    } else {
                        Button(
                            onClick = onAttendClick,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isFull
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isFull) "Evento lleno" else "Confirmar asistencia")
                        }
                    }
                }

                if (isPastEvent && isUserAttending) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCommentClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Comment, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Comentar")
                        }

                        OutlinedButton(
                            onClick = onRateClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Calificar")
                        }
                    }
                }
            }
        }

        // Comentarios
        if (event.comments.isNotEmpty()) {
            item {
                Text(
                    text = "Comentarios (${event.comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(event.comments) { comment ->
                CommentCard(comment = comment)
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(comment.timestamp.toDate()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ========== DIÁLOGO PARA COMENTAR ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Comment, contentDescription = null)
                Text("Agregar comentario")
            }
        },
        text = {
            Column {
                Text(
                    text = "Comparte tu experiencia en este evento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Comentario") },
                    placeholder = { Text("Escribe tu comentario aquí...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 5,
                    supportingText = {
                        Text("${commentText.length}/500")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onSubmit(commentText.trim())
                    }
                },
                enabled = commentText.isNotBlank() && commentText.length <= 500
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ========== DIÁLOGO PARA CALIFICAR ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Float) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null)
                Text("Calificar evento")
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Qué te pareció el evento?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Estrellas
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { rating = i.toFloat() }
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$i estrellas",
                                modifier = Modifier.size(40.dp),
                                tint = if (i <= rating) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (rating > 0) {
                    Text(
                        text = when (rating.toInt()) {
                            1 -> "Muy malo"
                            2 -> "Malo"
                            3 -> "Regular"
                            4 -> "Bueno"
                            5 -> "Excelente"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating) },
                enabled = rating > 0
            ) {
                Text("Calificar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ========== DIÁLOGO PARA COMPARTIR ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareEventDialog(
    event: Event,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(event.date.toDate())

    val shareText = """
        ¡Te invito a este evento!
        
        📅 ${event.title}
        📍 ${event.location}
        🗓️ $formattedDate a las ${event.time}
        
        ${event.description}
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Text("Compartir evento")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Comparte este evento en:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Opciones de compartir
                ShareOptionButton(
                    icon = Icons.Default.Email,
                    text = "Correo electrónico",
                    onClick = {
                        // Simular compartir por email
                        println("📧 Compartir por email: $shareText")
                        onDismiss()
                    }
                )

                ShareOptionButton(
                    icon = Icons.Default.Share,
                    text = "Redes sociales",
                    onClick = {
                        // Simular compartir en redes sociales
                        println("📱 Compartir en redes sociales: $shareText")
                        onDismiss()
                    }
                )

                ShareOptionButton(
                    icon = Icons.Default.ContentCopy,
                    text = "Copiar enlace",
                    onClick = {
                        // Simular copiar enlace
                        println("🔗 Enlace copiado: evento/${event.id}")
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ShareOptionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}