package com.walhero.focusbloom.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhero.focusbloom.data.AppRepository
import com.walhero.focusbloom.data.FocusSession
import com.walhero.focusbloom.data.FocusTask
import com.walhero.focusbloom.data.FocusTimerState
import com.walhero.focusbloom.data.Habit
import com.walhero.focusbloom.data.ThemeMode
import com.walhero.focusbloom.util.todayIso
import java.util.UUID
import kotlin.math.ceil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusBloomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _appState = MutableStateFlow(repository.loadState())
    val appState = _appState.asStateFlow()

    private val _timerState = MutableStateFlow(FocusTimerState())
    val timerState = _timerState.asStateFlow()

    private val _sessionCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionCompleted = _sessionCompleted.asSharedFlow()

    private var timerJob: Job? = null
    private var targetEndMillis: Long = 0L

    fun completeOnboarding() = updateState { it.copy(onboardingCompleted = true) }

    fun setThemeMode(mode: ThemeMode) = updateState { it.copy(themeMode = mode) }

    fun addTask(title: String) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        updateState { state ->
            state.copy(tasks = listOf(FocusTask(UUID.randomUUID().toString(), cleanTitle)) + state.tasks)
        }
    }

    fun toggleTask(taskId: String) = updateState { state ->
        state.copy(tasks = state.tasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        })
    }

    fun deleteTask(taskId: String) = updateState { state ->
        state.copy(tasks = state.tasks.filterNot { it.id == taskId })
    }

    fun clearCompletedTasks() = updateState { state ->
        state.copy(tasks = state.tasks.filterNot { it.isCompleted })
    }

    fun addHabit(title: String, emoji: String) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        updateState { state ->
            state.copy(habits = state.habits + Habit(
                id = UUID.randomUUID().toString(),
                title = cleanTitle,
                emoji = emoji,
            ))
        }
    }

    fun toggleHabit(habitId: String) = updateState { state ->
        val today = todayIso()
        state.copy(habits = state.habits.map { habit ->
            if (habit.id != habitId) return@map habit
            val dates = habit.completedDates.toMutableSet()
            if (!dates.add(today)) dates.remove(today)
            habit.copy(completedDates = dates)
        })
    }

    fun deleteHabit(habitId: String) = updateState { state ->
        state.copy(habits = state.habits.filterNot { it.id == habitId })
    }

    fun selectDuration(minutes: Int) {
        if (_timerState.value.isRunning) return
        _timerState.value = FocusTimerState(
            selectedMinutes = minutes,
            remainingSeconds = minutes * 60,
        )
    }

    fun startOrPauseTimer() {
        val current = _timerState.value
        if (current.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.update { current ->
            FocusTimerState(
                selectedMinutes = current.selectedMinutes,
                remainingSeconds = current.selectedMinutes * 60,
            )
        }
    }

    private fun startTimer() {
        val current = _timerState.value
        if (current.remainingSeconds <= 0) resetTimer()
        targetEndMillis = System.currentTimeMillis() + _timerState.value.remainingSeconds * 1000L
        _timerState.update { it.copy(isRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                val remaining = ceil((targetEndMillis - System.currentTimeMillis()) / 1000.0)
                    .toInt()
                    .coerceAtLeast(0)
                _timerState.update { it.copy(remainingSeconds = remaining) }
                if (remaining == 0) {
                    completeFocusSession()
                    break
                }
                delay(250)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        val remaining = ceil((targetEndMillis - System.currentTimeMillis()) / 1000.0)
            .toInt()
            .coerceAtLeast(0)
        _timerState.update { it.copy(remainingSeconds = remaining, isRunning = false) }
    }

    private fun completeFocusSession() {
        val minutes = _timerState.value.selectedMinutes
        updateState { state ->
            state.copy(sessions = state.sessions + FocusSession(
                id = UUID.randomUUID().toString(),
                date = todayIso(),
                durationMinutes = minutes,
            ))
        }
        _timerState.value = FocusTimerState(selectedMinutes = minutes, remainingSeconds = minutes * 60)
        _sessionCompleted.tryEmit(Unit)
    }

    private inline fun updateState(transform: (com.walhero.focusbloom.data.AppState) -> com.walhero.focusbloom.data.AppState) {
        _appState.update(transform)
        repository.saveState(_appState.value)
    }
}
