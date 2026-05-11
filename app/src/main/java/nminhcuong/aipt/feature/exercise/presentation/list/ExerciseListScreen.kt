package nminhcuong.aipt.feature.exercise.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import nminhcuong.aipt.feature.exercise.presentation.list.components.ExerciseListScreen

@Composable
fun ExerciseListRoute(
    onProfileClick: () -> Unit,
    onWorkoutPlanClick: () -> Unit,
    onProgressClick: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    viewModel: ExerciseListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    ExerciseListScreen(
        state = state,
        onProfileClick = onProfileClick,
        onWorkoutPlanClick = onWorkoutPlanClick,
        onProgressClick = onProgressClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onMuscleGroupSelected = viewModel::onMuscleGroupSelected,
        onFavoritesOnlyChanged = viewModel::onFavoritesOnlyChanged,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        onExerciseClick = onExerciseClick,
    )
}
