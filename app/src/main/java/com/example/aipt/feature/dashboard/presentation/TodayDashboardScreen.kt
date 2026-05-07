package com.example.aipt.feature.dashboard.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.dashboard.presentation.components.TodayDashboardScreen

@Composable
fun TodayDashboardRoute(
    onBackClick: () -> Unit,
    onCreatePlanClick: () -> Unit,
    onStartWorkoutClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: TodayDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    TodayDashboardScreen(
        state = state,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onCreatePlanClick = onCreatePlanClick,
        onStartWorkoutClick = {
            viewModel.startWorkout()
            onStartWorkoutClick()
        },
        onCompleteWorkoutClick = viewModel::completeWorkout,
    )
}
