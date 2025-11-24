package com.example.proyecto2.data

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val time: String = "",
    val location: String = "",
    val category: String = "",
    val organizerId: String = "",
    val organizerName: String = "",
    val attendees: List<String> = emptyList(),
    val maxAttendees: Int? = null,
    val comments: List<Comment> = emptyList(),
    val ratings: Map<String, Float> = emptyMap(),
    val averageRating: Float = 0f
) {
    fun isUserAttending(userId: String): Boolean {
        return attendees.contains(userId)
    }

    fun isFull(): Boolean {
        return maxAttendees?.let { attendees.size >= it } ?: false
    }
}