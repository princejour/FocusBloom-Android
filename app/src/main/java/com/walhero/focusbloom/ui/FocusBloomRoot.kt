package com.walhero.focusbloom.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.ui.components.BannerAd
import com.walhero.focusbloom.ui.screens.CalmScreen
import com.walhero.focusbloom.ui.screens.FocusScreen
import com.walhero.focusbloom.ui.screens.HabitsScreen
import com.walhero.focusbloom.ui.screens.InsightsScreen
import com.walhero.focusbloom.ui.screens.SettingsSheetContent
import com.walhero.focusbloom.ui.screens.TasksScreen

private enum class Destination(
    @StringRes val label: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    FOCUS(R.string.nav_focus, Icons.Outlined.Psychology, Icons.Filled.Psychology),
    HABITS(R.string.nav_habits, Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    TASKS(R.string.nav_tasks, Icons.Outlined.Checklist, Icons.Filled.Checklist),
    INSIGHTS(R.string.nav_insights, Icons.Outlined.Insights, Icons.Filled.Insights),
    CALM(R.string.nav_calm, Icons.Outlined.SelfImprovement, Icons.Filled.SelfImprovement),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusBloomRoot(
    viewModel: FocusBloomViewModel,
    adsReady: Boolean,
    privacyOptionsRequired: Boolean,
    onPrivacyOptions: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    val appState by viewModel.appState.collectAsStateWithLifecycleCompat()
    val timerState by viewModel.timerState.collectAsStateWithLifecycleCompat()
    var destination by rememberSaveable { mutableStateOf(Destination.FOCUS) }
    var settingsVisible by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sessionCompleted.collect {
            snackbarHostState.showSnackbar(context.getString(R.string.session_complete))
        }
    }

    if (!appState.onboardingCompleted) {
        OnboardingScreen(onComplete = viewModel::completeOnboarding)
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(onClick = { settingsVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_description),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            val useNavigationRail = maxWidth >= 720.dp
            if (useNavigationRail) {
                Row(Modifier.fillMaxSize()) {
                    DestinationRail(
                        selected = destination,
                        onSelected = { destination = it },
                    )
                    Column(Modifier.weight(1f)) {
                        ScreenContent(
                            destination = destination,
                            viewModel = viewModel,
                            appState = appState,
                            timerState = timerState,
                            onOpenCalm = { destination = Destination.CALM },
                            modifier = Modifier.weight(1f),
                        )
                        if (adsReady) BannerAd()
                    }
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    ScreenContent(
                        destination = destination,
                        viewModel = viewModel,
                        appState = appState,
                        timerState = timerState,
                        onOpenCalm = { destination = Destination.CALM },
                        modifier = Modifier.weight(1f),
                    )
                    if (adsReady) BannerAd()
                    DestinationBar(
                        selected = destination,
                        onSelected = { destination = it },
                    )
                }
            }
        }
    }

    if (settingsVisible) {
        ModalBottomSheet(onDismissRequest = { settingsVisible = false }) {
            SettingsSheetContent(
                themeMode = appState.themeMode,
                privacyOptionsRequired = privacyOptionsRequired,
                onThemeModeSelected = viewModel::setThemeMode,
                onPrivacyOptions = onPrivacyOptions,
                onLanguageSelected = onLanguageSelected,
                onClose = { settingsVisible = false },
            )
        }
    }
}

@Composable
private fun ScreenContent(
    destination: Destination,
    viewModel: FocusBloomViewModel,
    appState: com.walhero.focusbloom.data.AppState,
    timerState: com.walhero.focusbloom.data.FocusTimerState,
    onOpenCalm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        when (destination) {
            Destination.FOCUS -> FocusScreen(
                appState = appState,
                timerState = timerState,
                onDurationSelected = viewModel::selectDuration,
                onStartPause = viewModel::startOrPauseTimer,
                onReset = viewModel::resetTimer,
                onOpenCalm = onOpenCalm,
            )
            Destination.HABITS -> HabitsScreen(
                habits = appState.habits,
                onAddHabit = viewModel::addHabit,
                onToggleHabit = viewModel::toggleHabit,
                onDeleteHabit = viewModel::deleteHabit,
            )
            Destination.TASKS -> TasksScreen(
                tasks = appState.tasks,
                onAddTask = viewModel::addTask,
                onToggleTask = viewModel::toggleTask,
                onDeleteTask = viewModel::deleteTask,
                onClearCompleted = viewModel::clearCompletedTasks,
            )
            Destination.INSIGHTS -> InsightsScreen(appState = appState)
            Destination.CALM -> CalmScreen()
        }
    }
}

@Composable
private fun DestinationBar(selected: Destination, onSelected: (Destination) -> Unit) {
    NavigationBar {
        Destination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selected == destination,
                onClick = { onSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected == destination) destination.selectedIcon else destination.icon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

@Composable
private fun DestinationRail(selected: Destination, onSelected: (Destination) -> Unit) {
    NavigationRail(Modifier.fillMaxHeight()) {
        Destination.entries.forEach { destination ->
            NavigationRailItem(
                selected = selected == destination,
                onClick = { onSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected == destination) destination.selectedIcon else destination.icon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}
