package com.example.myaiapp

import com.google.firebase.Timestamp
import java.util.Date

data class Note(
    val id: String,
    val title: String ,
    val content: String ,
    val timestamp: Timestamp? = null
)
