package com.example.readspeedtracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.readspeedtracker.ui.MainScreenWithNav
import com.example.readspeedtracker.ui.theme.AppThemeColor
import com.example.readspeedtracker.ui.theme.ReadSpeedTrackerTheme
import com.example.readspeedtracker.viewmodel.MainViewModel
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PDFBoxResourceLoader.init(applicationContext)

        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

            var isDarkTheme by remember { mutableStateOf(prefs.getBoolean("dark_theme", false)) }
            var themeColor by remember {
                // Безопасное чтение enum: при повреждённом значении — GRAPHITE
                mutableStateOf(
                    runCatching {
                        AppThemeColor.valueOf(
                            prefs.getString("theme_color", AppThemeColor.GRAPHITE.name)
                                ?: AppThemeColor.GRAPHITE.name
                        )
                    }.getOrDefault(AppThemeColor.GRAPHITE)
                )
            }

            ReadSpeedTrackerTheme(darkTheme = isDarkTheme, themeColor = themeColor) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    MainScreenWithNav(
                        viewModel = viewModel,
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = {
                            isDarkTheme = !isDarkTheme
                            prefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
                        },
                        themeColor = themeColor,
                        onThemeColorChange = {
                            themeColor = it
                            prefs.edit().putString("theme_color", it.name).apply()
                        }
                    )
                }
            }
        }
    }
}