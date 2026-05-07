package com.example.aipt.feature.workout.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.workout.presentation.components.WorkoutSessionScreen

@Composable
fun WorkoutSessionRoute(
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit,
    viewModel: WorkoutSessionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinishClick()
    }

    WorkoutSessionScreen(
        state = state,
        onBackClick = onBackClick,
        onWeightKgChanged = viewModel::onWeightKgChanged,
        onSetsChanged = viewModel::onSetsChanged,
        onRepsChanged = viewModel::onRepsChanged,
        onNoteChanged = viewModel::onNoteChanged,
        onStartRest = viewModel::startRestTimer,
        onStopRest = viewModel::stopRestTimer,
        onNextExercise = viewModel::nextExercise,
        onFinishWorkout = viewModel::finishWorkout,
    )
}
