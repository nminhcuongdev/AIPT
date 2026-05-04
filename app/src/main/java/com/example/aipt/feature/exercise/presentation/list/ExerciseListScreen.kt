package com.example.aipt.feature.exercise.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.exercise.domain.model.Exercise

@Composable
fun ExerciseListRoute(
    onProfileClick: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    viewModel: ExerciseListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    ExerciseListScreen(
        state = state,
        onProfileClick = onProfileClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onMuscleGroupSelected = viewModel::onMuscleGroupSelected,
        onFavoritesOnlyChanged = viewModel::onFavoritesOnlyChanged,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        onExerciseClick = onExerciseClick,
    )
}

@Composable
private fun ExerciseListScreen(
    state: ExerciseListUiState,
    onProfileClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMuscleGroupSelected: (String) -> Unit,
    onFavoritesOnlyChanged: () -> Unit,
    onFavoriteClicked: (Exercise) -> Unit,
    onExerciseClick: (Int) -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Thu vien bai tap",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = onProfileClick) {
                    Text("Profile")
                }
            }
            Text(
                text = "Tim bai tap theo nhom co, dung cu va muc tieu luyen tap.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Tim kiem bai tap") },
                placeholder = { Text("Vi du: squat, chest, dumbbell") },
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.showFavoritesOnly,
                        onClick = onFavoritesOnlyChanged,
                        label = { Text("Yeu thich") },
                    )
                }
                items(state.muscleGroups) { muscleGroup ->
                    FilterChip(
                        selected = state.selectedMuscleGroup == muscleGroup,
                        onClick = { onMuscleGroupSelected(muscleGroup) },
                        label = { Text(if (muscleGroup == "All") "Tat ca" else muscleGroup) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (state.exercises.isEmpty()) {
                EmptyExerciseState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.exercises, key = { it.id }) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onFavoriteClicked = { onFavoriteClicked(exercise) },
                            onClick = { onExerciseClick(exercise.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onFavoriteClicked: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onFavoriteClicked) {
                    Text(if (exercise.isFavorite) "Da luu" else "Luu")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(exercise.muscleGroup) })
                AssistChip(onClick = {}, label = { Text(exercise.equipment) })
                if (exercise.lastViewedAt != null) {
                    AssistChip(onClick = {}, label = { Text("Da xem") })
                }
            }
        }
    }
}

@Composable
private fun EmptyExerciseState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                text = "Khong co bai tap phu hop.",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
