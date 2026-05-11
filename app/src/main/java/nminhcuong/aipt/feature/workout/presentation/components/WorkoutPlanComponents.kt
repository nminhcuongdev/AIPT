package nminhcuong.aipt.feature.workout.presentation.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.BuildConfig
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptMetricRow
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptPill
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.workout.domain.model.NutritionGuidance
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.presentation.WorkoutPlanUiState
import nminhcuong.aipt.ui.theme.Ember
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.MintSoft
import nminhcuong.aipt.ui.theme.Sea
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun WorkoutPlanScreen(
    state: WorkoutPlanUiState,
    onBackClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    onRetry: () -> Unit,
    onConfirmPlan: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                WorkoutPlanTopActions(
                    onBackClick = onBackClick,
                    onTrackProgressClick = onTrackProgressClick,
                    canTrackProgress = state.canTrackProgress,
                )
                Spacer(Modifier.height(8.dp))
                when {
                    state.isProfileMissing -> MissingProfileState()
                    state.isLoading -> LoadingState()
                    state.errorMessage != null -> ErrorState(state.errorMessage, onRetry)
                    state.response == null -> ErrorState("No workout plan response was returned.", onRetry)
                    else -> WorkoutPlanContent(
                        state = state,
                        onConfirmPlan = onConfirmPlan,
                        onTrackProgressClick = onTrackProgressClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutPlanTopActions(
    onBackClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    canTrackProgress: Boolean,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TextButton(onClick = onBackClick) { Text("Back") }
        TextButton(onClick = onTrackProgressClick, enabled = canTrackProgress) { Text("Track progress") }
    }
}

@Composable
private fun WorkoutPlanContent(
    state: WorkoutPlanUiState,
    onConfirmPlan: () -> Unit,
    onTrackProgressClick: () -> Unit,
) {
    val response = state.response ?: return
    val plan = response.plan
    AiptHeroHeader(
        eyebrow = "Workout Plan",
        title = "Your weekly training blueprint",
        description = plan.analysisSummary,
    )
    Spacer(Modifier.height(16.dp))
    state.request?.let { request ->
        AiptMetricRow(
            items = listOf(
                request.preferences.daysPerWeek.toString() to "days/week",
                request.preferences.sessionDurationMinutes.toString() to "minutes",
                request.preferences.experienceLevel to "level",
            ),
        )
    }
    Spacer(Modifier.height(18.dp))
    SavePlanActions(
        state = state,
        onConfirmPlan = onConfirmPlan,
        onTrackProgressClick = onTrackProgressClick,
    )
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

@Composable
private fun SavePlanActions(
    state: WorkoutPlanUiState,
    onConfirmPlan: () -> Unit,
    onTrackProgressClick: () -> Unit,
) {
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
}

@Composable
private fun LoadingState() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Column {
                Text("Creating workout plan", style = MaterialTheme.typography.titleLarge, color = Ink900)
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
                Text(exercise.prescriptionText())
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

private fun nminhcuong.aipt.feature.workout.domain.model.PlannedExercise.prescriptionText(): String = buildList {
    sets?.let { add("$it sets") }
    reps?.let { add("$it reps") }
    restSeconds?.let { add("${it}s rest") }
    add(intensity)
}.joinToString(" - ")

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
