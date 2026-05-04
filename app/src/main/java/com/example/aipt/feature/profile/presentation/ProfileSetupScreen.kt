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
import com.example.aipt.feature.profile.domain.model.GymEquipment
import kotlin.math.roundToInt

@Composable
fun BasicInfoRoute(
    onNext: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    BasicInfoScreen(
        state = state,
        onNameChanged = viewModel::onNameChanged,
        onAgeChanged = viewModel::onAgeChanged,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onNext = onNext,
    )
}

@Composable
fun InBodyRoute(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    InBodyScreen(
        state = state,
        onBodyFatPercentChanged = viewModel::onBodyFatPercentChanged,
        onSkeletalMuscleMassChanged = viewModel::onSkeletalMuscleMassChanged,
        onBodyWaterPercentChanged = viewModel::onBodyWaterPercentChanged,
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
fun TrainingGoalRoute(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    TrainingGoalScreen(
        selectedGoal = state.selectedGoal,
        onGoalSelected = viewModel::onGoalSelected,
        onBack = onBack,
        onNext = onNext,
    )
}

@Composable
fun GymEquipmentRoute(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    GymEquipmentScreen(
        state = state,
        onEquipmentSwiped = viewModel::onEquipmentSwiped,
        onResetEquipment = viewModel::onResetEquipment,
        onSaveProfile = viewModel::onSaveProfile,
        onBack = onBack,
        onFinish = onFinish,
    )
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
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            StepHeader(
                step = "Buoc 1/4",
                title = "Thong tin co ban",
                description = "AI trainer can cac thong tin nen tang de ca nhan hoa cuong do va muc tieu.",
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Ten cua ban") },
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(state.age, onAgeChanged, "Tuoi", Modifier.weight(1f))
                NumberField(state.heightCm, onHeightChanged, "Chieu cao cm", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            NumberField(state.weightKg, onWeightChanged, "Can nang kg", Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNext, enabled = state.canSave, modifier = Modifier.fillMaxWidth()) {
                Text("Tiep tuc")
            }
        }
    }
}

@Composable
private fun InBodyScreen(
    state: ProfileSetupUiState,
    onBodyFatPercentChanged: (String) -> Unit,
    onSkeletalMuscleMassChanged: (String) -> Unit,
    onBodyWaterPercentChanged: (String) -> Unit,
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
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            StepHeader(
                step = "Buoc 2/4",
                title = "Chi so InBody",
                description = "Nhap body composition neu co ket qua InBody. Co the bo trong chi so chua co.",
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Tong quan co the", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DecimalField(state.bodyFatPercent, onBodyFatPercentChanged, "Body fat %", Modifier.weight(1f))
                DecimalField(state.skeletalMuscleMassKg, onSkeletalMuscleMassChanged, "SMM kg", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DecimalField(state.bodyWaterPercent, onBodyWaterPercentChanged, "Body water %", Modifier.weight(1f))
                NumberField(state.visceralFatLevel, onVisceralFatLevelChanged, "Visceral fat", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(state.basalMetabolicRateKcal, onBasalMetabolicRateChanged, "BMR kcal", Modifier.weight(1f))
                DecimalField(state.waistHipRatio, onWaistHipRatioChanged, "Waist hip ratio", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Segmental muscle mass kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DecimalField(state.leftArmMuscleKg, onLeftArmMuscleChanged, "Left arm", Modifier.weight(1f))
                DecimalField(state.rightArmMuscleKg, onRightArmMuscleChanged, "Right arm", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            DecimalField(state.trunkMuscleKg, onTrunkMuscleChanged, "Trunk", Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DecimalField(state.leftLegMuscleKg, onLeftLegMuscleChanged, "Left leg", Modifier.weight(1f))
                DecimalField(state.rightLegMuscleKg, onRightLegMuscleChanged, "Right leg", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Quay lai") }
                Button(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Tiep tuc") }
            }
        }
    }
}

@Composable
private fun TrainingGoalScreen(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            StepHeader(
                step = "Buoc 3/4",
                title = "Muc tieu tap luyen",
                description = "Chon muc tieu chinh de goi y bai tap, volume va tien trinh phu hop.",
            )
            Spacer(modifier = Modifier.height(24.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TrainingGoals.forEach { goal ->
                    FilterChip(selected = selectedGoal == goal, onClick = { onGoalSelected(goal) }, label = { Text(goal) })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Quay lai") }
                Button(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Tiep tuc") }
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
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            StepHeader(
                step = "Buoc 4/4",
                title = "Thiet bi phong gym",
                description = "Vuot phai neu co san, vuot trai neu khong co. Du lieu nay giup AI tranh goi y bai tap khong thuc hien duoc.",
            )
            Spacer(modifier = Modifier.height(16.dp))
            EquipmentSwipeSection(state, onEquipmentSwiped, onResetEquipment)
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Quay lai") }
                Button(
                    onClick = {
                        onSaveProfile()
                        onFinish()
                    },
                    enabled = state.canSave,
                    modifier = Modifier.weight(1f),
                ) { Text("Hoan tat") }
            }
        }
    }
}

@Composable
private fun StepHeader(step: String, title: String, description: String) {
    Text(text = step, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun NumberField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(label) },
    )
}

@Composable
private fun DecimalField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = { Text(label) },
    )
}

@Composable
private fun EquipmentSwipeSection(
    state: ProfileSetupUiState,
    onEquipmentSwiped: (GymEquipment, Boolean) -> Unit,
    onResetEquipment: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Swipe thiet bi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Available: vuot phai. Unavailable: vuot trai.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = onResetEquipment) { Text("Lam lai") }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryPill("Co san: ${state.availableCount}")
        SummaryPill("Khong co: ${state.unavailableCount}")
        SummaryPill("Con lai: ${state.remainingCount}")
    }
    Spacer(modifier = Modifier.height(16.dp))
    val currentEquipment = state.currentEquipment
    if (currentEquipment == null) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Da phan loai xong thiet bi.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Bam Hoan tat de luu profile va vao thu vien bai tap.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        SwipeEquipmentCard(equipment = currentEquipment, onSwipe = { available -> onEquipmentSwiped(currentEquipment, available) })
    }
}

@Composable
private fun SummaryPill(text: String) {
    Surface(shape = RoundedCornerShape(100.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Text(text = text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), style = MaterialTheme.typography.labelMedium)
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
                .height(390.dp)
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
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(model = equipment.imageUrl, contentDescription = equipment.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)))),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                    Text(equipment.name, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (offsetX >= 0f) "Keo sang phai neu co san" else "Keo sang trai neu khong co",
                        color = Color.White.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { onSwipe(false) }, modifier = Modifier.size(width = 150.dp, height = 48.dp)) { Text("Unavailable") }
            Button(onClick = { onSwipe(true) }, modifier = Modifier.size(width = 150.dp, height = 48.dp)) { Text("Available") }
        }
    }
}
