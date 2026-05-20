package com.example.lla.uis.profile

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lla.ui.theme.LightBlue
import com.example.lla.ui.theme.PrimaryColor
import com.example.lla.ui.theme.SecondaryColor
import com.example.lla.ui.theme.TextSecondary
import com.example.lla.uis.auth.AuthState
import com.example.lla.uis.auth.AuthViewModel
import com.example.lla.uis.topic.TopicViewModel
import java.util.*

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    topicViewModel: TopicViewModel,
    onSeeAllLearnedClick: () -> Unit,
    onReviewLearnedClick: () -> Unit // Thêm tham số này
) {
    val authState by authViewModel.authState.collectAsState()
    val learnedVocabs by topicViewModel.learnedVocabularies.collectAsState()
    val isLoading by topicViewModel.isLoading.collectAsState()
    
    var newPassword by remember { mutableStateOf("") }
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

    LaunchedEffect(authState) {
        val user = (authState as? AuthState.Success)?.user
        user?.let {
            topicViewModel.fetchLearnedVocabularies(it.uid)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FE))
            .padding(16.dp)
    ) {
        // ... (Phần tiêu đề và Đổi mật khẩu giữ nguyên)

        Spacer(modifier = Modifier.height(24.dp))

        // 2. THAY THẾ TOÀN BỘ PHẦN DANH SÁCH CŨ BẰNG Ô BẤM NÀY
        Text(
            text = "Tiến trình học tập",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSeeAllLearnedClick() }, // Ấn vào để mở trang danh sách
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Từ vựng đã học",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Bạn đã thuộc ${learnedVocabs.size} từ vựng",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút Đăng xuất xuống dưới cùng

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ĐĂNG XUẤT")
        }
    }
}

@Composable
fun LearnedVocabItem(vocab: com.example.lla.model.Vocabulary, onSpeak: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = vocab.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vocab.word, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = vocab.meaning, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onSpeak, modifier = Modifier.background(LightBlue, CircleShape)) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}
