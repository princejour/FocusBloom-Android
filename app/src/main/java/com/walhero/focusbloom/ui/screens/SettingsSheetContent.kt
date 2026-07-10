package com.walhero.focusbloom.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.walhero.focusbloom.BuildConfig
import com.walhero.focusbloom.R
import com.walhero.focusbloom.data.ThemeMode

@Composable
fun SettingsSheetContent(
    themeMode: ThemeMode,
    privacyOptionsRequired: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onPrivacyOptions: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onClose: () -> Unit,
) {
    val applicationLocales = AppCompatDelegate.getApplicationLocales()
    val selectedLanguage = if (applicationLocales.isEmpty) "" else applicationLocales[0]?.language.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .navigationBarsPadding(),
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(22.dp))

        SettingsTitle(Icons.Default.DarkMode, stringResource(R.string.appearance))
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ThemeMode.entries.forEach { mode ->
                val (label, icon) = when (mode) {
                    ThemeMode.SYSTEM -> R.string.theme_system to Icons.Default.Devices
                    ThemeMode.LIGHT -> R.string.theme_light to Icons.Default.LightMode
                    ThemeMode.DARK -> R.string.theme_dark to Icons.Default.DarkMode
                }
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeSelected(mode) },
                    label = { Text(stringResource(label)) },
                    leadingIcon = { Icon(icon, null, Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        SettingsTitle(Icons.Default.Language, stringResource(R.string.language))
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LanguageOption(
                label = stringResource(R.string.language_system),
                selected = selectedLanguage.isBlank(),
                onClick = { onLanguageSelected("") },
            )
            LanguageOption(
                label = stringResource(R.string.language_english),
                selected = selectedLanguage == "en",
                onClick = { onLanguageSelected("en") },
            )
            LanguageOption(
                label = stringResource(R.string.language_arabic),
                selected = selectedLanguage == "ar",
                onClick = { onLanguageSelected("ar") },
            )
            LanguageOption(
                label = stringResource(R.string.language_french),
                selected = selectedLanguage == "fr",
                onClick = { onLanguageSelected("fr") },
            )
        }

        Spacer(Modifier.height(24.dp))
        SettingsTitle(Icons.Default.Lock, stringResource(R.string.privacy))
        Spacer(Modifier.height(10.dp))
        InfoCard(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.local_first),
            body = stringResource(R.string.local_first_body),
        )
        if (privacyOptionsRequired) {
            Spacer(Modifier.height(10.dp))
            Card(
                onClick = onPrivacyOptions,
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Default.PrivacyTip, null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text(stringResource(R.string.privacy_choices), fontWeight = FontWeight.SemiBold)
                        Text(
                            stringResource(R.string.privacy_choices_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        SettingsTitle(Icons.Default.Info, stringResource(R.string.about))
        Spacer(Modifier.height(10.dp))
        InfoCard(
            icon = Icons.Default.Info,
            title = stringResource(R.string.about_body),
            body = stringResource(R.string.ads_note),
        )
        Text(
            text = stringResource(R.string.version_label, BuildConfig.VERSION_NAME),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        ) {
            Text(stringResource(R.string.close))
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun SettingsTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(21.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, body: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
