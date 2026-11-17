package com.example.readspeedtracker.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.readspeedtracker.utils.PageSplitter
import com.example.readspeedtracker.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var pages by remember { mutableStateOf("") }
    var fileContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            errorMessage = null
            fileContent = ""

            coroutineScope.launch {
                try {
                    val fileName = getFileName(context, it) ?: ""
                    val mimeType = context.contentResolver.getType(it) ?: ""

                    val result = withContext(Dispatchers.IO) {
                        when {
                            fileName.endsWith(".txt", true) || mimeType.startsWith("text/") -> {
                                context.contentResolver.openInputStream(it)?.bufferedReader()?.use { r -> r.readText() } ?: ""
                            }
                            fileName.endsWith(".epub", true) || mimeType == "application/epub+zip" -> {
                                extractTextFromEpub(context, it)
                            }
                            fileName.endsWith(".pdf", true) || mimeType == "application/pdf" -> {
                                extractTextFromPdf(context, it)
                            }
                            else -> {
                                throw Exception("Формат не поддерживается. Используйте .txt, .epub или .pdf")
                            }
                        }
                    }

                    fileContent = result
                    if (fileContent.isEmpty()) {
                        errorMessage = "Не удалось извлечь текст из файла (возможно, PDF состоит из картинок)."
                    }
                } catch (e: Exception) {
                    errorMessage = "Ошибка: ${e.message}"
                    fileContent = ""
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая книга") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Назад") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = author, onValueChange = { author = it },
                label = { Text("Автор") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (fileContent.isEmpty()) "Загрузить файл (.txt, .epub, .pdf)" else "Файл загружен!")
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (fileContent.isNotEmpty()) {
                // Точный подсчёт слов через PageSplitter (учитывает пустые строки корректно)
                val wordCount = PageSplitter.countWords(fileContent)
                val pageCount = PageSplitter.splitIntoPages(fileContent).size
                Text(
                    "Слов: $wordCount | Страниц: $pageCount",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && fileContent.isNotBlank()) {
                        val totalPages = PageSplitter.splitIntoPages(fileContent).size
                        viewModel.addBook(title, author, totalPages, fileContent)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && fileContent.isNotBlank() && !isLoading
            ) {
                Text("Сохранить")
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        }
    }
    if (result == null) {
        val path = uri.path ?: return null
        val cut = path.lastIndexOf('/')
        result = if (cut != -1) path.substring(cut + 1) else path
    }
    return result
}

// EPUB: соблюдает порядок глав из content.opf, корректно закрывает zip-entry
fun extractTextFromEpub(context: Context, uri: Uri): String {
    val stringBuilder = StringBuilder()
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val zipBytes = inputStream.readBytes()
            val zipStream = ZipInputStream(zipBytes.inputStream())
            val entries = mutableMapOf<String, ByteArray>()
            var entry = zipStream.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    entries[entry.name] = zipStream.readBytes()
                }
                // Явно закрываем текущий entry перед переходом к следующему
                zipStream.closeEntry()
                entry = zipStream.nextEntry
            }
            zipStream.close()

            val opfName = entries.keys.find { it.endsWith(".opf") }
            if (opfName != null) {
                val opfData = entries[opfName]
                if (opfData != null) {
                    val factory = DocumentBuilderFactory.newInstance()
                    val builder = factory.newDocumentBuilder()
                    val doc = builder.parse(opfData.inputStream())
                    val manifest = doc.getElementsByTagName("item")
                    val spine = doc.getElementsByTagName("itemref")

                    val idToHref = mutableMapOf<String, String>()
                    for (i in 0 until manifest.length) {
                        val node = manifest.item(i)
                        val id = node.attributes.getNamedItem("id")?.nodeValue ?: continue
                        val href = node.attributes.getNamedItem("href")?.nodeValue ?: continue
                        idToHref[id] = href
                    }

                    for (i in 0 until spine.length) {
                        val node = spine.item(i)
                        val idref = node.attributes.getNamedItem("idref")?.nodeValue ?: continue
                        val href = idToHref[idref] ?: continue
                        val entryName = entries.keys.find { it.endsWith(href) }
                        if (entryName != null) {
                            val content = entries[entryName]?.toString(Charsets.UTF_8) ?: ""
                            val jsoupDoc = Jsoup.parse(content)
                            stringBuilder.append(jsoupDoc.text()).append("\n\n")
                        }
                    }
                }
            } else {
                for ((name, bytes) in entries) {
                    if (name.endsWith(".html") || name.endsWith(".xhtml")) {
                        val content = bytes.toString(Charsets.UTF_8)
                        val doc = Jsoup.parse(content)
                        stringBuilder.append(doc.text()).append("\n\n")
                    }
                }
            }
        }
    } catch (e: Exception) {
        throw Exception("Ошибка чтения EPUB: ${e.message}")
    }
    return stringBuilder.toString()
}

// Постраничное чтение PDF с защитой от битых страниц
fun extractTextFromPdf(context: Context, uri: Uri): String {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            com.tom_roush.pdfbox.pdmodel.PDDocument.load(inputStream).use { doc ->
                val stripper = com.tom_roush.pdfbox.text.PDFTextStripper()
                val stringBuilder = StringBuilder()
                val totalPages = doc.numberOfPages

                for (page in 1..totalPages) {
                    try {
                        stripper.startPage = page
                        stripper.endPage = page
                        val pageText = stripper.getText(doc)
                        if (pageText.isNotBlank()) {
                            stringBuilder.append(pageText.trim()).append("\n\n")
                        }
                    } catch (_: Exception) {
                        // Пропускаем битую страницу, продолжаем со следующей
                    }
                }
                stringBuilder.toString()
            }
        } ?: ""
    } catch (e: Exception) {
        throw Exception("Ошибка чтения PDF: ${e.message}")
    }
}