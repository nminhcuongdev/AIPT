package nminhcuong.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptMetricRow
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.presentation.ProfileSetupUiState

@Composable
internal fun GymEquipmentScreen(
    state: ProfileSetupUiState,
    onEquipmentSwiped: (GymEquipment, Boolean) -> Unit,
    onResetEquipment: () -> Unit,
    onSaveProfile: () -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 05 / Equipment",
                    title = "Swipe your gym inventory",
                    description = "Swipe right for available, left for unavailable. The AI will avoid exercises that require missing equipment.",
                    trailing = { TextButton(onClick = onResetEquipment) { Text("Reset") } },
                )
                Spacer(Modifier.height(16.dp))
                AiptMetricRow(
                    listOf(
                        state.availableCount.toString() to "available",
                        state.unavailableCount.toString() to "blocked",
                        state.remainingCount.toString() to "left",
                    ),
                )
                Spacer(Modifier.height(18.dp))
                EquipmentSwipeSection(state, onEquipmentSwiped, onResetEquipment)
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp)) { Text("Back") }
                    Button(
                        onClick = {
                            onSaveProfile()
                            onFinish()
                        },
                        enabled = state.canSave,
                        modifier = Modifier.weight(1f).height(54.dp),
                    ) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}
