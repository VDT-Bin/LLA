package com.example.lla.uis.profile

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
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
        Text(
            text = "Hồ sơ của tôi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Đổi mật khẩu
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Bảo mật tài khoản", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                )
                Button(
                    onClick = {
                        if (newPassword.length >= 6) {
                            authViewModel.changePassword(newPassword) { s, e ->
                                if (s) {
                                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()
                                    newPassword = ""
                                } else Toast.makeText(context, "Lỗi: $e", Toast.LENGTH_SHORT).show()
                            }
                        } else Toast.makeText(context, "Tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                ) { Text("CẬP NHẬT MẬT KHẨU") }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề & Nút Ôn tập từ đã học
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đã học (${learnedVocabs.size} từ)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (learnedVocabs.isNotEmpty()) {
                TextButton(onClick = onReviewLearnedClick) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ÔN TẬP LẠI", color = PrimaryColor)
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(learnedVocabs) { vocab ->
                    LearnedVocabItem(vocab = vocab, onSpeak = { tts?.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
