package com.example.lla.uis.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.model.Topic
import com.example.lla.ui.theme.*

@Composable
fun TopicScreen(
    viewModel: TopicViewModel,
    modifier: Modifier = Modifier,
    onTopicClick: (String) -> Unit = {}
) {
    val topics by viewModel.topics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredTopics = topics.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Khám phá chủ đề",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thanh tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Tìm kiếm chủ đề...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTopics) { topic ->
                    TopicItem(topic = topic, onClick = { onTopicClick(topic.id) })
                }
            }
        }
    }
}

@Composable
fun TopicItem(topic: Topic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Phần hiển thị Emoji bên trái
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = topic.emoji.ifEmpty { "📚" },
                    fontSize = 40.sp
                )
            }

            // Phần hiển thị thông tin bên phải
            Column(
                modifier = Modifier
                    .weight(1f) // Để Column chiếm hết phần còn lại
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2 // Cho phép hiện 2 dòng mô tả
                )
            }
        }
    }
}