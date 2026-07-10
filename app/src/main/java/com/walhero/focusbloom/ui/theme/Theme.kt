package com.walhero.focusbloom.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.walhero.focusbloom.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = MintSoft,
    onPrimaryContainer = ForestDark,
    secondary = Lavender,
    onSecondary = Color.White,
    secondaryContainer = LavenderSoft,
    onSecondaryContainer = Color(0xFF2D2469),
    tertiary = Sunrise,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEAF0ED),
    outline = Color(0xFF71807B),
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF79DDBB),
    onPrimary = Color(0xFF00382D),
    primaryContainer = ForestDark,
    onPrimaryContainer = Color(0xFFB5F1DC),
    secondary = Color(0xFFC7BBFF),
    onSecondary = Color(0xFF32236F),
    secondaryContainer = Color(0xFF493B88),
    onSecondaryContainer = Color(0xFFE6DFFF),
    tertiary = Color(0xFFFFC77A),
    background = Night,
    onBackground = Color(0xFFE3ECE8),
    surface = NightSurface,
    onSurface = Color(0xFFE3ECE8),
    surfaceVariant = NightCard,
    outline = Color(0xFF8B9B95),
    error = Color(0xFFFFB4AB),
)

@Composable
fun FocusBloomTheme(
    themeMode: ThemeMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = FocusBloomTypography,
        content = content,
    )
}
