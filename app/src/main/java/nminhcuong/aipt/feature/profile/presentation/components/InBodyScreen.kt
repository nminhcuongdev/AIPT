package nminhcuong.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.profile.presentation.ProfileSetupUiState
import nminhcuong.aipt.ui.theme.Ink900

@Composable
internal fun InBodyScreen(
    state: ProfileSetupUiState,
    onBodyFatPercentChanged: (String) -> Unit,
    onSkeletalMuscleMassChanged: (String) -> Unit,
    onBodyWaterLitersChanged: (String) -> Unit,
    onVisceralFatLevelChanged: (String) -> Unit,
    onBasalMetabolicRateChanged: (String) -> Unit,
    onWaistHipRatioChanged: (String) -> Unit,
    onLeftArmMuscleChanged: (String) -> Unit,
    onRightArmMuscleChanged: (String) -> Unit,
    onTrunkMuscleChanged: (String) -> Unit,
    onLeftLegMuscleChanged: (String) -> Unit,
    onRightLegMuscleChanged: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 02 / InBody",
                    title = "Decode body composition",
                    description = "Enter InBody metrics if available. Any unknown field can be left blank.",
                )
                Spacer(Modifier.height(18.dp))
                BodyCompositionPanel(
                    state = state,
                    onBodyFatPercentChanged = onBodyFatPercentChanged,
                    onSkeletalMuscleMassChanged = onSkeletalMuscleMassChanged,
                    onBodyWaterLitersChanged = onBodyWaterLitersChanged,
                    onVisceralFatLevelChanged = onVisceralFatLevelChanged,
                    onBasalMetabolicRateChanged = onBasalMetabolicRateChanged,
                    onWaistHipRatioChanged = onWaistHipRatioChanged,
                )
                Spacer(Modifier.height(16.dp))
                SegmentalMusclePanel(
                    state = state,
                    onLeftArmMuscleChanged = onLeftArmMuscleChanged,
                    onRightArmMuscleChanged = onRightArmMuscleChanged,
                    onTrunkMuscleChanged = onTrunkMuscleChanged,
                    onLeftLegMuscleChanged = onLeftLegMuscleChanged,
                    onRightLegMuscleChanged = onRightLegMuscleChanged,
                )
                Spacer(Modifier.height(18.dp))
                NavButtons(onBack, onNext)
            }
        }
    }
}

@Composable
private fun BodyCompositionPanel(
    state: ProfileSetupUiState,
    onBodyFatPercentChanged: (String) -> Unit,
    onSkeletalMuscleMassChanged: (String) -> Unit,
    onBodyWaterLitersChanged: (String) -> Unit,
    onVisceralFatLevelChanged: (String) -> Unit,
    onBasalMetabolicRateChanged: (String) -> Unit,
    onWaistHipRatioChanged: (String) -> Unit,
) {
    AiptPanel {
        Text("Composition", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DecimalField(state.bodyFatPercent, onBodyFatPercentChanged, "Body fat %", Modifier.weight(1f))
            DecimalField(state.skeletalMuscleMassKg, onSkeletalMuscleMassChanged, "SMM kg", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DecimalField(state.bodyWaterLiters, onBodyWaterLitersChanged, "Water L", Modifier.weight(1f))
            NumberField(state.visceralFatLevel, onVisceralFatLevelChanged, "Visceral", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberField(state.basalMetabolicRateKcal, onBasalMetabolicRateChanged, "BMR", Modifier.weight(1f))
            DecimalField(state.waistHipRatio, onWaistHipRatioChanged, "WHR", Modifier.weight(1f))
        }
    }
}

@Composable
private fun SegmentalMusclePanel(
    state: ProfileSetupUiState,
    onLeftArmMuscleChanged: (String) -> Unit,
    onRightArmMuscleChanged: (String) -> Unit,
    onTrunkMuscleChanged: (String) -> Unit,
    onLeftLegMuscleChanged: (String) -> Unit,
    onRightLegMuscleChanged: (String) -> Unit,
) {
    AiptPanel {
        Text("Segmental muscle kg", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DecimalField(state.leftArmMuscleKg, onLeftArmMuscleChanged, "Left arm", Modifier.weight(1f))
            DecimalField(state.rightArmMuscleKg, onRightArmMuscleChanged, "Right arm", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        DecimalField(state.trunkMuscleKg, onTrunkMuscleChanged, "Trunk", Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DecimalField(state.leftLegMuscleKg, onLeftLegMuscleChanged, "Left leg", Modifier.weight(1f))
            DecimalField(state.rightLegMuscleKg, onRightLegMuscleChanged, "Right leg", Modifier.weight(1f))
        }
    }
}
