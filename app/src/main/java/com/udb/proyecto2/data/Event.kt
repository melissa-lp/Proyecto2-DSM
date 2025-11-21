package com.udb.proyecto2.data

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val time: String = "",
    val location: String = "",
    val organizerId: String = "",
    val organizerName: String = "",
    val attendees: List<String> = emptyList(),
    val maxAttendees: Int? = null,
    val imageUrl: String? = null,
    val category: String = "General",
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val averageRating: Double = 0.0,
    val totalRatings: Int = 0
) {
    fun isUserAttending(userId: String): Boolean {
        return attendees.contains(userId)
    }

    //Se verifica si el evento está lleno
    fun isFull(): Boolean {
        return maxAttendees?.let { attendees.size >= it } ?: false
    }
}