package com.example.lla.uis.topic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lla.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TopicViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    private val _vocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabularies: StateFlow<List<Vocabulary>> = _vocabularies.asStateFlow()

    private val _reviewVocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val reviewVocabularies: StateFlow<List<Vocabulary>> = _reviewVocabularies.asStateFlow()

    private val _learnedVocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val learnedVocabularies: StateFlow<List<Vocabulary>> = _learnedVocabularies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchTopics()
    }

    fun fetchTopics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("topics").get().await()
                _topics.value = snapshot.documents.mapNotNull { it.toObject(Topic::class.java)?.apply { id = it.id } }
            } catch (e: Exception) {
                Log.e("TopicVM", "Error topics: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun fetchLessons(topicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("lessons")
                    .whereEqualTo("topicId", topicId).get().await()

                // 1. Tạo danh sách các công việc chạy song song (Deferred)
                val deferredLessons = snapshot.documents.map { doc ->
                    async {
                        val lesson = doc.toObject(Lesson::class.java)
                        if (lesson != null) {
                            lesson.id = doc.id
                            try {
                                // Gọi count từ server
                                val countSnapshot = firestore.collection("vocabularies")
                                    .whereEqualTo("lessonId", doc.id)
                                    .count().get(com.google.firebase.firestore.AggregateSource.SERVER).await()
                                lesson.wordCount = countSnapshot.count.toInt()
                            } catch (e: Exception) {
                                lesson.wordCount = 0 // Dự phòng nếu lỗi
                            }
                        }
                        lesson
                    }
                }

                // 2. Chờ tất cả các công việc song song hoàn thành cùng lúc
                val lessonList = deferredLessons.awaitAll().filterNotNull()

                _lessons.value = lessonList
            } catch (e: Exception) {
                Log.e("TopicVM", "Error lessons: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchVocabulariesByLesson(lessonId: String) {
        viewModelScope.launch {
            if (lessonId == "review") return@launch
            _isLoading.value = true
            _vocabularies.value = emptyList()
            try {
                val snapshot = firestore.collection("vocabularies").whereEqualTo("lessonId", lessonId).get().await()
                _vocabularies.value = snapshot.documents.mapNotNull { it.toObject(Vocabulary::class.java)?.apply { id = it.id } }
            } catch (e: Exception) {
                Log.e("TopicVM", "Error vocab: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun updateVocabProgress(userId: String, vocab: Vocabulary, level: Int) {
        // KIỂM TRA 1: Dữ liệu đầu vào có bị rỗng không?
        if (userId.isBlank()) {
            Log.e("TopicVM_Debug", "LỖI: userId bị rỗng. Người dùng đã đăng nhập chưa?")
            return
        }
        if (vocab.id.isBlank()) {
            Log.e("TopicVM_Debug", "LỖI: vocab.id bị rỗng. Kiểm tra lại dữ liệu nạp từ Firestore.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("TopicVM_Debug", "Bắt đầu lưu tiến độ cho từ: ${vocab.word} (ID: ${vocab.id})")

                val now = System.currentTimeMillis()
                val interval = when (level) {
                    1 -> 60 * 1000L
                    2 -> 24 * 3600 * 1000L
                    3 -> 3 * 24 * 3600 * 1000L
                    4 -> 7 * 24 * 3600 * 1000L
                    else -> 0L
                }

                val progressDocId = "${userId}_${vocab.id}"
                val data = hashMapOf(
                    "userId" to userId,
                    "vocabularyId" to vocab.id,
                    "word" to vocab.word,
                    "level" to level,
                    "lastReview" to now,
                    "nextReview" to now + interval
                )

                // KIỂM TRA 2: Kết nối mạng và Quyền hạn (Rules)
                firestore.collection("userProgress")
                    .document(progressDocId)
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("TopicVM_Debug", "===> SUCCESS: Đã lưu dữ liệu lên Cloud thành công!")
                    }
                    .addOnFailureListener { e ->
                        // Nếu vào đây: 90% là do Firestore Rules chưa cho phép (Permission Denied)
                        Log.e("TopicVM_Debug", "===> FAILURE: Không thể ghi dữ liệu. Lỗi: ${e.message}")
                    }
                    .await()

                // KIỂM TRA 3: Cập nhật UI
                Log.d("TopicVM_Debug", "Đang làm mới danh sách đã học và ôn tập...")
                fetchReviewVocabularies(userId)
                fetchLearnedVocabularies(userId)

            } catch (e: Exception) {
                Log.e("TopicVM_Debug", "===> CRASH: Lỗi ngoại lệ trong Coroutine: ${e.message}")
            }
        }
    }

//    fun updateVocabProgress(userId: String, vocab: Vocabulary, level: Int) {
//        viewModelScope.launch {
//            try {
//                val now = System.currentTimeMillis()
//                val interval = when (level) {
//                    1 -> 60 * 1000L // 1 phút
//                    2 -> 24 * 3600 * 1000L // 1 ngày
//                    3 -> 3 * 24 * 3600 * 1000L // 3 ngày
//                    4 -> 7 * 24 * 3600 * 1000L // 7 ngày
//                    else -> 0L
//                }
//
//                val query = firestore.collection("userProgress")
//                    .whereEqualTo("userId", userId)
//                    .whereEqualTo("vocabularyId", vocab.id)
//                    .get().await()
//
//                val data = hashMapOf(
//                    "userId" to userId,
//                    "vocabularyId" to vocab.id,
//                    "word" to vocab.word,
//                    "level" to level,
//                    "lastReview" to now,
//                    "nextReview" to now + interval
//                )
//
//                if (query.isEmpty) {
//                    firestore.collection("userProgress").add(data)
//                } else {
//                    query.documents[0].reference.update(data as Map<String, Any>)
//                }
//                fetchReviewVocabularies(userId)
//            } catch (e: Exception) {
//                Log.e("TopicVM", "Error save progress: ${e.message}")
//            }
//        }
//    }

    fun fetchReviewVocabularies(userId: String) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val snapshot = firestore.collection("userProgress")
                    .whereEqualTo("userId", userId)
                    .whereLessThanOrEqualTo("nextReview", now)
                    .get().await()

                val vocabIds = snapshot.documents.mapNotNull { it.getString("vocabularyId") }
                if (vocabIds.isNotEmpty()) {
                    val vocabList = mutableListOf<Vocabulary>()
                    vocabIds.distinct().chunked(10).forEach { chunk ->
                        val vSnapshot = firestore.collection("vocabularies").whereIn("__name__", chunk).get().await()
                        vSnapshot.documents.forEach { doc -> 
                            doc.toObject(Vocabulary::class.java)?.let { it.id = doc.id; vocabList.add(it) }
                        }
                    }
                    _reviewVocabularies.value = vocabList
                } else {
                    _reviewVocabularies.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("TopicVM", "Error review: ${e.message}")
            }
        }
    }

    fun prepareReviewMode() {
        _vocabularies.value = _reviewVocabularies.value
    }

    // MỚI: Chuẩn bị ôn tập từ danh sách từ đã học ở Profile
    fun prepareReviewLearnedMode() {
        _vocabularies.value = _learnedVocabularies.value
    }

    fun fetchLearnedVocabularies(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("userProgress").whereEqualTo("userId", userId).get().await()
                val vocabIds = snapshot.documents.mapNotNull { it.getString("vocabularyId") }
                if (vocabIds.isNotEmpty()) {
                    val vocabList = mutableListOf<Vocabulary>()
                    vocabIds.distinct().chunked(10).forEach { chunk ->
                        val vSnapshot = firestore.collection("vocabularies").whereIn("__name__", chunk).get().await()
                        vSnapshot.documents.forEach { doc -> 
                            doc.toObject(Vocabulary::class.java)?.let { it.id = doc.id; vocabList.add(it) }
                        }
                    }
                    _learnedVocabularies.value = vocabList
                } else {
                    _learnedVocabularies.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("TopicVM", "Error learned: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
