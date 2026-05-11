package nminhcuong.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.profile.presentation.ExperienceLevels
import nminhcuong.aipt.feature.profile.presentation.PreferredLanguages
import nminhcuong.aipt.feature.profile.presentation.ProfileSetupUiState
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun PreferencesScreen(
    state: ProfileSetupUiState,
    onDaysPerWeekChanged: (String) -> Unit,
    onSessionDurationChanged: (String) -> Unit,
    onExperienceLevelSelected: (String) -> Unit,
    onInjuriesChanged: (String) -> Unit,
    onPreferredLanguageSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 04 / Preferences",
                    title = "Set training constraints",
                    description = "Complete the workout-plan request with schedule, experience, limitations, and response language.",
                )
                Spacer(Modifier.height(22.dp))
                AiptPanel {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NumberField(state.daysPerWeek, onDaysPerWeekChanged, "Days / week", Modifier.weight(1f))
                        NumberField(state.sessionDurationMinutes, onSessionDurationChanged, "Minutes", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    OptionChips(
                        title = "Experience level",
                        values = ExperienceLevels,
                        selectedValue = state.experienceLevel,
                        labelFor = { it.replaceFirstChar { char -> char.uppercase() } },
                        onSelected = onExperienceLevelSelected,
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.injuriesOrLimitations,
                        onValueChange = onInjuriesChanged,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Injuries or limitations") },
                        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    )
                    Spacer(Modifier.height(16.dp))
                    OptionChips(
                        title = "Preferred response language",
                        values = PreferredLanguages,
                        selectedValue = state.preferredLanguage,
                        labelFor = { it.label },
                        valueFor = { it.code },
                        onSelected = onPreferredLanguageSelected,
                    )
                }
                Spacer(Modifier.height(18.dp))
                NavButtons(onBack, onNext)
            }
        }
    }
}

@Composable
private fun <T> OptionChips(
    title: String,
    values: List<T>,
    selectedValue: String,
    labelFor: (T) -> String,
    valueFor: (T) -> String = { it.toString() },
    onSelected: (String) -> Unit,
) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = Ink900)
    Spacer(Modifier.height(10.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.forEach { item ->
            val value = valueFor(item)
            FilterChip(
                selected = selectedValue == value,
                onClick = { onSelected(value) },
                label = { Text(labelFor(item)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Ink900,
                    selectedLabelColor = Volt,
                ),
            )
        }
    }
}
