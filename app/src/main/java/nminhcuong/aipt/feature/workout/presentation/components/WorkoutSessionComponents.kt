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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptMetricRow
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptPill
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.workout.presentation.WorkoutSessionExerciseUiState
import nminhcuong.aipt.feature.workout.presentation.WorkoutSessionUiState
import nminhcuong.aipt.ui.theme.Bone
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.MintSoft
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun WorkoutSessionScreen(
    state: WorkoutSessionUiState,
    onBackClick: () -> Unit,
    onWeightKgChanged: (String) -> Unit,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onStartRest: () -> Unit,
    onStopRest: () -> Unit,
    onNextExercise: () -> Unit,
    onFinishWorkout: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                when {
                    state.isLoading -> LoadingSessionPanel()
                    state.isPlanMissing -> MissingSessionPlanPanel()
                    else -> ActiveSessionContent(
                        state = state,
                        onWeightKgChanged = onWeightKgChanged,
                        onSetsChanged = onSetsChanged,
                        onRepsChanged = onRepsChanged,
                        onNoteChanged = onNoteChanged,
                        onStartRest = onStartRest,
                        onStopRest = onStopRest,
                        onNextExercise = onNextExercise,
                        onFinishWorkout = onFinishWorkout,
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoadingSessionPanel() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            Text("Loading workout session", style = MaterialTheme.typography.titleLarge, color = Ink900)
        }
    }
}

@Composable
private fun MissingSessionPlanPanel() {
    AiptHeroHeader(
        eyebrow = "Workout Session",
        title = "No plan available",
        description = "Confirm a workout plan before starting a guided session.",
    )
}

@Composable
private fun ActiveSessionContent(
    state: WorkoutSessionUiState,
    onWeightKgChanged: (String) -> Unit,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onStartRest: () -> Unit,
    onStopRest: () -> Unit,
    onNextExercise: () -> Unit,
    onFinishWorkout: () -> Unit,
) {
    val exercise = state.currentExercise ?: return

    AiptHeroHeader(
        eyebrow = "Workout Session",
        title = state.title,
        description = state.focus,
        trailing = { AiptPill(state.progressLabel, containerColor = Volt, contentColor = Ink900) },
    )
    Spacer(Modifier.height(14.dp))
    AiptMetricRow(
        items = listOf(
            state.progressLabel to "exercise",
            exercise.restSeconds.toString() to "rest sec",
            state.exercises.size.toString() to "total",
        ),
    )
    Spacer(Modifier.height(16.dp))
    CurrentExercisePanel(
        exercise = exercise,
        isResting = state.isResting,
        restRemainingSeconds = state.restRemainingSeconds,
        onWeightKgChanged = onWeightKgChanged,
        onSetsChanged = onSetsChanged,
        onRepsChanged = onRepsChanged,
        onNoteChanged = onNoteChanged,
        onStartRest = onStartRest,
        onStopRest = onStopRest,
    )
    Spacer(Modifier.height(14.dp))
    SessionActions(
        canGoNext = state.canGoNext,
        canFinish = state.canFinish,
        onNextExercise = onNextExercise,
        onFinishWorkout = onFinishWorkout,
    )
    Spacer(Modifier.height(18.dp))
    Text("Session queue", style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(10.dp))
    state.exercises.forEachIndexed { index, item ->
        QueueExerciseRow(index = index, exercise = item, isCurrent = index == state.currentIndex)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SessionActions(
    canGoNext: Boolean,
    canFinish: Boolean,
    onNextExercise: () -> Unit,
    onFinishWorkout: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onNextExercise,
            enabled = canGoNext,
            modifier = Modifier.weight(1f).height(56.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Next exercise")
        }
        OutlinedButton(
            onClick = onFinishWorkout,
            enabled = canFinish,
            modifier = Modifier.weight(1f).height(56.dp),
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text("Finish")
        }
    }
}

@Composable
private fun CurrentExercisePanel(
    exercise: WorkoutSessionExerciseUiState,
    isResting: Boolean,
    restRemainingSeconds: Int,
    onWeightKgChanged: (String) -> Unit,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onStartRest: () -> Unit,
    onStopRest: () -> Unit,
) {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.large, color = MintSoft) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Ink900)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.headlineSmall, color = Ink900, fontWeight = FontWeight.Black)
                Text(exercise.prescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        ExerciseContext(exercise)
        Spacer(Modifier.height(16.dp))
        ExerciseLogFields(
            exercise = exercise,
            onWeightKgChanged = onWeightKgChanged,
            onSetsChanged = onSetsChanged,
            onRepsChanged = onRepsChanged,
            onNoteChanged = onNoteChanged,
        )
        Spacer(Modifier.height(16.dp))
        RestTimerPanel(
            isResting = isResting,
            remainingSeconds = restRemainingSeconds,
            onStartRest = onStartRest,
            onStopRest = onStopRest,
        )
    }
}

@Composable
private fun ExerciseContext(exercise: WorkoutSessionExerciseUiState) {
    if (exercise.equipment.isNotEmpty()) {
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            exercise.equipment.forEach { item -> AssistChip(onClick = {}, label = { Text(item) }) }
        }
    }
    if (exercise.plannedNotes.isNotBlank()) {
        Spacer(Modifier.height(10.dp))
        Text(exercise.plannedNotes, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ExerciseLogFields(
    exercise: WorkoutSessionExerciseUiState,
    onWeightKgChanged: (String) -> Unit,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
) {
    DecimalSessionField(
        value = exercise.weightKg,
        onValueChange = onWeightKgChanged,
        label = "Weight kg",
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        NumberSessionField(
            value = exercise.sets,
            onValueChange = onSetsChanged,
            label = "Sets",
            modifier = Modifier.weight(1f),
        )
        NumberSessionField(
            value = exercise.reps,
            onValueChange = onRepsChanged,
            label = "Reps",
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = exercise.note,
        onValueChange = onNoteChanged,
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        label = { Text("Note") },
    )
}

@Composable
private fun RestTimerPanel(
    isResting: Boolean,
    remainingSeconds: Int,
    onStartRest: () -> Unit,
    onStopRest: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isResting) Ink900 else MintSoft,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = if (isResting) Volt else Ink900)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isResting) formatTime(remainingSeconds) else "Rest timer",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isResting) Bone else Ink900,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = if (isResting) "Rest before the next set" else "Start rest between sets",
                    color = if (isResting) Bone.copy(alpha = 0.74f) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isResting) {
                OutlinedButton(onClick = onStopRest) {
                    Icon(Icons.Default.PauseCircle, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 3.dp))
                    Text("Stop")
                }
            } else {
                Button(onClick = onStartRest) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 3.dp))
                    Text("Rest")
                }
            }
        }
    }
}

@Composable
private fun QueueExerciseRow(index: Int, exercise: WorkoutSessionExerciseUiState, isCurrent: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (isCurrent) MintSoft else MaterialTheme.colorScheme.surface,
        shadowElevation = if (isCurrent) 4.dp else 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AiptPill((index + 1).toString().padStart(2, '0'), containerColor = if (isCurrent) Volt else Ink900, contentColor = if (isCurrent) Ink900 else Bone)
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = Ink900, fontWeight = FontWeight.Black)
                Text(exercise.prescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun NumberSessionField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
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
private fun DecimalSessionField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = { Text(label) },
    )
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainder = seconds % 60
    return "%d:%02d".format(minutes, remainder)
}
