package com.example.lla.uis.topic

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.*
import com.example.lla.uis.auth.AuthState
import com.example.lla.uis.auth.AuthViewModel
import com.example.lla.model.Vocabulary
import java.util.*

@Composable
fun FlashcardScreen(
    viewModel: TopicViewModel,
    authViewModel: AuthViewModel,
    topicId: String,
    onClose: () -> Unit
) {
    val vocabularies by viewModel.vocabularies.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val tts = remember {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
        textToSpeech
    }

    LaunchedEffect(topicId) {
        viewModel.fetchVocabulariesByLesson(topicId)
    }

    LaunchedEffect(currentIndex) {
        isFlipped = false
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            if (vocabularies.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / vocabularies.size },
                    modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = PrimaryColor,
                    trackColor = Color(0xFFD1D9E6)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (vocabularies.isNotEmpty()) {
            val currentVocab = vocabularies[currentIndex]
            val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 500),
                label = "flip"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (rotation <= 90f) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = currentVocab.emoji, fontSize = 80.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = currentVocab.word, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Text(text = currentVocab.pronunciation, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        Spacer(modifier = Modifier.height(24.dp))
                        IconButton(
                            onClick = { tts?.speak(currentVocab.word, TextToSpeech.QUEUE_FLUSH, null, null) },
                            modifier = Modifier.size(56.dp).background(LightBlue, CircleShape)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp).graphicsLayer { rotationY = 180f },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Ý nghĩa", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = currentVocab.meaning, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            androidx.compose.animation.AnimatedVisibility(
                visible = isFlipped, // Chỉ hiện khi đã lật thẻ
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

                RatingButton("Quên", Color(0xFFFFEBEE), Color(0xFFE57373), Modifier.weight(1f)) {
                    Log.d("TopicVM_Debug", "Đã bấm nút 'Quên'. Trạng thái user hiện tại: $user")
                    user?.let { viewModel.updateVocabProgress(it.uid, currentVocab, 1) }
                    if (currentIndex < vocabularies.size - 1) currentIndex++ else onClose()
                }
                RatingButton("Khó", Color(0xFFFFF3E0), Color(0xFFFFB74D), Modifier.weight(1f)) {
                    user?.let { viewModel.updateVocabProgress(it.uid, currentVocab, 2) }
                    if (currentIndex < vocabularies.size - 1) currentIndex++ else onClose()
                }
                RatingButton("Tốt", Color(0xFFE0F2F1), Color(0xFF4DB6AC), Modifier.weight(1f)) {
                    user?.let { viewModel.updateVocabProgress(it.uid, currentVocab, 3) }
                    if (currentIndex < vocabularies.size - 1) currentIndex++ else onClose()
                }
                RatingButton("Dễ", Color(0xFFE3F2FD), Color(0xFF64B5F6), Modifier.weight(1f)) {
                    user?.let { viewModel.updateVocabProgress(it.uid, currentVocab, 4) }
                    if (currentIndex < vocabularies.size - 1) currentIndex++ else onClose()
                }
                }
            }

            if(!isFlipped){
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun RatingButton(label: String, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = when(label) {
                "Quên" -> "😫"
                "Khó" -> "😟"
                "Tốt" -> "😊"
                else -> "😃"
            }, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
