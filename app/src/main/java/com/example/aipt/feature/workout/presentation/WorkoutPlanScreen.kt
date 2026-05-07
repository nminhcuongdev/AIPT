package com.example.aipt.feature.workout.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.workout.presentation.components.WorkoutPlanScreen

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
