package com.example.proyecto2.data

import com.google.firebase.Timestamp

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)