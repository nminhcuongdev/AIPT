package com.example.aipt.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptMetricRow
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.Volt
import kotlin.math.roundToInt

@Composable
fun BasicInfoRoute(onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    BasicInfoScreen(state, viewModel::onNameChanged, viewModel::onAgeChanged, viewModel::onHeightChanged, viewModel::onWeightChanged, onNext)
}

@Composable
fun InBodyRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    InBodyScreen(
        state = state,
        onBodyFatPercentChanged = viewModel::onBodyFatPercentChanged,
        onSkeletalMuscleMassChanged = viewModel::onSkeletalMuscleMassChanged,
        onBodyWaterLitersChanged = viewModel::onBodyWaterLitersChanged,
        onVisceralFatLevelChanged = viewModel::onVisceralFatLevelChanged,
        onBasalMetabolicRateChanged = viewModel::onBasalMetabolicRateChanged,
        onWaistHipRatioChanged = viewModel::onWaistHipRatioChanged,
        onLeftArmMuscleChanged = viewModel::onLeftArmMuscleChanged,
        onRightArmMuscleChanged = viewModel::onRightArmMuscleChanged,
        onTrunkMuscleChanged = viewModel::onTrunkMuscleChanged,
        onLeftLegMuscleChanged = viewModel::onLeftLegMuscleChanged,
        onRightLegMuscleChanged = viewModel::onRightLegMuscleChanged,
        onBack = onBack,
        onNext = onNext,
    )
}

@Composable
fun TrainingGoalRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    TrainingGoalScreen(state.selectedGoal, viewModel::onGoalSelected, onBack, onNext)
}


@Composable
fun PreferencesRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    PreferencesScreen(
        state = state,
        onDaysPerWeekChanged = viewModel::onDaysPerWeekChanged,
        onSessionDurationChanged = viewModel::onSessionDurationChanged,
        onExperienceLevelSelected = viewModel::onExperienceLevelSelected,
        onInjuriesChanged = viewModel::onInjuriesChanged,
        onPreferredLanguageSelected = viewModel::onPreferredLanguageSelected,
        onBack = onBack,
        onNext = onNext,
    )
}
@Composable
fun GymEquipmentRoute(onBack: () -> Unit, onFinish: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    GymEquipmentScreen(state, viewModel::onEquipmentSwiped, viewModel::onResetEquipment, viewModel::onSaveProfile, onBack, onFinish)
}

@Composable
private fun BasicInfoScreen(
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
                    OutlinedTextField(state.name, onNameChanged, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Your name") }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) })
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NumberField(state.age, onAgeChanged, "Age", Modifier.weight(1f))
                        NumberField(state.heightCm, onHeightChanged, "Height", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(14.dp))
                    NumberField(state.weightKg, onWeightChanged, "Weight kg", Modifier.fillMaxWidth())
                    Spacer(Modifier.height(22.dp))
                    Button(onClick = onNext, enabled = state.canSave, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text("Continue") }
                }
            }
        }
    }
}

@Composable
private fun InBodyScreen(
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
                AiptHeroHeader("Step 02 / InBody", "Decode body composition", "Enter InBody metrics if available. Any unknown field can be left blank.")
                Spacer(Modifier.height(18.dp))
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
                Spacer(Modifier.height(16.dp))
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
                Spacer(Modifier.height(18.dp))
                NavButtons(onBack, onNext)
            }
        }
    }
}

@Composable
private fun TrainingGoalScreen(selectedGoal: String, onGoalSelected: (String) -> Unit, onBack: () -> Unit, onNext: () -> Unit) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader("Step 03 / Goal", "Choose the north star", "This goal guides how the AI plans workouts and prioritizes exercises.")
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


