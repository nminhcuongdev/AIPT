package com.example.aipt.feature.dashboard.presentation

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptMetricRow
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.MintSoft
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.Volt

@Composable
fun TodayDashboardRoute(
    onBackClick: () -> Unit,
    onCreatePlanClick: () -> Unit,
    onStartWorkoutClick: () -> Unit,
    viewModel: TodayDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    TodayDashboardScreen(
        state = state,
        onBackClick = onBackClick,
        onCreatePlanClick = onCreatePlanClick,
        onStartWorkoutClick = {
            viewModel.startWorkout()
            onStartWorkoutClick()
        },
        onCompleteWorkoutClick = viewModel::completeWorkout,
    )
}

@Composable
private fun TodayDashboardScreen(
    state: TodayDashboardUiState,
    onBackClick: () -> Unit,
    onCreatePlanClick: () -> Unit,
    onStartWorkoutClick: () -> Unit,
    onCompleteWorkoutClick: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                when {
                    state.isLoading -> LoadingDashboard()
                    state.isPlanMissing -> MissingPlanDashboard(onCreatePlanClick)
                    else -> DashboardContent(
                        state = state,
                        onStartWorkoutClick = onStartWorkoutClick,
                        onCompleteWorkoutClick = onCompleteWorkoutClick,
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoadingDashboard() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            Text("Loading today's workout", style = MaterialTheme.typography.titleLarge, color = Ink900)
        }
    }
}

@Composable
private fun MissingPlanDashboard(onCreatePlanClick: () -> Unit) {
    AiptHeroHeader(
        eyebrow = "Dashboard",
        title = "No workout plan yet",
        description = "Confirm a weekly plan first, then today's training dashboard will appear here.",
    )
    Spacer(Modifier.height(16.dp))
    Button(onClick = onCreatePlanClick, modifier = Modifier.fillMaxWidth().height(54.dp)) {
        Text("Create workout plan")
    }
}

@Composable
private fun DashboardContent(
    state: TodayDashboardUiState,
    onStartWorkoutClick: () -> Unit,
    onCompleteWorkoutClick: () -> Unit,
) {
    AiptHeroHeader(
        eyebrow = state.dayLabel,
        title = state.title,
        description = state.focus,
        trailing = {
            StatusPill(state.status)
        },
    )
    Spacer(Modifier.height(14.dp))
    AiptMetricRow(
        items = listOf(
            "Day ${state.workoutDay}" to "schedule",
            state.exercises.size.toString() to "exercises",
            state.statusLabel to "status",
        ),
    )
    Spacer(Modifier.height(16.dp))
    PrimaryWorkoutAction(state, onStartWorkoutClick, onCompleteWorkoutClick)
    Spacer(Modifier.height(16.dp))
    Text("Today's exercises", style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(10.dp))
    state.exercises.forEachIndexed { index, exercise ->
        TodayExerciseCard(index + 1, exercise)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StatusPill(status: WorkoutSessionStatus) {
    val label = when (status) {
        WorkoutSessionStatus.NotStarted -> "Not started"
        WorkoutSessionStatus.InProgress -> "In progress"
        WorkoutSessionStatus.Completed -> "Done"
    }
    val color = when (status) {
        WorkoutSessionStatus.NotStarted -> Bone
        WorkoutSessionStatus.InProgress -> Volt
        WorkoutSessionStatus.Completed -> Sea
    }
    val contentColor = if (status == WorkoutSessionStatus.NotStarted) Ink900 else Ink900
    AiptPill(label, containerColor = color, contentColor = contentColor)
}

@Composable
private fun PrimaryWorkoutAction(
    state: TodayDashboardUiState,
    onStartWorkoutClick: () -> Unit,
    onCompleteWorkoutClick: () -> Unit,
) {
    when (state.status) {
        WorkoutSessionStatus.NotStarted -> Button(
            onClick = onStartWorkoutClick,
            enabled = state.canStart,
            modifier = Modifier.fillMaxWidth().height(58.dp),
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Start workout")
        }
        WorkoutSessionStatus.InProgress -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onStartWorkoutClick, modifier = Modifier.weight(1f).height(56.dp)) {
                Icon(Icons.Default.Timer, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Resume")
            }
            OutlinedButton(onClick = onCompleteWorkoutClick, modifier = Modifier.weight(1f).height(56.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Complete")
            }
        }
        WorkoutSessionStatus.Completed -> Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MintSoft,
        ) {
            Row(modifier = Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Sea)
                Text("Workout completed for today", style = MaterialTheme.typography.titleMedium, color = Ink900)
            }
        }
    }
}

@Composable
private fun TodayExerciseCard(index: Int, exercise: TodayExerciseUiState) {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = MaterialTheme.shapes.large, color = MintSoft) {
                Text(
                    text = index.toString().padStart(2, '0'),
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                    color = Ink900,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Ink900)
                    Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = Ink900, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TargetChip(Icons.Default.Schedule, "${exercise.sets} sets")
                    TargetChip(Icons.Default.FitnessCenter, "${exercise.reps} reps")
                }
                Spacer(Modifier.height(8.dp))
                Text("Target weight: ${exercise.targetWeight}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (exercise.equipment.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        exercise.equipment.forEach { item -> AssistChip(onClick = {}, label = { Text(item) }) }
                    }
                }
                if (exercise.notes.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(exercise.notes, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun TargetChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(shape = MaterialTheme.shapes.medium, color = Ink900) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Volt)
            Text(text, color = Bone, style = MaterialTheme.typography.labelLarge)
        }
    }
}
