package com.example.readspeedtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Book::class, ReadingSession::class, Note::class],
    version = 4, // Версия увеличена
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun sessionDao(): SessionDao
    abstract fun noteDao(): NoteDao
}