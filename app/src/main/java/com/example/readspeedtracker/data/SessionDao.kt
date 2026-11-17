package com.example.readspeedtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ReadingSession>>

    @Query("SELECT * FROM sessions WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getSessionsForBook(bookId: Long): Flow<List<ReadingSession>>

    @Insert
    suspend fun insertSession(session: ReadingSession)

    // Удалить все сессии (для сброса статистики)
    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}