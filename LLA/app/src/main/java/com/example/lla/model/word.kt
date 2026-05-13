package com.example.lla.model

data class Word(
    val id: String = "",
    val topicId: String = "",
    val original: String = "",
    val translation: String = "",
    val phonetic: String = "",
    val example: String = "",
    val audioUrl: String = ""
)