package com.example.readspeedtracker.utils

import android.content.Context
import com.example.readspeedtracker.data.Book
import com.example.readspeedtracker.data.ReadingSession

object AchievementManager {
    private const val PREFS_NAME = "achievements_prefs"
    private const val KEY_UNLOCKED = "unlocked_achievements"

    fun getUnlocked(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_UNLOCKED, emptySet()) ?: emptySet()
    }

    fun updateAchievements(context: Context, books: List<Book>, sessions: List<ReadingSession>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentlyUnlocked = getUnlocked(context).toMutableSet()
        var changed = false

        val totalBooks = books.size
        val completedBooks = books.count { it.pagesRead >= it.totalPages && it.totalPages > 0 }
        val totalSessions = sessions.size
        val maxWpm = sessions.maxOfOrNull { it.wpm } ?: 0.0
        val totalMinutes = sessions.sumOf { it.durationSeconds } / 60

        val checks = mapOf(
            "first_book" to (totalBooks >= 1),
            "first_session" to (totalSessions >= 1),
            "speed_200" to (maxWpm >= 200),
            "speed_300" to (maxWpm >= 300),
            "books_5" to (totalBooks >= 5),
            "completed_1" to (completedBooks >= 1),
            "time_60" to (totalMinutes >= 60)
        )

        for ((id, condition) in checks) {
            if (condition && !currentlyUnlocked.contains(id)) {
                currentlyUnlocked.add(id)
                changed = true
            }
        }

        if (changed) {
            prefs.edit().putStringSet(KEY_UNLOCKED, currentlyUnlocked).apply()
        }
    }
}