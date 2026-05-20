package com.example.lla.model

data class Vocabulary(
    var id: String = "",
    var word: String = "",
    var meaning: String = "",
    var pronunciation: String = "",
    var emoji: String = "",
    var lessonId: String = "" // Đã đổi topicId thành lessonId để khớp luồng Topic -> Lesson -> Vocabulary
)
