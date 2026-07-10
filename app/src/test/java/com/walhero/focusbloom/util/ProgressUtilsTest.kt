package com.walhero.focusbloom.util

import com.walhero.focusbloom.data.FocusSession
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressUtilsTest {
    @Test
    fun `current streak includes today and consecutive previous days`() {
        val today = LocalDate.of(2026, 7, 10)
        val completed = setOf("2026-07-10", "2026-07-09", "2026-07-08", "2026-07-06")

        assertEquals(3, currentStreak(completed, today))
    }

    @Test
    fun `current streak accepts yesterday when today is not complete`() {
        val today = LocalDate.of(2026, 7, 10)
        val completed = setOf("2026-07-09", "2026-07-08")

        assertEquals(2, currentStreak(completed, today))
    }

    @Test
    fun `best streak finds longest historical run`() {
        val completed = setOf(
            "2026-07-01",
            "2026-07-02",
            "2026-07-04",
            "2026-07-05",
            "2026-07-06",
        )

        assertEquals(3, bestStreak(completed))
    }

    @Test
    fun `sessions are totalled by requested day`() {
        val days = listOf(LocalDate.of(2026, 7, 9), LocalDate.of(2026, 7, 10))
        val sessions = listOf(
            FocusSession("1", "2026-07-09", 25),
            FocusSession("2", "2026-07-10", 45),
            FocusSession("3", "2026-07-10", 25),
        )

        assertEquals(listOf(25, 70), sessionsByDay(sessions, days))
    }

    @Test
    fun `timer formatting is always two digits`() {
        assertEquals("25:00", formatTimer(1500))
        assertEquals("00:09", formatTimer(9))
        assertEquals("00:00", formatTimer(-1))
    }
}
