package com.example.lla.model

import com.google.firebase.Timestamp

data class UserVocabularyProgress(
    var id: String = "",
    var userId: String = "",
    var vocabularyId: String = "",
    var word: String = "", // Lưu word để dễ tìm kiếm/hiển thị
    var level: Int = 0,    // Mức độ thuộc lòng (1-4)
    var lastReview: Timestamp? = null,
    var nextReview: Long = 0 // Thời gian ôn tập tiếp theo (mili giây)
)
