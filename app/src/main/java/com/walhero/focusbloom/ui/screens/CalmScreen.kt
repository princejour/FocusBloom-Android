package com.walhero.focusbloom.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.R
import com.walhero.focusbloom.ui.components.SectionHeader
import com.walhero.focusbloom.ui.theme.Lavender
import com.walhero.focusbloom.ui.theme.Mint
import kotlinx.coroutines.delay

private data class BreathingPhase(@StringRes val label: Int, val seconds: Int, val scale: Float)

@Composable
fun CalmScreen() {
    val phases = listOf(
        BreathingPhase(R.string.breathe_in, 4, 1.18f),
        BreathingPhase(R.string.hold, 4, 1.18f),
        BreathingPhase(R.string.breathe_out, 6, 0.72f),
    )
    var phaseIndex by remember { mutableIntStateOf(0) }
    var secondsRemaining by remember { mutableIntStateOf(phases.first().seconds) }
    var isRunning by remember { mutableStateOf(false) }
    val phase = phases[phaseIndex]

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            if (secondsRemaining <= 1) {
                phaseIndex = (phaseIndex + 1) % phases.size
                secondsRemaining = phases[phaseIndex].seconds
            } else {
                secondsRemaining -= 1
            }
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isRunning) phase.scale else 0.86f,
        animationSpec = tween(durationMillis = if (isRunning) phase.seconds * 900 else 500),
        label = "breathingScale",
    )
    val infiniteTransition = rememberInfiniteTransition(label = "calmPulse")
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "haloAlpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        SectionHeader(
            title = stringResource(R.string.calm_title),
            subtitle = stringResource(R.string.calm_subtitle),
        )
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f),
                                MaterialTheme.colorScheme.surface,
                            ),
                        ),
                    )
                    .padding(horizontal = 24.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.size(250.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(225.dp)
                            .scale(animatedScale)
                            .alpha(haloAlpha)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .size(178.dp)
                            .scale(animatedScale)
                            .background(
                                Brush.linearGradient(listOf(Mint, Lavender)),
                                CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(42.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isRunning) stringResource(phase.label)
                                else stringResource(R.string.breathing_ready),
                                style = MaterialTheme.typography.titleLarge,
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (isRunning) secondsRemaining.toString() else "4 · 4 · 6",
                                style = MaterialTheme.typography.headlineMedium,
                                color = androidx.compose.ui.graphics.Color.White,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(if (isRunning) R.string.pause_breathing else R.string.begin_breathing))
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Default.Spa, null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(
                        stringResource(R.string.calm_tip_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.calm_tip_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
