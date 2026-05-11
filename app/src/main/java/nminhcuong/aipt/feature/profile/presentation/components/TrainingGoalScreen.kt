package nminhcuong.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.profile.presentation.TrainingGoals
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun TrainingGoalScreen(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 03 / Goal",
                    title = "Choose the north star",
                    description = "This goal guides how the AI plans workouts and prioritizes exercises.",
                )
                Spacer(Modifier.height(22.dp))
                AiptPanel {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrainingGoals.forEach { goal ->
                            FilterChip(
                                selected = selectedGoal == goal.label,
                                onClick = { onGoalSelected(goal.label) },
                                label = { Text(goal.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Ink900,
                                    selectedLabelColor = Volt,
                                ),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                NavButtons(onBack, onNext)
            }
        }
    }
}
