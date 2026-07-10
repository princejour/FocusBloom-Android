package com.walhero.focusbloom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.data.Habit
import com.walhero.focusbloom.ui.components.EmptyState
import com.walhero.focusbloom.ui.components.SectionHeader
import com.walhero.focusbloom.ui.theme.Mint
import com.walhero.focusbloom.ui.theme.Sunrise
import com.walhero.focusbloom.util.currentStreak
import com.walhero.focusbloom.util.isCompletedToday

@Composable
fun HabitsScreen(
    habits: List<Habit>,
    onAddHabit: (String, String) -> Unit,
    onToggleHabit: (String) -> Unit,
    onDeleteHabit: (String) -> Unit,
) {
    var addDialogVisible by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionHeader(
                title = stringResource(R.string.habits_title),
                subtitle = stringResource(R.string.habits_subtitle),
                action = {
                    FilledTonalButton(onClick = { addDialogVisible = true }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.size(6.dp))
                        Text(stringResource(R.string.add_habit))
                    }
                },
            )
        }

        if (habits.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Spa,
                    title = stringResource(R.string.no_habits_title),
                    body = stringResource(R.string.no_habits_body),
                )
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                HabitCard(
                    habit = habit,
                    onToggle = { onToggleHabit(habit.id) },
                    onDelete = { habitToDelete = habit },
                )
            }
        }
    }

    if (addDialogVisible) {
        AddHabitDialog(
            onDismiss = { addDialogVisible = false },
            onAdd = { title, emoji ->
                onAddHabit(title, emoji)
                addDialogVisible = false
            },
        )
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text(stringResource(R.string.delete_habit_title)) },
            text = { Text(stringResource(R.string.delete_habit_body)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteHabit(habit.id)
                    habitToDelete = null
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun HabitCard(habit: Habit, onToggle: () -> Unit, onDelete: () -> Unit) {
    val completed = habit.isCompletedToday()
    val streak = currentStreak(habit.completedDates)

    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (completed) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = habit.emoji, style = MaterialTheme.typography.headlineMedium)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (streak > 0) Icons.Default.LocalFireDepartment else Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = if (streak > 0) Sunrise else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.size(5.dp))
                    Text(
                        text = if (streak == 1) stringResource(R.string.streak_one_day)
                        else stringResource(R.string.days_count, streak),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (completed) Mint else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (completed) Icons.Default.Check else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = stringResource(
                        if (completed) R.string.completed_today else R.string.mark_complete,
                    ),
                    tint = if (completed) androidx.compose.ui.graphics.Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    val emojis = listOf("🌱", "💧", "📚", "🚶", "🧘", "✍️", "☀️", "💪")
    var title by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf(emojis.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_habit)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(50) },
                    label = { Text(stringResource(R.string.habit_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(18.dp))
                Text(stringResource(R.string.choose_icon), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    emojis.forEach { emoji ->
                        Card(
                            onClick = { selectedEmoji = emoji },
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        ) {
                            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                                Text(emoji, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, selectedEmoji) },
                enabled = title.isNotBlank(),
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
