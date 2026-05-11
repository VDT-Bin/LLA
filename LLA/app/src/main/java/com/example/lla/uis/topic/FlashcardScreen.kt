package com.example.lla.uis.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.*

@Composable
fun FlashcardScreen(onClose: () -> Unit) {
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
            LinearProgressIndicator(
                progress = { 0.4f },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = PrimaryColor,
                trackColor = Color(0xFFD1D9E6)
            )
            Spacer(modifier = Modifier.width(16.dp))
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
                    Text(text = "12", fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightBlue
                ) {
                    Text(
                        text = "New Word",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ebullient",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "/ɪˈbʊl.i.ənt/",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightBlue)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryColor)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Cheerful and full of energy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"He was ebullient after hearing the news of his promotion.\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Tap the card to flip", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Rating Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RatingButton("Quên", Color(0xFFFFEBEE), Color(0xFFE57373), Modifier.weight(1f))
            RatingButton("Khó", Color(0xFFFFF3E0), Color(0xFFFFB74D), Modifier.weight(1f))
            RatingButton("Tốt", Color(0xFFE0F2F1), Color(0xFF4DB6AC), Modifier.weight(1f))
            RatingButton("Dễ", Color(0xFFE3F2FD), Color(0xFF64B5F6), Modifier.weight(1f))
        }
    }
}

@Composable
fun RatingButton(label: String, bgColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Simplified emoji or icon
                Text(text = when(label) {
                    "Quên" -> "😫"
                    "Khó" -> "😟"
                    "Tốt" -> "😊"
                    else -> "😃"
                }, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
