package com.example.lla.model

data class UserProgress(
    val wordId: String ="",
    val userId: String ="",
    val level: String ="",
    val lastReview: Long = System.currentTimeMillis()
)
