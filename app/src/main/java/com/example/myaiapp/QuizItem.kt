package com.example.myaiapp

data class QuizItem(
    val ans: String,
    val question: String,
    val quiz: String,
    val op1: String,
    val op2: String,
    val op3: String,
    val op4: String,
    var correctCount: Int = 0, // Thêm trường correctCount
    var incorrectCount: Int = 0 // Thêm trường incorrectCount
)