@Composable
private fun PreferencesScreen(
    state: ProfileSetupUiState,
    onDaysPerWeekChanged: (String) -> Unit,
    onSessionDurationChanged: (String) -> Unit,
    onExperienceLevelSelected: (String) -> Unit,
    onInjuriesChanged: (String) -> Unit,
    onPreferredLanguageSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "Step 04 / Preferences",
                    title = "Set training constraints",
                    description = "Complete the workout-plan request with schedule, experience, limitations, and response language.",
                )
                Spacer(Modifier.height(22.dp))
                AiptPanel {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NumberField(state.daysPerWeek, onDaysPerWeekChanged, "Days / week", Modifier.weight(1f))
                        NumberField(state.sessionDurationMinutes, onSessionDurationChanged, "Minutes", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Experience level", style = MaterialTheme.typography.titleMedium, color = Ink900)
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ExperienceLevels.forEach { level ->
                            FilterChip(
                                selected = state.experienceLevel == level,
                                onClick = { onExperienceLevelSelected(level) },
                                label = { Text(level.replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Ink900, selectedLabelColor = Volt),
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.injuriesOrLimitations,
                        onValueChange = onInjuriesChanged,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Injuries or limitations") },
                        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Preferred response language", style = MaterialTheme.typography.titleMedium, color = Ink900)
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PreferredLanguages.forEach { language ->
                            FilterChip(
                                selected = state.preferredLanguage == language.code,
                                onClick = { onPreferredLanguageSelected(language.code) },
                                label = { Text(language.label) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Ink900, selectedLabelColor = Volt),
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
@Composable
private fun GymEquipmentScreen(
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
                AiptMetricRow(listOf(state.availableCount.toString() to "available", state.unavailableCount.toString() to "blocked", state.remainingCount.toString() to "left"))
                Spacer(Modifier.height(18.dp))
                EquipmentSwipeSection(state, onEquipmentSwiped)
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
                    ) { Text("Finish") }
                }
            }
        }
    }
}

@Composable
private fun NavButtons(onBack: () -> Unit, onNext: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp)) { Text("Back") }
        Button(onClick = onNext, modifier = Modifier.weight(1f).height(54.dp)) { Text("Continue") }
    }
}

@Composable
private fun NumberField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = modifier, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), label = { Text(label) }, leadingIcon = { Icon(numberFieldIcon(label), contentDescription = null) })
}

@Composable
private fun DecimalField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = modifier, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), label = { Text(label) }, leadingIcon = { Icon(decimalFieldIcon(label), contentDescription = null) })
}


private fun numberFieldIcon(label: String) = when {
    label.contains("Age", ignoreCase = true) -> Icons.Default.Person
    label.contains("Height", ignoreCase = true) -> Icons.Default.Height
    label.contains("Weight", ignoreCase = true) -> Icons.Default.MonitorWeight
    label.contains("BMR", ignoreCase = true) -> Icons.Default.LocalFireDepartment
    label.contains("Visceral", ignoreCase = true) -> Icons.Default.AccessibilityNew
    else -> Icons.Default.Numbers
}

private fun decimalFieldIcon(label: String) = when {
    label.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
    label.contains("arm", ignoreCase = true) || label.contains("leg", ignoreCase = true) || label.contains("Trunk", ignoreCase = true) -> Icons.Default.FitnessCenter
    label.contains("SMM", ignoreCase = true) -> Icons.Default.FitnessCenter
    label.contains("Body fat", ignoreCase = true) -> Icons.Default.MonitorWeight
    else -> Icons.Default.Numbers
}
@Composable
private fun EquipmentSwipeSection(state: ProfileSetupUiState, onEquipmentSwiped: (GymEquipment, Boolean) -> Unit) {
    val currentEquipment = state.currentEquipment
    if (currentEquipment == null) {
        AiptPanel {
            Text("Inventory complete", style = MaterialTheme.typography.titleLarge, color = Ink900)
            Spacer(Modifier.height(6.dp))
            Text("Tap Finish to save your profile and enter the exercise library.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        SwipeEquipmentCard(equipment = currentEquipment, onSwipe = { available -> onEquipmentSwiped(currentEquipment, available) })
    }
}

@Composable
private fun SwipeEquipmentCard(equipment: GymEquipment, onSwipe: (Boolean) -> Unit) {
    var offsetX by remember(equipment.id) { mutableFloatStateOf(0f) }
    val threshold = 160f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(equipment.id) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                        },
                        onDragEnd = {
                            when {
                                offsetX > threshold -> onSwipe(true)
                                offsetX < -threshold -> onSwipe(false)
                            }
                            offsetX = 0f
                        },
                    )
                },
            shape = RoundedCornerShape(34.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(model = equipment.imageUrl, contentDescription = equipment.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Ink900.copy(alpha = 0.9f)))))
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                    AiptPill(text = if (offsetX >= 0f) "AVAILABLE ->" else "<- UNAVAILABLE", containerColor = if (offsetX >= 0f) Volt else Ember, contentColor = Ink900)
                    Spacer(Modifier.height(14.dp))
                    Text(equipment.name, color = Bone, style = MaterialTheme.typography.headlineMedium)
                    Text("Swipe left or right", color = Bone.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedButton(onClick = { onSwipe(false) }, modifier = Modifier.weight(1f).height(52.dp)) { Text("Unavailable") }
            Button(onClick = { onSwipe(true) }, modifier = Modifier.weight(1f).height(52.dp)) { Text("Available") }
        }
    }
}



