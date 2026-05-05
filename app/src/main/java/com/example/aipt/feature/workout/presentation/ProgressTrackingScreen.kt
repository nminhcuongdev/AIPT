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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.Volt

@Composable
fun ProgressTrackingRoute(
    onBackClick: () -> Unit,
    viewModel: ProgressTrackingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ProgressTrackingScreen(
        state = state,
        onBackClick = onBackClick,
        onWeightKgChanged = viewModel::onWeightKgChanged,
        onRepsChanged = viewModel::onRepsChanged,
        onNoteChanged = viewModel::onNoteChanged,
        onAnalyzeDay = viewModel::analyzeDay,
        onConfirmDay = viewModel::confirmSuggestedDay,
    )
}

@Composable
private fun ProgressTrackingScreen(
    state: ProgressTrackingUiState,
    onBackClick: () -> Unit,
    onWeightKgChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
    onAnalyzeDay: (Int) -> Unit,
    onConfirmDay: (Int) -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = "Progress Tracking",
                    title = "Track each training day",
                    description = "Save one workout day at a time. Each day is analyzed separately and updated for next week when the API returns a new day plan.",
                )
                Spacer(Modifier.height(16.dp))
                when {
                    state.isLoading && state.schedule.isEmpty() -> LoadingPlanPanel()
                    state.isPlanMissing -> MissingPlanPanel()
                    else -> ScheduleTrackingPanel(
                        state = state,
                        onWeightKgChanged = onWeightKgChanged,
                        onRepsChanged = onRepsChanged,
                        onNoteChanged = onNoteChanged,
                        onAnalyzeDay = onAnalyzeDay,
                        onConfirmDay = onConfirmDay,
                    )
                }
                if (state.errorMessage != null) {
                    Spacer(Modifier.height(14.dp))
                    AiptPanel {
                        Text("Analysis failed", style = MaterialTheme.typography.titleLarge, color = Ember)
                        Spacer(Modifier.height(8.dp))
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoadingPlanPanel() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Column {
                Text("Creating workout blueprint", style = MaterialTheme.typography.titleLarge, color = Ink900)
                Text("Using your saved profile to prepare the tracking schedule.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MissingPlanPanel() {
    AiptPanel {
        Text("Workout plan required", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(8.dp))
        Text("Create a weekly training blueprint first, then return here to track each planned exercise.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ScheduleTrackingPanel(
    state: ProgressTrackingUiState,
    onWeightKgChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
    onAnalyzeDay: (Int) -> Unit,
    onConfirmDay: (Int) -> Unit,
) {
    state.schedule.groupBy { it.day to it.dayTitle }.forEach { (day, exercises) ->
        val dayNumber = day.first
        val dayStatus = state.dayStatuses[dayNumber]
        AiptPanel {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AiptPill("Day $dayNumber", containerColor = Volt, contentColor = Ink900)
                AiptPill(day.second, containerColor = Ink900)
            }
            Spacer(Modifier.height(12.dp))
            exercises.forEach { exercise ->
                ExerciseTrackingRow(
                    exercise = exercise,
                    onWeightKgChanged = onWeightKgChanged,
                    onRepsChanged = onRepsChanged,
                    onNoteChanged = onNoteChanged,
                )
                Spacer(Modifier.height(14.dp))
            }
            Button(
                onClick = { onAnalyzeDay(dayNumber) },
                enabled = state.canAnalyzeDay(dayNumber),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (dayStatus?.isLoading == true) CircularProgressIndicator(color = Ink900) else Text("Save day and update next week")
            }
            if (dayStatus?.errorMessage != null) {
                Spacer(Modifier.height(10.dp))
                Text(dayStatus.errorMessage, color = Ember)
            }
            if (dayStatus?.response != null) {
                Spacer(Modifier.height(12.dp))
                DayAnalysisPanel(
                    response = dayStatus.response,
                    isConfirmed = dayStatus.isConfirmed,
                    canConfirm = state.canConfirmDay(dayNumber),
                    onConfirm = { onConfirmDay(dayNumber) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun ExerciseTrackingRow(
    exercise: ProgressExerciseLogUiState,
    onWeightKgChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
) {
    Column {
        Text(exercise.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Ink900)
        if (exercise.plannedPrescription.isNotBlank()) {
            Text(exercise.plannedPrescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (exercise.equipment.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                exercise.equipment.forEach { item -> AssistChip(onClick = {}, label = { Text(item) }) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DecimalField(
                value = exercise.weightKg,
                onValueChange = { onWeightKgChanged(exercise.id, it) },
                label = "Weight kg",
                modifier = Modifier.weight(1f),
            )
            NumberField(
                value = exercise.reps,
                onValueChange = { onRepsChanged(exercise.id, it) },
                label = "Reps",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = exercise.note,
            onValueChange = { onNoteChanged(exercise.id, it) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Note") },
        )
    }
}

@Composable
private fun DayAnalysisPanel(
    response: WorkoutDayProgressAnalysisResponse,
    isConfirmed: Boolean,
    canConfirm: Boolean,
    onConfirm: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AiptPill("Day updated", containerColor = Sea, contentColor = Ink900)
        response.model?.let { AssistChip(onClick = {}, label = { Text(it) }) }
    }
    Spacer(Modifier.height(10.dp))
    response.analysisSummary?.takeIf { it.isNotBlank() }?.let {
        Text(it, style = MaterialTheme.typography.bodyLarge, color = Ink900)
        Spacer(Modifier.height(10.dp))
    }
    response.advice?.takeIf { it.isNotBlank() }?.let {
        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
    }
    AdviceList("Recommendations", response.recommendations, Sea)
    AdviceList("Next steps", response.nextSteps, Ink900)
    AdviceList("Safety notes", response.safetyNotes, Ember)
    if (response.nextWeekDay != null) {
        Spacer(Modifier.height(8.dp))
        if (isConfirmed) {
            Text("Confirmed and saved to workout schedule.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("API suggested a next-week schedule for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text("Confirm and save this day")
            }
        }
    }
}

@Composable
private fun AdviceList(title: String, items: List<String>?, accent: Color) {
    if (items.isNullOrEmpty()) return
    Text(title, style = MaterialTheme.typography.titleMedium, color = accent)
    Spacer(Modifier.height(6.dp))
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Text("- $item", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun NumberField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(label) },
    )
}

@Composable
private fun DecimalField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = { Text(label) },
    )
}
