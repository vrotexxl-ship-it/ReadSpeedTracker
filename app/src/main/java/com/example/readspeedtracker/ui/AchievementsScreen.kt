package com.example.readspeedtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.readspeedtracker.utils.AchievementManager

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen() {
    val context = LocalContext.current
    val unlockedIds = remember { AchievementManager.getUnlocked(context) }

    val achievements = listOf(
        Achievement(
            id = "first_book",
            title = "Первая книга",
            description = "Добавьте свою первую книгу",
            icon = Icons.Default.MenuBook,
            isUnlocked = "first_book" in unlockedIds
        ),
        Achievement(
            id = "first_session",
            title = "Первый шаг",
            description = "Завершите первую сессию чтения",
            icon = Icons.Default.PlayArrow,
            isUnlocked = "first_session" in unlockedIds
        ),
        Achievement(
            id = "speed_200",
            title = "Уверенный темп",
            description = "Достигните скорости 200 WPM",
            icon = Icons.Default.Speed,
            isUnlocked = "speed_200" in unlockedIds
        ),
        Achievement(
            id = "speed_300",
            title = "Скорочтение",
            description = "Достигните скорости 300 WPM",
            icon = Icons.Default.FlashOn,
            isUnlocked = "speed_300" in unlockedIds
        ),
        Achievement(
            id = "books_5",
            title = "Книголюб",
            description = "Добавьте 5 книг в библиотеку",
            icon = Icons.Default.LibraryBooks,
            isUnlocked = "books_5" in unlockedIds
        ),
        Achievement(
            id = "completed_1",
            title = "Дочитать до конца",
            description = "Прочитайте первую книгу полностью",
            icon = Icons.Default.CheckCircle,
            isUnlocked = "completed_1" in unlockedIds
        ),
        Achievement(
            id = "time_60",
            title = "Час за чтением",
            description = "Проведите за чтением 60 минут",
            icon = Icons.Default.Timer,
            isUnlocked = "time_60" in unlockedIds
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundIcons(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Достижения", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievement ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (achievement.isUnlocked) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = achievement.icon,
                                    contentDescription = null,
                                    tint = if (achievement.isUnlocked) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    achievement.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    achievement.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (achievement.isUnlocked) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}