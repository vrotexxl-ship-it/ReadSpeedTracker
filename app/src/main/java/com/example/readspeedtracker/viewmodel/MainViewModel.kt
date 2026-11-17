package com.example.readspeedtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.readspeedtracker.data.*
import com.example.readspeedtracker.utils.AchievementManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = getDatabase(application)

    private val bookDao = db.bookDao()
    private val sessionDao = db.sessionDao()
    private val noteDao = db.noteDao()

    val books: StateFlow<List<Book>> = bookDao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<ReadingSession>> = sessionDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Автоматически проверяем достижения при изменении списков
        viewModelScope.launch {
            combine(books, sessions) { b, s -> b to s }.collect { (b, s) ->
                AchievementManager.updateAchievements(getApplication(), b, s)
            }
        }
    }

    fun addBook(title: String, author: String, totalPages: Int, content: String = "") {
        viewModelScope.launch {
            bookDao.insertBook(Book(title = title, author = author, totalPages = totalPages, content = content))
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            bookDao.deleteBook(book)
        }
    }

    fun clearAllSessions() {
        viewModelScope.launch {
            sessionDao.deleteAllSessions()
        }
    }

    fun updatePagesRead(book: Book, pagesRead: Int) {
        viewModelScope.launch {
            bookDao.updateBook(book.copy(pagesRead = pagesRead))
        }
    }

    fun saveSession(book: Book, durationSeconds: Long, pagesRead: Int, wordsRead: Int) {
        viewModelScope.launch {
            val minutes = durationSeconds / 60.0
            val wpm = if (minutes > 0) wordsRead / minutes else 0.0

            sessionDao.insertSession(
                ReadingSession(
                    bookId = book.id,
                    timestamp = System.currentTimeMillis(),
                    durationSeconds = durationSeconds,
                    pagesRead = pagesRead,
                    wpm = wpm
                )
            )

            val updatedBook = book.copy(pagesRead = book.pagesRead + pagesRead)
            bookDao.updateBook(updatedBook)
        }
    }

    // --- Заметки ---
    fun addNote(bookId: Long, text: String) {
        viewModelScope.launch {
            noteDao.insertNote(Note(bookId = bookId, text = text))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(application: Application): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java,
                    "reading_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}