package com.example.proyecto2.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val eventsCollection = db.collection("events")

    // Eventos activos
    // Obtener todos los eventos (filtrado en memoria)
    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .get()  // Sin filtros ni ordenamiento en Firestore
                .await()

            // Filtrar y ordenar en memoria (no requiere índice)
            val events = snapshot.documents
                .mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                .filter { it.isActive }  // Filtrar activos
                .sortedBy { it.date }    // Ordenar por fecha

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
                .update("attendees", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
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
                .update("attendees", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
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
}