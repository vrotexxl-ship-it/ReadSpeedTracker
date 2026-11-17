package com.example.readspeedtracker.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BackgroundIcons(modifier: Modifier = Modifier) {
    // Разнообразные иконки на тему чтения
    val icons = listOf(
        Icons.Default.MenuBook,
        Icons.Default.AutoStories,
        Icons.Default.Book,
        Icons.Default.Bookmark,
        Icons.Default.Create, // Перо
        Icons.Default.RemoveRedEye, // Очки
        Icons.Default.School,
        Icons.Default.Star
    )
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = 6
        val rows = 14
        // Явное обращение к this@BoxWithConstraints убирает предупреждение
        val cellWidth = this@BoxWithConstraints.maxWidth / columns
        val cellHeight = this@BoxWithConstraints.maxHeight / rows

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val index = (row * columns + col) % icons.size
                val icon = icons[index]
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.035f),
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = cellWidth * col, y = cellHeight * row)
                        .rotate(45f)
                )
            }
        }
    }
}