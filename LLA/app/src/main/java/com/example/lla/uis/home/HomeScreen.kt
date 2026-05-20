package com.example.lla.uis.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.*
import com.example.lla.uis.auth.AuthState
import com.example.lla.uis.auth.AuthViewModel
import com.example.lla.uis.topic.TopicViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    topicViewModel: TopicViewModel,
    onPracticeClick: () -> Unit = {},
    onTopicClick: () -> Unit = {}
) {
    val authState by authViewModel.authState.collectAsState()
    val reviewVocabs by topicViewModel.reviewVocabularies.collectAsState()
    
    val user = (authState as? AuthState.Success)?.user

    LaunchedEffect(user) {
        user?.let {
            topicViewModel.fetchReviewVocabularies(it.uid)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LLA Learning",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray)
                )
            }
        }

        // Welcome Section
        item {
            Column {
                // Sửa lại text hiển thị tên theo yêu cầu của bạn
                Text(
                    text = "Xin Chào ${user?.displayName ?: "Người dùng"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hôm nay chúng ta học gì nào?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Practice/Review Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Đến giờ ôn tập!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${reviewVocabs.size} từ vựng đã đến hạn ôn tập", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = {
                            if (reviewVocabs.isNotEmpty()) {
                                topicViewModel.prepareReviewMode()
                                onPracticeClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                        shape = RoundedCornerShape(12.dp),
                        enabled = reviewVocabs.isNotEmpty()
                    ) {
                        Text(
                            text = if (reviewVocabs.isEmpty()) "CHƯA CÓ TỪ CẦN ÔN" else "BẮT ĐẦU ÔN TẬP",
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Explore Topics Link
        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onTopicClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Khám phá các chủ đề", fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
