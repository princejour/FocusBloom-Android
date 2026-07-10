package com.walhero.focusbloom.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AppRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadState(): AppState = AppState(
        tasks = loadTasks(),
        habits = loadHabits(),
        sessions = loadSessions(),
        themeMode = runCatching {
            ThemeMode.valueOf(preferences.getString(KEY_THEME, ThemeMode.SYSTEM.name).orEmpty())
        }.getOrDefault(ThemeMode.SYSTEM),
        onboardingCompleted = preferences.getBoolean(KEY_ONBOARDING, false),
    )

    fun saveState(state: AppState) {
        preferences.edit()
            .putString(KEY_TASKS, tasksToJson(state.tasks).toString())
            .putString(KEY_HABITS, habitsToJson(state.habits).toString())
            .putString(KEY_SESSIONS, sessionsToJson(state.sessions.takeLast(MAX_SESSIONS)).toString())
            .putString(KEY_THEME, state.themeMode.name)
            .putBoolean(KEY_ONBOARDING, state.onboardingCompleted)
            .apply()
    }

    private fun loadTasks(): List<FocusTask> = parseArray(KEY_TASKS) { item ->
        FocusTask(
            id = item.getString("id"),
            title = item.getString("title"),
            isCompleted = item.optBoolean("completed", false),
            createdAt = item.optLong("createdAt", System.currentTimeMillis()),
        )
    }

    private fun loadHabits(): List<Habit> = parseArray(KEY_HABITS) { item ->
        val datesArray = item.optJSONArray("completedDates") ?: JSONArray()
        val dates = buildSet {
            for (index in 0 until datesArray.length()) add(datesArray.getString(index))
        }
        Habit(
            id = item.getString("id"),
            title = item.getString("title"),
            emoji = item.optString("emoji", "🌱"),
            completedDates = dates,
            createdAt = item.optLong("createdAt", System.currentTimeMillis()),
        )
    }

    private fun loadSessions(): List<FocusSession> = parseArray(KEY_SESSIONS) { item ->
        FocusSession(
            id = item.getString("id"),
            date = item.getString("date"),
            durationMinutes = item.getInt("minutes"),
            completedAt = item.optLong("completedAt", System.currentTimeMillis()),
        )
    }

    private inline fun <T> parseArray(key: String, crossinline mapper: (JSONObject) -> T): List<T> =
        runCatching {
            val array = JSONArray(preferences.getString(key, "[]"))
            buildList {
                for (index in 0 until array.length()) add(mapper(array.getJSONObject(index)))
            }
        }.getOrDefault(emptyList())

    private fun tasksToJson(tasks: List<FocusTask>) = JSONArray().apply {
        tasks.forEach { task ->
            put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("completed", task.isCompleted)
                put("createdAt", task.createdAt)
            })
        }
    }

    private fun habitsToJson(habits: List<Habit>) = JSONArray().apply {
        habits.forEach { habit ->
            put(JSONObject().apply {
                put("id", habit.id)
                put("title", habit.title)
                put("emoji", habit.emoji)
                put("createdAt", habit.createdAt)
                put("completedDates", JSONArray(habit.completedDates.sorted()))
            })
        }
    }

    private fun sessionsToJson(sessions: List<FocusSession>) = JSONArray().apply {
        sessions.forEach { session ->
            put(JSONObject().apply {
                put("id", session.id)
                put("date", session.date)
                put("minutes", session.durationMinutes)
                put("completedAt", session.completedAt)
            })
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "focusbloom_state"
        const val KEY_TASKS = "tasks"
        const val KEY_HABITS = "habits"
        const val KEY_SESSIONS = "sessions"
        const val KEY_THEME = "theme"
        const val KEY_ONBOARDING = "onboarding"
        const val MAX_SESSIONS = 1000
    }
}
