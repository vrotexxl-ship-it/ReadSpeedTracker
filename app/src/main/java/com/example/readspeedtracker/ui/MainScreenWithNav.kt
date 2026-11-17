package com.example.readspeedtracker.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.readspeedtracker.ui.theme.AppThemeColor
import com.example.readspeedtracker.viewmodel.MainViewModel

@Composable
fun MainScreenWithNav(
    viewModel: MainViewModel,
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    themeColor: AppThemeColor,
    onThemeColorChange: (AppThemeColor) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Скрываем нижнюю навигацию на экранах сессии и добавления книги,
    // чтобы не дублировать bottomBar из SessionScreen.
    val showBottomBar = currentRoute in listOf("home", "achievements", "notes")

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (currentRoute == "home") MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Book, contentDescription = "Библиотека")
                            }
                        },
                        label = { Text("Библиотека") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "achievements",
                        onClick = {
                            navController.navigate("achievements") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (currentRoute == "achievements") MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = "Достижения")
                            }
                        },
                        label = { Text("Достижения") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "notes",
                        onClick = {
                            navController.navigate("notes") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (currentRoute == "notes") MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Заметки")
                            }
                        },
                        label = { Text("Заметки") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onAddBookClick = { navController.navigate("add") },
                    onBookClick = { book ->
                        navController.navigate("session/${book.id}")
                    },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    themeColor = themeColor,
                    onThemeColorChange = onThemeColorChange
                )
            }
            composable("achievements") {
                AchievementsScreen() // Убран параметр viewModel
            }
            composable("notes") {
                NotesScreen(viewModel = viewModel)
            }
            composable("add") {
                AddBookScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("session/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull() ?: 0L
                val books by viewModel.books.collectAsState()
                val book = books.find { it.id == bookId }
                if (book != null) {
                    SessionScreen(
                        book = book,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}