package com.example.aipt.feature.profile.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.presentation.ProfileSetupUiState
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Volt
import kotlin.math.roundToInt

@Composable
internal fun EquipmentSwipeSection(
    state: ProfileSetupUiState,
    onEquipmentSwiped: (GymEquipment, Boolean) -> Unit,
) {
    val currentEquipment = state.currentEquipment
    if (currentEquipment == null) {
        AiptPanel {
            Text("Inventory complete", style = MaterialTheme.typography.titleLarge, color = Ink900)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tap Finish to save your profile and create your workout plan.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        SwipeEquipmentCard(
            equipment = currentEquipment,
            onSwipe = { available -> onEquipmentSwiped(currentEquipment, available) },
        )
    }
}

@Composable
private fun SwipeEquipmentCard(equipment: GymEquipment, onSwipe: (Boolean) -> Unit) {
    var offsetX by remember(equipment.id) { mutableFloatStateOf(0f) }
    val threshold = 160f
    val context = LocalContext.current
    val imageRequest = remember(context, equipment.id, equipment.imageUrl) {
        ImageRequest.Builder(context)
            .data(equipment.imageUrl)
            .crossfade(true)
            .memoryCacheKey("${equipment.id}:${equipment.imageUrl}")
            .diskCacheKey(equipment.imageUrl)
            .build()
    }

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
                key(equipment.id, equipment.imageUrl) {
                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = equipment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { EquipmentImagePlaceholder(equipment.name) },
                        error = { EquipmentImagePlaceholder(equipment.name) },
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Ink900.copy(alpha = 0.9f)))),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                    AiptPill(
                        text = if (offsetX >= 0f) "AVAILABLE ->" else "<- UNAVAILABLE",
                        containerColor = if (offsetX >= 0f) Volt else Ember,
                        contentColor = Ink900,
                    )
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

@Composable
private fun EquipmentImagePlaceholder(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Ink900, Color(0xFF24313A)))),
    ) {
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp),
        ) {
            drawGeneratedEquipment(name)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
        ) {
            AiptPill("Generated visual", containerColor = Volt, contentColor = Ink900)
            Spacer(Modifier.height(10.dp))
            Text(name, style = MaterialTheme.typography.headlineMedium, color = Bone)
        }
    }
}

private fun DrawScope.drawGeneratedEquipment(name: String) {
    val key = name.lowercase()
    val accent = Volt
    val soft = Bone.copy(alpha = 0.9f)
    when {
        "dumbbell" in key -> drawDumbbells(accent, soft)
        "barbell" in key -> drawBarbell(accent, soft)
        "kettlebell" in key -> drawKettlebell(accent, soft)
        "treadmill" in key -> drawTreadmill(accent, soft)
        "cable" in key -> drawCableMachine(accent, soft)
        "pull" in key -> drawPullUpBar(accent, soft)
        "bench" in key -> drawBench(accent, soft)
        "leg press" in key -> drawLegPress(accent, soft)
        "smith" in key -> drawSmithMachine(accent, soft)
        "resistance" in key -> drawResistanceBands(accent, soft)
        "medicine" in key -> drawMedicineBall(accent, soft)
        "rowing" in key || "rower" in key -> drawRowingMachine(accent, soft)
        else -> drawDumbbells(accent, soft)
    }
}

private fun DrawScope.drawDumbbells(accent: Color, soft: Color) {
    val cy = size.height * 0.5f
    drawLine(soft, Offset(size.width * 0.24f, cy), Offset(size.width * 0.76f, cy), strokeWidth = 14f, cap = StrokeCap.Round)
    listOf(0.18f, 0.28f, 0.72f, 0.82f).forEach { x ->
        drawRoundRect(accent, topLeft = Offset(size.width * x - 18f, cy - 54f), size = Size(36f, 108f), cornerRadius = CornerRadius(14f, 14f))
    }
}

