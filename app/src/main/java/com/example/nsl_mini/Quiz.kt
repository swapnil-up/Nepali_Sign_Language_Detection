package com.example.nsl_mini

data class Quiz(
    var id: String? = null,
    var imageUrl: String? = null,
    var options: List<String> = listOf(),
    var correctAnswer: String? = null
)
