package com.example.aipt.feature.workout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.BuildConfig
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptMetricRow
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.feature.workout.domain.model.NutritionGuidance
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.Volt
import com.example.aipt.ui.theme.MintSoft

@Composable
fun WorkoutPlanRoute(
    onBackClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    viewModel: WorkoutPlanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    WorkoutPlanScreen(
        state = state,
        onBackClick = onBackClick,
        onTrackProgressClick = onTrackProgressClick,
        onRetry = viewModel::retry,
        onConfirmPlan = viewModel::confirmPlan,
    )
}

@Composable
private fun WorkoutPlanScreen(
    state: WorkoutPlanUiState,
    onBackClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    onRetry: () -> Unit,
    onConfirmPlan: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(onClick = onBackClick) { Text("Back") }
                    TextButton(onClick = onTrackProgressClick, enabled = state.canTrackProgress) { Text("Track progress") }
                }
                Spacer(Modifier.height(8.dp))
                when {
                    state.isProfileMissing -> MissingProfileState()
                    state.isLoading -> LoadingState()
                    state.errorMessage != null -> ErrorState(state.errorMessage, onRetry)
                    state.response == null -> ErrorState("No workout plan response was returned.", onRetry)
                    else -> {
                        val response = state.response
                        val plan = response.plan
                        AiptHeroHeader(
                            eyebrow = "Workout Plan",
                            title = "Your weekly training blueprint",
                            description = plan.analysisSummary,
                        )
                        Spacer(Modifier.height(16.dp))
                        val request = state.request
                        if (request != null) {
                            AiptMetricRow(
                                items = listOf(
                                    request.preferences.daysPerWeek.toString() to "days/week",
                                    request.preferences.sessionDurationMinutes.toString() to "minutes",
                                    request.preferences.experienceLevel to "level",
                                ),
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        Button(
                            onClick = onConfirmPlan,
                            enabled = state.canConfirmPlan,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                        ) {
                            if (state.isSavingPlan) CircularProgressIndicator(color = Ink900) else Text("Confirm and save workout plan")
                        }
                        if (state.saveMessage != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(state.saveMessage, color = Sea)
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onTrackProgressClick,
                            enabled = state.canTrackProgress,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                        ) {
                            Text("Track workout progress")
                        }
                        Spacer(Modifier.height(18.dp))
                        GuidancePanel("Follow-up questions", plan.followUpQuestions)
                        Spacer(Modifier.height(14.dp))
                        GuidancePanel("Assumptions", plan.assumptions)
                        Spacer(Modifier.height(18.dp))
                        plan.weeklySchedule.forEach { day ->
                            WorkoutDayCard(day)
                            Spacer(Modifier.height(14.dp))
                        }
                        ProgressionPanel(plan.progressionPlan.map { "Week ${it.week}: ${it.instructions}" })
                        Spacer(Modifier.height(14.dp))
                        NutritionPanel(plan.nutritionGuidance)
                        Spacer(Modifier.height(14.dp))
                        GuidancePanel("Recovery", plan.recoveryGuidance)
                        Spacer(Modifier.height(14.dp))
                        GuidancePanel("Safety notes", plan.safetyNotes, accent = Ember)
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun LoadingState() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Column {
                Text("Creating workout plan", style = MaterialTheme.typography.titleLarge, color = Ink900)
                Text("Calling ${BuildConfig.API_BASE_URL.withTrailingSlash()}api/v1/workout-plans", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    AiptPanel {
        Text("Workout plan request failed", style = MaterialTheme.typography.titleLarge, color = Ember)
        Spacer(Modifier.height(8.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("Retry")
        }
    }
}
@Composable
private fun MissingProfileState() {
    AiptPanel {
        Text("Profile required", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(8.dp))
        Text("Complete and save your profile first so the planner can build a request body.")
    }
}

@Composable
private fun WorkoutDayCard(day: WorkoutDay) {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AiptPill("Day ${day.day}", containerColor = Volt, contentColor = Ink900)
            AiptPill(day.title, containerColor = Ink900)
        }
        Spacer(Modifier.height(10.dp))
        Text(day.focus, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        Text("Warm-up", style = MaterialTheme.typography.titleMedium, color = Ink900)
        BulletList(day.warmup)
        Spacer(Modifier.height(12.dp))
        Text("Exercises", style = MaterialTheme.typography.titleMedium, color = Ink900)
        day.exercises.forEach { exercise ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.Surface(shape = MaterialTheme.shapes.medium, color = MintSoft) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Ink900)
                    }
                    Text(exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Ink900)
                }
                val prescription = buildList {
                    exercise.sets?.let { add("$it sets") }
                    exercise.reps?.let { add("$it reps") }
                    exercise.restSeconds?.let { add("${it}s rest") }
                    add(exercise.intensity)
                }.joinToString(" - ")
                Text(prescription)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    exercise.equipment.forEach { item -> AssistChip(onClick = {}, label = { Text(item) }) }
                }
                Text(exercise.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Cooldown", style = MaterialTheme.typography.titleMedium, color = Ink900)
        BulletList(day.cooldown)
    }
}

@Composable
private fun ProgressionPanel(items: List<String>) = GuidancePanel("Progression", items, accent = Sea)

@Composable
private fun NutritionPanel(nutrition: NutritionGuidance) {
    GuidancePanel(
        title = "Nutrition",
        items = listOf(nutrition.calorieGuidance, nutrition.proteinGuidance, nutrition.hydrationGuidance),
        accent = Volt,
    )
}

@Composable
private fun GuidancePanel(title: String, items: List<String>, accent: Color = Ink900) {
    AiptPanel {
        Text(title, style = MaterialTheme.typography.titleLarge, color = accent)
        Spacer(Modifier.height(8.dp))
        BulletList(items)
    }
}

private fun String.withTrailingSlash(): String = if (endsWith('/')) this else "$this/"

@Composable
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Text("- $item", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
