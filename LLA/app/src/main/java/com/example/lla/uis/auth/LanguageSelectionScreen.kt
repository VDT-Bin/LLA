package com.example.lla.uis.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lla.ui.theme.*

data class Language(val name: String, val localName: String, val flag: String)

@Composable
fun LanguageSelectionScreen(onContinue: () -> Unit) {
    val languages = listOf(
        Language("English", "Tiếng Anh", "🇬🇧"),
        Language("Japanese", "Tiếng Nhật", "🇯🇵"),
        Language("Korean", "Tiếng Hàn", "🇰🇷"),
        Language("French", "Tiếng Pháp", "🇫🇷"),
        Language("German", "Tiếng Đức", "🇩🇪"),
        Language("Spanish", "Tiếng Tây Ban Nha", "🇪🇸")
    )
    var selectedLanguage by remember { mutableStateOf<Language?>(languages[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF5F9FF),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔤", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hành trình mới của bạn",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF3F51B5)
                )
            }
        }

        Text(
            text = "Bạn muốn học ngôn ngữ nào?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Chọn một ngôn ngữ để bắt đầu cuộc phiêu lưu ngay hôm nay.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(languages) { lang ->
                LanguageCard(
                    language = lang,
                    isSelected = lang == selectedLanguage,
                    onClick = { selectedLanguage = lang }
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { 0.2f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF7E57C2),
                trackColor = Color(0xFFF0E6FF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TIẾP TỤC", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun LanguageCard(language: Language, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF7E57C2) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = language.flag, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = language.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = language.localName, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}
