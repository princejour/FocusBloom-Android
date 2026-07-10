package com.walhero.focusbloom.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.data.FocusTask
import com.walhero.focusbloom.ui.components.EmptyState
import com.walhero.focusbloom.ui.components.SectionHeader

private enum class TaskFilter { ALL, OPEN, DONE }

@Composable
fun TasksScreen(
    tasks: List<FocusTask>,
    onAddTask: (String) -> Unit,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onClearCompleted: () -> Unit,
) {
    var newTask by rememberSaveable { mutableStateOf("") }
    var filter by remember { mutableStateOf(TaskFilter.ALL) }
    val visibleTasks = when (filter) {
        TaskFilter.ALL -> tasks
        TaskFilter.OPEN -> tasks.filterNot { it.isCompleted }
        TaskFilter.DONE -> tasks.filter { it.isCompleted }
    }

    fun submitTask() {
        if (newTask.isBlank()) return
        onAddTask(newTask)
        newTask = ""
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionHeader(
                title = stringResource(R.string.tasks_title),
                subtitle = stringResource(R.string.tasks_subtitle),
            )
        }

        item {
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it.take(80) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.task_hint)) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                trailingIcon = {
                    IconButton(onClick = ::submitTask, enabled = newTask.isNotBlank()) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitTask() }),
            )
        }

        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = filter == TaskFilter.ALL,
                        onClick = { filter = TaskFilter.ALL },
                        label = { Text(stringResource(R.string.filter_all)) },
                    )
                    FilterChip(
                        selected = filter == TaskFilter.OPEN,
                        onClick = { filter = TaskFilter.OPEN },
                        label = { Text(stringResource(R.string.filter_open)) },
                    )
                    FilterChip(
                        selected = filter == TaskFilter.DONE,
                        onClick = { filter = TaskFilter.DONE },
                        label = { Text(stringResource(R.string.filter_done)) },
                    )
                }
                if (tasks.any { it.isCompleted }) {
                    TextButton(
                        onClick = onClearCompleted,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(stringResource(R.string.clear_completed))
                    }
                }
            }
        }

        if (visibleTasks.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.CheckCircle,
                    title = stringResource(R.string.no_tasks_title),
                    body = stringResource(R.string.no_tasks_body),
                )
            }
        } else {
            items(visibleTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggle = { onToggleTask(task.id) },
                    onDelete = { onDeleteTask(task.id) },
                )
            }
        }
    }
}

@Composable
private fun TaskCard(task: FocusTask, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Spacer(Modifier.size(8.dp))
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.delete))
            }
        }
    }
}
