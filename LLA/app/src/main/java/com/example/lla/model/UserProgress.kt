package com.example.lla.model

data class UserProgress(
    var id: String = "",
    var userId: String = "",
    var vocabularyId: String = "",
    var level: Int = 0, // 1: Quên, 2: Khó, 3: Tốt, 4: Dễ
    var lastReview: Long = 0,
    var nextReview: Long = 0
)
