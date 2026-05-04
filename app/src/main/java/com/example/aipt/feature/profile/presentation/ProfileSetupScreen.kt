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
import androidx.compose.ui.draw.clip
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
fun ProfileSetupRoute(
    onContinue: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    ProfileSetupScreen(
        state = state,
        onNameChanged = viewModel::onNameChanged,
        onAgeChanged = viewModel::onAgeChanged,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onGoalSelected = viewModel::onGoalSelected,
        onEquipmentSwiped = viewModel::onEquipmentSwiped,
        onResetEquipment = viewModel::onResetEquipment,
        onSaveProfile = viewModel::onSaveProfile,
        onContinue = onContinue,
    )
}

@Composable
private fun ProfileSetupScreen(
    state: ProfileSetupUiState,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onGoalSelected: (String) -> Unit,
    onEquipmentSwiped: (GymEquipment, Boolean) -> Unit,
    onResetEquipment: () -> Unit,
    onSaveProfile: () -> Unit,
    onContinue: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                text = "Ho so tap luyen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Nhap thong tin co ban de AI trainer ca nhan hoa bai tap theo muc tieu va thiet bi gym cua ban.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(20.dp))
            BasicInfoSection(
                state = state,
                onNameChanged = onNameChanged,
                onAgeChanged = onAgeChanged,
                onHeightChanged = onHeightChanged,
                onWeightChanged = onWeightChanged,
            )
            Spacer(modifier = Modifier.height(24.dp))
            GoalSection(
                selectedGoal = state.selectedGoal,
                onGoalSelected = onGoalSelected,
            )
            Spacer(modifier = Modifier.height(24.dp))
            EquipmentSwipeSection(
                state = state,
                onEquipmentSwiped = onEquipmentSwiped,
                onResetEquipment = onResetEquipment,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSaveProfile,
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Luu profile")
            }
            if (state.saved) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Tiep tuc vao thu vien bai tap")
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                    Text("Bo qua va vao thu vien bai tap")
                }
            }
        }
    }
}

@Composable
private fun BasicInfoSection(
    state: ProfileSetupUiState,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
) {
    Text(
        text = "Thong tin co ban",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = state.name,
        onValueChange = onNameChanged,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Ten cua ban") },
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        NumberField(
            value = state.age,
            onValueChange = onAgeChanged,
            label = "Tuoi",
            modifier = Modifier.weight(1f),
        )
        NumberField(
            value = state.heightCm,
            onValueChange = onHeightChanged,
            label = "Chieu cao cm",
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    NumberField(
        value = state.weightKg,
        onValueChange = onWeightChanged,
        label = "Can nang kg",
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
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
private fun GoalSection(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
) {
    Text(
        text = "Muc tieu tap luyen",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(modifier = Modifier.height(12.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrainingGoals.forEach { goal ->
            FilterChip(
                selected = selectedGoal == goal,
                onClick = { onGoalSelected(goal) },
                label = { Text(goal) },
            )
        }
    }
}

@Composable
private fun EquipmentSwipeSection(
    state: ProfileSetupUiState,
    onEquipmentSwiped: (GymEquipment, Boolean) -> Unit,
    onResetEquipment: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Thiet bi phong gym",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Vuot phai: available. Vuot trai: unavailable.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onResetEquipment) {
            Text("Lam lai")
        }
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
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Da phan loai xong thiet bi.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Ban co the luu profile hoac lam lai danh sach thiet bi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    } else {
        SwipeEquipmentCard(
            equipment = currentEquipment,
            onSwipe = { available -> onEquipmentSwiped(currentEquipment, available) },
        )
    }
}

@Composable
private fun SummaryPill(text: String) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun SwipeEquipmentCard(
    equipment: GymEquipment,
    onSwipe: (Boolean) -> Unit,
) {
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
                AsyncImage(
                    model = equipment.imageUrl,
                    contentDescription = equipment.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
                ) {
                    Text(
                        text = equipment.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
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
            OutlinedButton(onClick = { onSwipe(false) }, modifier = Modifier.size(width = 150.dp, height = 48.dp)) {
                Text("Unavailable")
            }
            Button(onClick = { onSwipe(true) }, modifier = Modifier.size(width = 150.dp, height = 48.dp)) {
                Text("Available")
            }
        }
    }
}
