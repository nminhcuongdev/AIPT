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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.example.aipt.feature.workout.domain.model.WorkoutProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutProgressEntry
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
        onExerciseNameChanged = viewModel::onExerciseNameChanged,
        onWeightKgChanged = viewModel::onWeightKgChanged,
        onSetsChanged = viewModel::onSetsChanged,
        onRepsChanged = viewModel::onRepsChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onAddEntry = viewModel::addEntry,
        onRemoveEntry = viewModel::removeEntry,
        onAnalyze = viewModel::analyzeProgress,
    )
}

@Composable
private fun ProgressTrackingScreen(
    state: ProgressTrackingUiState,
    onBackClick: () -> Unit,
    onExerciseNameChanged: (String) -> Unit,
    onWeightKgChanged: (String) -> Unit,
    onSetsChanged: (String) -> Unit,
    onRepsChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAddEntry: () -> Unit,
    onRemoveEntry: (Int) -> Unit,
    onAnalyze: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = "Progress Tracking",
                    title = "Log lifts and reps",
                    description = "Record actual workout numbers, then send them to the API for progression advice.",
                )
                Spacer(Modifier.height(16.dp))
                AiptPanel {
                    OutlinedTextField(
                        value = state.exerciseName,
                        onValueChange = onExerciseNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Exercise") },
                        placeholder = { Text("Dumbbell Bench Press") },
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DecimalField(state.weightKg, onWeightKgChanged, "Weight kg", Modifier.weight(1f))
                        NumberField(state.sets, onSetsChanged, "Sets", Modifier.weight(1f))
                        NumberField(state.reps, onRepsChanged, "Reps", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = onNotesChanged,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        label = { Text("Notes") },
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onAddEntry, enabled = state.canAdd, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Add entry")
                    }
                }
                Spacer(Modifier.height(14.dp))
                EntryListPanel(state.entries, onRemoveEntry)
                Spacer(Modifier.height(14.dp))
                Button(onClick = onAnalyze, enabled = state.canAnalyze, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    if (state.isLoading) CircularProgressIndicator(color = Ink900) else Text("Analyze progress")
                }
                if (state.errorMessage != null) {
                    Spacer(Modifier.height(14.dp))
                    AiptPanel {
                        Text("Analysis failed", style = MaterialTheme.typography.titleLarge, color = Ember)
                        Spacer(Modifier.height(8.dp))
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (state.response != null) {
                    Spacer(Modifier.height(14.dp))
                    AnalysisPanel(state.response)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EntryListPanel(entries: List<WorkoutProgressEntry>, onRemoveEntry: (Int) -> Unit) {
    AiptPanel {
        Text("Logged entries", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(8.dp))
        if (entries.isEmpty()) {
            Text("No workout entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            entries.forEachIndexed { index, entry ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Ink900)
                            Text(entry.summary(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(onClick = { onRemoveEntry(index) }) { Text("Remove") }
                    }
                    if (!entry.notes.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(entry.notes, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisPanel(response: WorkoutProgressAnalysisResponse) {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AiptPill("AI advice", containerColor = Volt, contentColor = Ink900)
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

private fun WorkoutProgressEntry.summary(): String = buildList {
    weightKg?.let { add("$it kg") }
    sets?.let { add("$it sets") }
    reps?.let { add("$it reps") }
}.joinToString(" - ").ifBlank { "No numbers" }