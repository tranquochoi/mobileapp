package com.example.myaiapp

import java.util.Date

data class Note(
    val title: String ,
    val content: String ,
    val timestamp: Date = Date()
)