package com.example.lla.uis.topic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lla.model.Topic
import com.example.lla.model.Vocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TopicViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _vocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabularies: StateFlow<List<Vocabulary>> = _vocabularies.asStateFlow()

    private val _learnedVocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val learnedVocabularies: StateFlow<List<Vocabulary>> = _learnedVocabularies.asStateFlow()

    private val _reviewVocabularies = MutableStateFlow<List<Vocabulary>>(emptyList())
    val reviewVocabularies: StateFlow<List<Vocabulary>> = _reviewVocabularies.asStateFlow()

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
                val topicList = snapshot.documents.mapNotNull { doc ->
                    val topic = doc.toObject(Topic::class.java)
                    topic?.id = doc.id
                    topic
                }
                _topics.value = topicList
            } catch (e: Exception) {
                Log.e("TopicVM", "Lỗi fetchTopics: \${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchVocabularies(topicId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _vocabularies.value = emptyList()
            try {
                val snapshot = firestore.collection("vocabularies")
                    .whereEqualTo("topicId", topicId)
                    .get().await()
                
                val vocabList = snapshot.documents.mapNotNull { doc ->
                    val vocab = doc.toObject(Vocabulary::class.java)
                    vocab?.id = doc.id
                    vocab
                }
                _vocabularies.value = vocabList
            } catch (e: Exception) {
                Log.e("TopicVM", "Lỗi fetchVocabularies: \${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProgress(userId: String, vocab: Vocabulary, level: Int) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val interval = when (level) {
                    1 -> 1 * 60 * 1000L // 1 phút
                    2 -> 24 * 60 * 60 * 1000L // 1 ngày
                    3 -> 3 * 24 * 60 * 60 * 1000L // 3 ngày
                    4 -> 7 * 24 * 60 * 60 * 1000L // 7 ngày
                    else -> 0L
                }

                val querySnapshot = firestore.collection("userProgress")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("vocabularyId", vocab.id)
                    .get().await()

                val data = hashMapOf(
                    "userId" to userId,
                    "vocabularyId" to vocab.id,
                    "word" to vocab.word,
                    "level" to level.toString(),
                    "lastReview" to com.google.firebase.Timestamp.now(),
                    "nextReview" to now + interval
                )

                if (querySnapshot.isEmpty) {
                    firestore.collection("userProgress").add(data).await()
                } else {
                    querySnapshot.documents[0].reference.update(data as Map<String, Any>).await()
                }
                fetchLearnedVocabularies(userId)
                fetchReviewVocabularies(userId) // Cập nhật lại danh sách ôn tập
            } catch (e: Exception) {
                Log.e("TopicVM", "Lỗi updateProgress: \${e.message}")
            }
        }
    }

    fun fetchReviewVocabularies(userId: String) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val progressSnapshot = firestore.collection("userProgress")
                    .whereEqualTo("userId", userId)
                    .whereLessThanOrEqualTo("nextReview", now)
                    .get().await()

                val vocabIds = progressSnapshot.documents.mapNotNull { it.getString("vocabularyId") }
                
                if (vocabIds.isNotEmpty()) {
                    val vocabList = mutableListOf<Vocabulary>()
                    vocabIds.distinct().chunked(10).forEach { chunk ->
                        val vocabSnapshot = firestore.collection("vocabularies")
                            .whereIn("__name__", chunk)
                            .get().await()
                        
                        vocabSnapshot.documents.forEach { doc ->
                            doc.toObject(Vocabulary::class.java)?.let {
                                it.id = doc.id
                                vocabList.add(it)
                            }
                        }
                    }
                    _reviewVocabularies.value = vocabList
                } else {
                    _reviewVocabularies.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("TopicVM", "Lỗi fetchReviewVocabularies: \${e.message}")
            }
        }
    }

    // Gán dữ liệu ôn tập vào danh sách từ vựng hiện tại để FlashcardScreen có thể dùng chung
    fun setVocabulariesForReview() {
        _vocabularies.value = _reviewVocabularies.value
    }

    fun fetchLearnedVocabularies(userId: String) {
        viewModelScope.launch {
            try {
                val progressSnapshot = firestore.collection("userProgress")
                    .whereEqualTo("userId", userId)
                    .get().await()

                val vocabIds = progressSnapshot.documents.mapNotNull { it.getString("vocabularyId") }
                
                if (vocabIds.isNotEmpty()) {
                    val vocabList = mutableListOf<Vocabulary>()
                    vocabIds.distinct().chunked(10).forEach { chunk ->
                        val vocabSnapshot = firestore.collection("vocabularies")
                            .whereIn("__name__", chunk)
                            .get().await()
                        
                        vocabSnapshot.documents.forEach { doc ->
                            doc.toObject(Vocabulary::class.java)?.let {
                                it.id = doc.id
                                vocabList.add(it)
                            }
                        }
                    }
                    _learnedVocabularies.value = vocabList
                } else {
                    _learnedVocabularies.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("TopicVM", "Lỗi fetchLearnedVocabularies: \${e.message}")
            }
        }
    }
}
