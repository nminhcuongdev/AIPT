package nminhcuong.aipt.feature.exercise.presentation.list.components

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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptMetricRow
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.exercise.domain.model.Exercise
import nminhcuong.aipt.feature.exercise.presentation.list.ExerciseListUiState
import nminhcuong.aipt.ui.theme.Bone
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Sea
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun ExerciseListScreen(
    state: ExerciseListUiState,
    onProfileClick: () -> Unit,
    onWorkoutPlanClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMuscleGroupSelected: (String) -> Unit,
    onFavoritesOnlyChanged: () -> Unit,
    onFavoriteClicked: (Exercise) -> Unit,
    onExerciseClick: (Int) -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
//            ExerciseListHeader(
//                onProfileClick = onProfileClick,
//                onWorkoutPlanClick = onWorkoutPlanClick,
//                onProgressClick = onProgressClick,
//            )
            Spacer(Modifier.height(16.dp))
            AiptMetricRow(
                listOf(
                    state.exercises.size.toString() to "shown",
                    state.muscleGroups.drop(1).size.toString() to "groups",
                    state.exercises.count { it.isFavorite }.toString() to "saved",
                ),
            )
            Spacer(Modifier.height(16.dp))
            ExerciseFilterPanel(
                state = state,
                onSearchQueryChanged = onSearchQueryChanged,
                onMuscleGroupSelected = onMuscleGroupSelected,
                onFavoritesOnlyChanged = onFavoritesOnlyChanged,
            )
            Spacer(Modifier.height(14.dp))
            ExerciseResults(
                state = state,
                onFavoriteClicked = onFavoriteClicked,
                onExerciseClick = onExerciseClick,
            )
        }
    }
}

@Composable
private fun ExerciseListHeader(
    onProfileClick: () -> Unit,
    onWorkoutPlanClick: () -> Unit,
    onProgressClick: () -> Unit,
) {
    AiptHeroHeader(
        eyebrow = "AIPT Library",
        title = "",
        description = "Search, filter, and save movements that match your profile and available equipment.",
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onProfileClick) { Text("Profile") }
                TextButton(onClick = onWorkoutPlanClick) { Text("Create plan") }
                TextButton(onClick = onProgressClick) { Text("Track progress") }
            }
        },
    )
}

@Composable
private fun ExerciseFilterPanel(
    state: ExerciseListUiState,
    onSearchQueryChanged: (String) -> Unit,
    onMuscleGroupSelected: (String) -> Unit,
    onFavoritesOnlyChanged: () -> Unit,
) {
    AiptPanel {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Search exercise") },
            placeholder = { Text("squat, chest, dumbbell") },
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = state.showFavoritesOnly,
                    onClick = onFavoritesOnlyChanged,
                    label = { Text("Saved") },
                    colors = filterColors(),
                )
            }
            items(state.muscleGroups) { muscleGroup ->
                FilterChip(
                    selected = state.selectedMuscleGroup == muscleGroup,
                    onClick = { onMuscleGroupSelected(muscleGroup) },
                    label = { Text(if (muscleGroup == "All") "All" else muscleGroup) },
                    colors = filterColors(),
                )
            }
        }
    }
}

@Composable
private fun ExerciseResults(
    state: ExerciseListUiState,
    onFavoriteClicked: (Exercise) -> Unit,
    onExerciseClick: (Int) -> Unit,
) {
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

@Composable
private fun filterColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Ink900,
    selectedLabelColor = Volt,
)

@Composable
private fun ExerciseCard(exercise: Exercise, onFavoriteClicked: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.large, color = Ink900) {
                    Text(
                        text = exercise.muscleGroup.take(2).uppercase(),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        color = Volt,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.name, style = MaterialTheme.typography.titleLarge, color = Ink900)
                    Text(exercise.equipment, style = MaterialTheme.typography.bodyMedium, color = Sea, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onFavoriteClicked) { Text(if (exercise.isFavorite) "Saved" else "Save") }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(exercise.muscleGroup) })
                if (exercise.lastViewedAt != null) {
                    AssistChip(onClick = {}, label = { Text("Viewed") })
                }
            }
        }
    }
}

@Composable
private fun EmptyExerciseState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(color = Ink900, shape = MaterialTheme.shapes.extraLarge) {
            Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No matching movement", style = MaterialTheme.typography.titleLarge, color = Bone)
                Text("Try a different search or clear filters.", style = MaterialTheme.typography.bodyMedium, color = Bone.copy(alpha = 0.72f))
            }
        }
    }
}
