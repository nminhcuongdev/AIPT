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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

@Composable
internal fun BasicInfoScreen(
    state: ProfileSetupUiState,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 01 / Profile",
                    title = "Build your training baseline",
                    description = "Enter your baseline so the AI trainer can tune intensity, volume, and progression.",
                )
                Spacer(Modifier.height(22.dp))
                AiptPanel {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Your name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NumberField(state.age, onAgeChanged, "Age", Modifier.weight(1f))
                        NumberField(state.heightCm, onHeightChanged, "Height", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(14.dp))
                    NumberField(state.weightKg, onWeightChanged, "Weight kg", Modifier.fillMaxWidth())
                    Spacer(Modifier.height(22.dp))
                    Button(onClick = onNext, enabled = state.canSave, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}
