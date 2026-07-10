package com.walhero.focusbloom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.data.AppState
import com.walhero.focusbloom.data.FocusTimerState
import com.walhero.focusbloom.ui.components.MetricCard
import com.walhero.focusbloom.ui.components.SectionHeader
import com.walhero.focusbloom.ui.theme.Lavender
import com.walhero.focusbloom.ui.theme.Mint
import com.walhero.focusbloom.ui.theme.Rose
import com.walhero.focusbloom.ui.theme.Sunrise
import com.walhero.focusbloom.util.formatTimer
import com.walhero.focusbloom.util.formattedToday
import com.walhero.focusbloom.util.isCompletedToday
import com.walhero.focusbloom.util.todayIso
import androidx.core.os.ConfigurationCompat
import java.time.LocalDate
import java.util.Locale

@Composable
fun FocusScreen(
    appState: AppState,
    timerState: FocusTimerState,
    onDurationSelected: (Int) -> Unit,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
    onOpenCalm: () -> Unit,
) {
    val context = LocalContext.current
    val prompts = context.resources.getStringArray(R.array.daily_prompts)
    val prompt = prompts[(LocalDate.now().dayOfYear - 1) % prompts.size]
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0] ?: Locale.ROOT
    val today = todayIso()
    val todayMinutes = appState.sessions.filter { it.date == today }.sumOf { it.durationMinutes }
    val habitsDone = appState.habits.count { it.isCompletedToday() }
    val tasksDone = appState.tasks.count { it.isCompleted }

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column {
                Text(
                    text = formattedToday(locale),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                SectionHeader(
                    title = stringResource(R.string.ready_to_bloom),
                    subtitle = stringResource(R.string.today_message),
                )
            }
        }

        item {
            FocusTimerCard(
                timerState = timerState,
                onDurationSelected = onDurationSelected,
                onStartPause = onStartPause,
                onReset = onReset,
            )
        }

        item {
            Column {
                Text(stringResource(R.string.today_progress), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.focus_minutes),
                        value = todayMinutes.toString(),
                        icon = Icons.Default.Timer,
                        accent = Mint,
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.habits_done),
                        value = "$habitsDone/${appState.habits.size}",
                        icon = Icons.Default.Favorite,
                        accent = Rose,
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.tasks_done),
                        value = "$tasksDone/${appState.tasks.size}",
                        icon = Icons.Default.CheckCircle,
                        accent = Lavender,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            Card(
                onClick = onOpenCalm,
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.SelfImprovement, null, tint = MaterialTheme.colorScheme.onSecondary)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.quick_calm), style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.quick_calm_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Text(
                        stringResource(R.string.open_breathing),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(Icons.Default.FormatQuote, null, tint = Sunrise)
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusTimerCard(
    timerState: FocusTimerState,
    onDurationSelected: (Int) -> Unit,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
) {
    val totalSeconds = timerState.selectedMinutes * 60
    val remainingFraction = (timerState.remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val timerDescription = stringResource(R.string.timer_description)
    val startLabel = when {
        timerState.isRunning -> R.string.pause
        timerState.remainingSeconds < totalSeconds -> R.string.resume
        else -> R.string.start
    }

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.focus_session), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(218.dp)
                    .semantics { contentDescription = timerDescription },
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = { remainingFraction },
                    modifier = Modifier.size(218.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTimer(timerState.remainingSeconds),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.minutes_short, timerState.selectedMinutes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.select_duration), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(25, 45, 60).forEach { minutes ->
                    FilterChip(
                        selected = timerState.selectedMinutes == minutes,
                        onClick = { onDurationSelected(minutes) },
                        label = { Text(stringResource(R.string.minutes_short, minutes)) },
                        enabled = !timerState.isRunning,
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onStartPause,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                ) {
                    Icon(
                        if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(startLabel))
                }
                FilledTonalButton(
                    onClick = onReset,
                    modifier = Modifier.height(54.dp),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reset))
                }
            }
        }
    }
}
