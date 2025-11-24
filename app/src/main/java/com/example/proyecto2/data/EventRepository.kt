package com.example.proyecto2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val eventsCollection = db.collection("events")

    // Obtener todos los eventos
    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .get()
                .await()

            val events = snapshot.documents
                .mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                .sortedBy { it.date }  // Ordenar por fecha

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Evento por ID
    suspend fun getEventById(eventId: String): Result<Event?> {
        return try {
            val snapshot = eventsCollection.document(eventId).get().await()
            val event = snapshot.toObject(Event::class.java)?.copy(id = snapshot.id)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crear nuevo evento
    suspend fun createEvent(event: Event): Result<String> {
        return try {
            val docRef = eventsCollection.add(event).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Asistencia a un evento
    suspend fun confirmAttendance(eventId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            eventsCollection.document(eventId)
                .update("attendees", FieldValue.arrayUnion(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cancelar asistencia
    suspend fun cancelAttendance(eventId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            eventsCollection.document(eventId)
                .update("attendees", FieldValue.arrayRemove(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eventos del usuario (a los que asiste)
    suspend fun getUserEvents(): Result<List<Event>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = eventsCollection
                .whereArrayContains("attendees", userId)
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)?.copy(id = doc.id)
            }
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Agregar comentario
    suspend fun addComment(eventId: String, commentText: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado"))

            val comment = Comment(
                userId = currentUser.uid,
                userName = currentUser.email?.substringBefore("@") ?: "Usuario",
                text = commentText,
                timestamp = Timestamp.now()
            )

            eventsCollection.document(eventId)
                .update("comments", FieldValue.arrayUnion(comment))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Agregar calificación
    suspend fun addRating(eventId: String, rating: Float): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado"))

            val eventDoc = eventsCollection.document(eventId).get().await()
            val event = eventDoc.toObject(Event::class.java) ?: return Result.failure(Exception("Evento no encontrado"))

            // Actualizar ratings
            val updatedRatings = event.ratings.toMutableMap()
            updatedRatings[currentUser.uid] = rating

            // Calcular nuevo promedio
            val averageRating = updatedRatings.values.average().toFloat()

            eventsCollection.document(eventId)
                .update(
                    mapOf(
                        "ratings" to updatedRatings,
                        "averageRating" to averageRating
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Obtener eventos creados por el usuario actual
    suspend fun getMyCreatedEvents(): Result<List<Event>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = eventsCollection
                .whereEqualTo("organizerId", userId)
                .get()
                .await()

            val events = snapshot.documents
                .mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                .sortedByDescending { it.date }  // Más recientes primero

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar evento
    suspend fun updateEvent(eventId: String, event: Event): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val existingEvent = eventsCollection.document(eventId).get().await()
            val existingEventData = existingEvent.toObject(Event::class.java)

            if (existingEventData?.organizerId != userId) {
                return Result.failure(Exception("No tienes permiso para editar este evento"))
            }

            val updateData = hashMapOf(
                "title" to event.title,
                "description" to event.description,
                "date" to event.date,
                "time" to event.time,
                "location" to event.location,
                "category" to event.category,
                "maxAttendees" to event.maxAttendees
            )

            eventsCollection.document(eventId)
                .update(updateData as Map<String, Any>)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar evento
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val existingEvent = eventsCollection.document(eventId).get().await()
            val existingEventData = existingEvent.toObject(Event::class.java)

            if (existingEventData?.organizerId != userId) {
                return Result.failure(Exception("No tienes permiso para eliminar este evento"))
            }

            eventsCollection.document(eventId).delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}