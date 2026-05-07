package com.example.aipt.feature.workout.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.workout.presentation.components.ProgressTrackingScreen

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
        onSetsChanged = viewModel::onSetsChanged,
        onRepsChanged = viewModel::onRepsChanged,
        onNoteChanged = viewModel::onNoteChanged,
        onAnalyzeDay = viewModel::analyzeDay,
        onConfirmDay = viewModel::confirmSuggestedDay,
        onExerciseChartSelected = viewModel::onExerciseChartSelected,
    )
}
