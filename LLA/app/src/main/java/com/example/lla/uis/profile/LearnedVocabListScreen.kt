package com.example.lla.uis.profile

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.LightBlue
import com.example.lla.ui.theme.PrimaryColor
import com.example.lla.ui.theme.SecondaryColor
import com.example.lla.uis.topic.TopicViewModel
import kotlin.text.contains

@Composable
fun LearnedVocabListScreen(
    viewModel: TopicViewModel, // Nhớ import ViewModel của bạn
    onBack: () -> Unit
) {
    val learnedVocabs by viewModel.learnedVocabularies.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current


    val filteredList = remember(learnedVocabs, searchQuery) {
        learnedVocabs.filter {
            it.word.contains(searchQuery, ignoreCase = true) ||
                    it.meaning.contains(searchQuery, ignoreCase = true)
        }
    }


    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        lateinit var textToSpeech: TextToSpeech

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = java.util.Locale.US
            }
        }
        tts = textToSpeech

        // Hủy TTS khi rời khỏi màn hình này
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FE))
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Back",
                    modifier = Modifier.rotate(180f)
                )
            }
            Text(
                text = "Từ vựng đã học",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Ô tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            placeholder = { Text("Tìm kiếm từ vựng...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
            shape = RoundedCornerShape(12.dp)
        )

        // Danh sách từ vựng
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { vocab ->
                FullInfoVocabItem(
                    vocab = vocab,
                    onSpeak = {
                        // Kiểm tra an toàn trước khi phát âm
                        tts?.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                )
            }
        }
    }
}

@Composable
fun FullInfoVocabItem(vocab: com.example.lla.model.Vocabulary, onSpeak: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = vocab.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vocab.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PrimaryColor
                )
                Text(
                    text = vocab.pronunciation, // Đây là IPA
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryColor,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vocab.meaning, // Nghĩa tiếng Việt
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(
                onClick = onSpeak,
                modifier = Modifier.background(LightBlue, CircleShape)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor)
            }
        }
    }
}