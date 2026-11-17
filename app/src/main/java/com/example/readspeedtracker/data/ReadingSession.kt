package com.example.readspeedtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val timestamp: Long,
    val durationSeconds: Long,
    val pagesRead: Int,
    val wpm: Double
)