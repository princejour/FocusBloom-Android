package com.walhero.focusbloom.data

data class FocusTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

data class Habit(
    val id: String,
    val title: String,
    val emoji: String,
    val completedDates: Set<String> = emptySet(),
    val createdAt: Long = System.currentTimeMillis(),
)

data class FocusSession(
    val id: String,
    val date: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

data class AppState(
    val tasks: List<FocusTask> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val sessions: List<FocusSession> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val onboardingCompleted: Boolean = false,
)

data class FocusTimerState(
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
)
