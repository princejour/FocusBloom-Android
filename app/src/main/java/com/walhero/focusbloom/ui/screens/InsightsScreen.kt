package com.walhero.focusbloom.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.data.AppState
import com.walhero.focusbloom.ui.components.MetricCard
import com.walhero.focusbloom.ui.components.SectionHeader
import com.walhero.focusbloom.ui.theme.Lavender
import com.walhero.focusbloom.ui.theme.Mint
import com.walhero.focusbloom.ui.theme.Rose
import com.walhero.focusbloom.ui.theme.Sunrise
import com.walhero.focusbloom.util.bestStreak
import com.walhero.focusbloom.util.dayLabel
import com.walhero.focusbloom.util.lastSevenDays
import com.walhero.focusbloom.util.sessionsByDay
import java.util.Locale

@Composable
fun InsightsScreen(appState: AppState) {
    val days = lastSevenDays()
    val dailyMinutes = sessionsByDay(appState.sessions, days)
    val weekDates = days.map { it.toString() }.toSet()
    val weeklySessions = appState.sessions.filter { it.date in weekDates }
    val totalFocus = weeklySessions.sumOf { it.durationMinutes }
    val bestHabitStreak = appState.habits.maxOfOrNull { bestStreak(it.completedDates) } ?: 0
    val completionPercent = if (appState.tasks.isEmpty()) 0
    else appState.tasks.count { it.isCompleted } * 100 / appState.tasks.size

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            SectionHeader(
                title = stringResource(R.string.insights_title),
                subtitle = stringResource(R.string.insights_subtitle),
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.total_focus),
                        value = stringResource(R.string.minutes_short, totalFocus),
                        icon = Icons.Default.Timer,
                        accent = Mint,
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.sessions),
                        value = weeklySessions.size.toString(),
                        icon = Icons.Default.TrackChanges,
                        accent = Lavender,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.best_streak),
                        value = stringResource(R.string.days_count, bestHabitStreak),
                        icon = Icons.Default.LocalFireDepartment,
                        accent = Sunrise,
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.completion),
                        value = stringResource(R.string.progress_percent, completionPercent),
                        icon = Icons.Default.CheckCircle,
                        accent = Rose,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            WeeklyFocusCard(
                values = dailyMinutes,
                labels = days.map { dayLabel(it, Locale.getDefault()) },
            )
        }
    }
}

@Composable
private fun WeeklyFocusCard(values: List<Int>, labels: List<String>) {
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val chartDescription = stringResource(R.string.weekly_focus)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.weekly_focus),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(22.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .semantics { contentDescription = chartDescription },
            ) {
                drawBars(values, barColor, trackColor)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                labels.forEachIndexed { index, label ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = values.getOrElse(index) { 0 }.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBars(values: List<Int>, barColor: Color, trackColor: Color) {
    if (values.isEmpty()) return
    val maxValue = values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val slotWidth = size.width / values.size
    val barWidth = slotWidth * 0.48f
    val cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)

    values.forEachIndexed { index, value ->
        val x = index * slotWidth + (slotWidth - barWidth) / 2f
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(x, 0f),
            size = Size(barWidth, size.height),
            cornerRadius = cornerRadius,
        )
        val height = if (value == 0) 4.dp.toPx() else size.height * value / maxValue
        drawRoundRect(
            color = barColor,
            topLeft = Offset(x, size.height - height),
            size = Size(barWidth, height),
            cornerRadius = cornerRadius,
        )
    }
}
