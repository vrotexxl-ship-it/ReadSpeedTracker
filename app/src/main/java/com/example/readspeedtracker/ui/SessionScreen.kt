package com.example.readspeedtracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readspeedtracker.data.Book
import com.example.readspeedtracker.utils.PageSplitter
import com.example.readspeedtracker.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(book: Book, viewModel: MainViewModel, onBack: () -> Unit) {
    val pages = remember(book.content) {
        PageSplitter.splitIntoPages(book.content)
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableLongStateOf(0L) }
    var pagesInput by remember { mutableStateOf("") }
    // Стартовый индекс текущей сессии (0-based). Устанавливается при старте таймера.
    var startPageIndex by remember { mutableIntStateOf(0) }
    // Был ли таймер запущен хотя бы раз в этой сессии (для корректного расчёта startPageIndex)
    var hasStarted by remember { mutableStateOf(false) }

    // Диалог выбора страницы
    var showPageDialog by remember { mutableStateOf(false) }
    var pageInput by remember { mutableStateOf("") }

    // Диалог добавления заметки
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    val showResumeButton = book.pagesRead > 0 &&
            book.pagesRead < pages.size &&
            currentPageIndex + 1 < book.pagesRead

    /**
     * Сохраняет прогресс как количество прочитанных страниц = max(текущее, индекс+1).
     * Индекс 0-based → количество = index + 1.
     */
    fun saveProgressIfNeeded() {
        val newPagesRead = maxOf(book.pagesRead, currentPageIndex + 1)
        if (newPagesRead != book.pagesRead) {
            viewModel.updatePagesRead(book, newPagesRead)
        }
    }

    BackHandler {
        isRunning = false
        saveProgressIfNeeded()
        onBack()
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            seconds += 1L
        }
    }

    // Диалог выбора страницы
    if (showPageDialog) {
        AlertDialog(
            onDismissRequest = {
                showPageDialog = false
                pageInput = ""
            },
            title = { Text("Перейти к странице") },
            text = {
                Column {
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { pageInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Номер страницы (1..${pages.size})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val page = pageInput.toIntOrNull()
                    if (page != null && page in 1..pages.size) {
                        currentPageIndex = page - 1
                    }
                    showPageDialog = false
                    pageInput = ""
                }) {
                    Text("Перейти")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPageDialog = false
                    pageInput = ""
                }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог добавления заметки
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Добавить заметку") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Текст заметки") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (noteText.isNotBlank()) {
                        viewModel.addNote(book.id, noteText)
                        noteText = ""
                        showNoteDialog = false
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundIcons(modifier = Modifier.fillMaxSize())

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(book.title, style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        TextButton(onClick = {
                            isRunning = false
                            saveProgressIfNeeded()
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNoteDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Добавить заметку")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${seconds / 60}:${String.format("%02d", seconds % 60)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row {
                                Button(
                                    onClick = {
                                        if (!isRunning) {
                                            // Если таймер стартует впервые (или после сброса) —
                                            // фиксируем стартовую страницу.
                                            if (seconds == 0L) {
                                                startPageIndex = currentPageIndex
                                                hasStarted = true
                                            }
                                            isRunning = true
                                        } else {
                                            isRunning = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRunning) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isRunning) "Пауза" else if (seconds == 0L) "Старт" else "Далее")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        isRunning = false
                                        seconds = 0L
                                        startPageIndex = currentPageIndex
                                        hasStarted = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Сброс", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        if (book.content.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = pagesInput,
                                onValueChange = { pagesInput = it },
                                label = { Text("Сколько страниц прочитали?") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        if (seconds > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    // Если пользователь не нажимал «Старт», считаем стартом текущую страницу
                                    val effectiveStart = if (hasStarted) startPageIndex else currentPageIndex

                                    val pagesReadThisSession = if (book.content.isNotEmpty()) {
                                        (kotlin.math.abs(currentPageIndex - effectiveStart) + 1)
                                            .coerceAtLeast(0)
                                    } else {
                                        pagesInput.toIntOrNull() ?: 0
                                    }

                                    val wordsRead = if (book.content.isNotEmpty()) {
                                        val from = minOf(effectiveStart, currentPageIndex)
                                        val to = maxOf(effectiveStart, currentPageIndex) + 1
                                        // Защита от выхода за границы
                                        val safeFrom = from.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                                        val safeTo = to.coerceIn(safeFrom + 1, pages.size)
                                        if (safeFrom < safeTo) {
                                            pages.subList(safeFrom, safeTo)
                                                .sumOf { PageSplitter.countWords(it) }
                                        } else 0
                                    } else {
                                        pagesReadThisSession * 250
                                    }

                                    if (pagesReadThisSession > 0) {
                                        viewModel.saveSession(book, seconds, pagesReadThisSession, wordsRead)
                                        saveProgressIfNeeded()
                                        onBack()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isRunning,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Завершить и сохранить")
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Нажмите «Старт», чтобы начать сессию чтения",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            if (book.content.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (showResumeButton) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Вы прочитали ${book.pagesRead} страниц",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val targetIndex = book.pagesRead.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                                        currentPageIndex = targetIndex
                                        startPageIndex = targetIndex
                                        hasStarted = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Продолжить чтение")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    AnimatedContent(
                        targetState = currentPageIndex,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "page"
                    ) { pageIndex ->
                        Text(
                            text = pages.getOrNull(pageIndex) ?: "Конец текста",
                            fontSize = 18.sp,
                            lineHeight = 30.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                            enabled = currentPageIndex > 0,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", modifier = Modifier.size(18.dp))
                        }

                        FilledTonalButton(
                            onClick = {
                                pageInput = (currentPageIndex + 1).toString()
                                showPageDialog = true
                            },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${currentPageIndex + 1} / ${pages.size}")
                        }

                        FilledTonalIconButton(
                            onClick = { if (currentPageIndex < pages.size - 1) currentPageIndex++ },
                            enabled = currentPageIndex < pages.size - 1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Вперёд", modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Читайте бумажную книгу и засекайте время!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}