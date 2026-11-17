package com.example.readspeedtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val totalPages: Int,
    val pagesRead: Int = 0,
    val content: String = "",
    val lastPageIndex: Int = 0
)