private fun DrawScope.drawBarbell(accent: Color, soft: Color) {
    val cy = size.height * 0.5f
    drawLine(soft, Offset(size.width * 0.08f, cy), Offset(size.width * 0.92f, cy), strokeWidth = 10f, cap = StrokeCap.Round)
    listOf(0.14f, 0.22f, 0.78f, 0.86f).forEach { x ->
        drawRoundRect(accent, topLeft = Offset(size.width * x - 12f, cy - 68f), size = Size(24f, 136f), cornerRadius = CornerRadius(8f, 8f))
    }
}

private fun DrawScope.drawKettlebell(accent: Color, soft: Color) {
    drawArc(soft, 200f, 140f, false, topLeft = Offset(size.width * 0.34f, size.height * 0.18f), size = Size(size.width * 0.32f, size.height * 0.32f), style = Stroke(width = 16f, cap = StrokeCap.Round))
    drawCircle(accent, radius = size.minDimension * 0.26f, center = Offset(size.width * 0.5f, size.height * 0.58f))
    drawCircle(Ink900, radius = size.minDimension * 0.09f, center = Offset(size.width * 0.5f, size.height * 0.58f))
}

private fun DrawScope.drawTreadmill(accent: Color, soft: Color) {
    drawRoundRect(accent, topLeft = Offset(size.width * 0.18f, size.height * 0.62f), size = Size(size.width * 0.64f, 34f), cornerRadius = CornerRadius(18f, 18f))
    drawLine(soft, Offset(size.width * 0.28f, size.height * 0.62f), Offset(size.width * 0.46f, size.height * 0.32f), strokeWidth = 10f, cap = StrokeCap.Round)
    drawRoundRect(soft, topLeft = Offset(size.width * 0.42f, size.height * 0.25f), size = Size(size.width * 0.28f, 48f), cornerRadius = CornerRadius(14f, 14f))
}

private fun DrawScope.drawCableMachine(accent: Color, soft: Color) {
    drawRoundRect(soft, topLeft = Offset(size.width * 0.2f, size.height * 0.16f), size = Size(22f, size.height * 0.66f), cornerRadius = CornerRadius(10f, 10f))
    drawRoundRect(soft, topLeft = Offset(size.width * 0.72f, size.height * 0.16f), size = Size(22f, size.height * 0.66f), cornerRadius = CornerRadius(10f, 10f))
    drawLine(accent, Offset(size.width * 0.24f, size.height * 0.24f), Offset(size.width * 0.72f, size.height * 0.24f), strokeWidth = 10f, cap = StrokeCap.Round)
    drawLine(accent, Offset(size.width * 0.5f, size.height * 0.24f), Offset(size.width * 0.5f, size.height * 0.58f), strokeWidth = 7f, cap = StrokeCap.Round)
    drawCircle(accent, radius = 18f, center = Offset(size.width * 0.5f, size.height * 0.64f))
}

private fun DrawScope.drawPullUpBar(accent: Color, soft: Color) {
    drawLine(accent, Offset(size.width * 0.18f, size.height * 0.28f), Offset(size.width * 0.82f, size.height * 0.28f), strokeWidth = 16f, cap = StrokeCap.Round)
    drawLine(soft, Offset(size.width * 0.26f, size.height * 0.28f), Offset(size.width * 0.26f, size.height * 0.78f), strokeWidth = 10f, cap = StrokeCap.Round)
    drawLine(soft, Offset(size.width * 0.74f, size.height * 0.28f), Offset(size.width * 0.74f, size.height * 0.78f), strokeWidth = 10f, cap = StrokeCap.Round)
}

private fun DrawScope.drawBench(accent: Color, soft: Color) {
    drawRoundRect(accent, topLeft = Offset(size.width * 0.22f, size.height * 0.42f), size = Size(size.width * 0.56f, 42f), cornerRadius = CornerRadius(16f, 16f))
    drawLine(soft, Offset(size.width * 0.34f, size.height * 0.58f), Offset(size.width * 0.28f, size.height * 0.78f), strokeWidth = 10f, cap = StrokeCap.Round)
    drawLine(soft, Offset(size.width * 0.66f, size.height * 0.58f), Offset(size.width * 0.72f, size.height * 0.78f), strokeWidth = 10f, cap = StrokeCap.Round)
}

