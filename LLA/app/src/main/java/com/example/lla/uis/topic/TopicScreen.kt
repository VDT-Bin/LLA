package com.example.lla.uis.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.*

@Composable
fun TopicScreen(
    modifier: Modifier = Modifier,
    onTopicClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "LinguistPlay",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = LightBlue,
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "12",
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search your next lesson...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryChip("Basic", isSelected = true)
            CategoryChip("Business", isSelected = false)
            CategoryChip("Travel", isSelected = false)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Topic Card (Animals)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTopicClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LightBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "50 words",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryColor
                        )
                    }
                    Text(text = "Animals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Master the wild world", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Topics
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TopicSmallCard("Food", "35 words", Modifier.weight(1f), onClick = onTopicClick)
            TopicSmallCard("Home", "42 words", Modifier.weight(1f), onClick = onTopicClick)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // New Topic Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTopicClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8EAF6),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "New Topic",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3F51B5)
                        )
                    }
                    Text(text = "Hobbies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Daily leisure & sports", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) PrimaryColor else Color(0xFFF5F5F5),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TopicSmallCard(title: String, count: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(text = count, style = MaterialTheme.typography.labelSmall, color = PrimaryColor)
        }
    }
}
