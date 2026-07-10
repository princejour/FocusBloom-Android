package com.walhero.focusbloom.util

import com.walhero.focusbloom.data.FocusSession
import com.walhero.focusbloom.data.Habit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun todayIso(): String = LocalDate.now().toString()

fun Habit.isCompletedToday(today: LocalDate = LocalDate.now()): Boolean =
    today.toString() in completedDates

fun currentStreak(completedDates: Set<String>, today: LocalDate = LocalDate.now()): Int {
    if (completedDates.isEmpty()) return 0
    val parsedDates = completedDates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet()
    var cursor = if (today in parsedDates) today else today.minusDays(1)
    var streak = 0
    while (cursor in parsedDates) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}

fun bestStreak(completedDates: Set<String>): Int {
    val sorted = completedDates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
    if (sorted.isEmpty()) return 0
    var best = 1
    var current = 1
    for (index in 1 until sorted.size) {
        current = if (sorted[index - 1].plusDays(1) == sorted[index]) current + 1 else 1
        best = maxOf(best, current)
    }
    return best
}

fun lastSevenDays(today: LocalDate = LocalDate.now()): List<LocalDate> =
    (6 downTo 0).map { today.minusDays(it.toLong()) }

fun sessionsByDay(
    sessions: List<FocusSession>,
    days: List<LocalDate> = lastSevenDays(),
): List<Int> = days.map { day ->
    sessions.filter { it.date == day.toString() }.sumOf { it.durationMinutes }
}

fun startOfWeek(date: LocalDate = LocalDate.now()): LocalDate =
    date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())

fun dayLabel(date: LocalDate, locale: Locale = Locale.getDefault()): String =
    date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).take(2)

fun formatTimer(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    return "%02d:%02d".format(Locale.ROOT, safeSeconds / 60, safeSeconds % 60)
}

fun formattedToday(locale: Locale = Locale.getDefault()): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
