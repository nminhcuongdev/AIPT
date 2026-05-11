package nminhcuong.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptMetricRow
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.profile.presentation.ProfileSetupUiState
import nminhcuong.aipt.ui.theme.Ink900

@Composable
internal fun ProfileManagerScreen(
    state: ProfileSetupUiState,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onCreateProfile: () -> Unit,
    onCreateWorkoutPlan: () -> Unit,
    onDeleteProfile: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBack) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = "Profile and equipment",
                    title = if (state.hasProfile) state.name else "No profile yet",
                    description = if (state.hasProfile) {
                        "Review your current baseline, update training inputs, or remove this profile."
                    } else {
                        "Create a profile before generating a workout plan."
                    },
                )
                Spacer(Modifier.height(16.dp))
                if (state.hasProfile) {
                    CurrentProfileContent(
                        state = state,
                        onEditProfile = onEditProfile,
                        onCreateWorkoutPlan = onCreateWorkoutPlan,
                        onDeleteProfile = onDeleteProfile,
                    )
                } else {
                    EmptyProfileContent(onCreateProfile = onCreateProfile)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CurrentProfileContent(
    state: ProfileSetupUiState,
    onEditProfile: () -> Unit,
    onCreateWorkoutPlan: () -> Unit,
    onDeleteProfile: () -> Unit,
) {
    AiptMetricRow(
        listOf(
            state.weightKg.ifBlank { "--" } to "kg",
            state.bodyFatPercent.ifBlank { "--" } to "body fat %",
            state.skeletalMuscleMassKg.ifBlank { "--" } to "SMM kg",
        ),
    )
    Spacer(Modifier.height(16.dp))
    AiptPanel {
        ProfileSummaryLine("Age", state.age)
        ProfileSummaryLine("Height", state.heightCm, " cm")
        ProfileSummaryLine("Goal", state.selectedGoal)
        ProfileSummaryLine(
            label = "Schedule",
            value = listOf(state.daysPerWeek, state.sessionDurationMinutes)
                .filter { it.isNotBlank() }
                .joinToString(" days/week, "),
            suffix = if (state.sessionDurationMinutes.isNotBlank()) " min" else "",
        )
        ProfileSummaryLine("Experience", state.experienceLevel)
        ProfileSummaryLine("Limitations", state.injuriesOrLimitations)
        Spacer(Modifier.height(10.dp))
        AiptMetricRow(
            listOf(
                state.availableCount.toString() to "available",
                state.unavailableCount.toString() to "blocked",
                state.remainingCount.toString() to "unset",
            ),
        )
    }
    Spacer(Modifier.height(14.dp))
    Button(onClick = onCreateWorkoutPlan, modifier = Modifier.fillMaxWidth().height(54.dp)) {
        Text("Create workout plan")
    }
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(onClick = onEditProfile, modifier = Modifier.weight(1f).height(54.dp)) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Edit")
        }
        OutlinedButton(onClick = onDeleteProfile, modifier = Modifier.weight(1f).height(54.dp)) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Delete")
        }
    }
}

@Composable
private fun EmptyProfileContent(onCreateProfile: () -> Unit) {
    AiptPanel {
        Text(
            text = "Profile deleted or not created yet",
            style = MaterialTheme.typography.titleLarge,
            color = Ink900,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Start a new profile to unlock plan generation and progress tracking.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onCreateProfile, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Create new profile")
        }
    }
}

@Composable
private fun ProfileSummaryLine(label: String, value: String, suffix: String = "") {
    val displayValue = value.ifBlank { "--" }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = "$displayValue$suffix",
            style = MaterialTheme.typography.titleMedium,
            color = Ink900,
            fontWeight = FontWeight.Bold,
        )
    }
}