private fun DrawScope.drawLegPress(accent: Color, soft: Color) {
    drawLine(soft, Offset(size.width * 0.26f, size.height * 0.76f), Offset(size.width * 0.76f, size.height * 0.26f), strokeWidth = 14f, cap = StrokeCap.Round)
    drawRoundRect(accent, topLeft = Offset(size.width * 0.2f, size.height * 0.62f), size = Size(size.width * 0.28f, 40f), cornerRadius = CornerRadius(16f, 16f))
    drawRoundRect(accent, topLeft = Offset(size.width * 0.62f, size.height * 0.18f), size = Size(42f, size.height * 0.28f), cornerRadius = CornerRadius(14f, 14f))
}

private fun DrawScope.drawSmithMachine(accent: Color, soft: Color) {
    drawLine(soft, Offset(size.width * 0.25f, size.height * 0.18f), Offset(size.width * 0.25f, size.height * 0.82f), strokeWidth = 12f, cap = StrokeCap.Round)
    drawLine(soft, Offset(size.width * 0.75f, size.height * 0.18f), Offset(size.width * 0.75f, size.height * 0.82f), strokeWidth = 12f, cap = StrokeCap.Round)
    drawLine(accent, Offset(size.width * 0.18f, size.height * 0.42f), Offset(size.width * 0.82f, size.height * 0.42f), strokeWidth = 12f, cap = StrokeCap.Round)
    drawCircle(accent, 18f, Offset(size.width * 0.2f, size.height * 0.42f))
    drawCircle(accent, 18f, Offset(size.width * 0.8f, size.height * 0.42f))
}

private fun DrawScope.drawResistanceBands(accent: Color, soft: Color) {
    drawArc(accent, 20f, 300f, false, topLeft = Offset(size.width * 0.22f, size.height * 0.2f), size = Size(size.width * 0.56f, size.height * 0.56f), style = Stroke(width = 13f, cap = StrokeCap.Round))
    drawArc(soft, 200f, 260f, false, topLeft = Offset(size.width * 0.32f, size.height * 0.32f), size = Size(size.width * 0.36f, size.height * 0.36f), style = Stroke(width = 11f, cap = StrokeCap.Round))
}

private fun DrawScope.drawMedicineBall(accent: Color, soft: Color) {
    drawCircle(accent, radius = size.minDimension * 0.3f, center = Offset(size.width * 0.5f, size.height * 0.52f))
    drawLine(Ink900.copy(alpha = 0.55f), Offset(size.width * 0.33f, size.height * 0.35f), Offset(size.width * 0.67f, size.height * 0.69f), strokeWidth = 8f, cap = StrokeCap.Round)
    drawCircle(soft, radius = 10f, center = Offset(size.width * 0.42f, size.height * 0.45f))
    drawCircle(soft, radius = 10f, center = Offset(size.width * 0.58f, size.height * 0.58f))
}

private fun DrawScope.drawRowingMachine(accent: Color, soft: Color) {
    drawLine(accent, Offset(size.width * 0.18f, size.height * 0.68f), Offset(size.width * 0.82f, size.height * 0.68f), strokeWidth = 12f, cap = StrokeCap.Round)
    drawCircle(soft, radius = 34f, center = Offset(size.width * 0.22f, size.height * 0.56f))
    drawRoundRect(soft, topLeft = Offset(size.width * 0.48f, size.height * 0.52f), size = Size(70f, 34f), cornerRadius = CornerRadius(14f, 14f))
    drawLine(soft, Offset(size.width * 0.52f, size.height * 0.52f), Offset(size.width * 0.68f, size.height * 0.34f), strokeWidth = 8f, cap = StrokeCap.Round)
}
