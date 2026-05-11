package nminhcuong.aipt.feature.workout.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptPill
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import nminhcuong.aipt.feature.workout.presentation.BodyMetricChartPointUiState
import nminhcuong.aipt.feature.workout.presentation.ChartPointUiState
import nminhcuong.aipt.feature.workout.presentation.ProgressChartsUiState
import nminhcuong.aipt.feature.workout.presentation.ProgressExerciseLogUiState
import nminhcuong.aipt.feature.workout.presentation.ProgressTrackingUiState
import nminhcuong.aipt.ui.theme.ActionBlue
import nminhcuong.aipt.ui.theme.BoneDark
import nminhcuong.aipt.ui.theme.Ember
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Sea
import nminhcuong.aipt.ui.theme.Steel
import nminhcuong.aipt.ui.theme.Volt
import java.text.DecimalFormat
import kotlin.math.max
@Composable
internal fun ProgressTrackingScreen(
    state: ProgressTrackingUiState,
    onBackClick: () -> Unit,
    onWeightKgChanged: (String, String) -> Unit,
    onSetsChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
    onAnalyzeDay: (Int) -> Unit,
    onConfirmDay: (Int) -> Unit,
    onExerciseChartSelected: (String) -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = "Progress Tracking",
                    title = "Track each training day",
                    description = "Log sets, reps, weight, weekly volume, and body composition trends from real training data.",
                )
                Spacer(Modifier.height(16.dp))
                ProgressChartsPanel(
                    charts = state.charts,
                    onExerciseChartSelected = onExerciseChartSelected,
                )
                Spacer(Modifier.height(16.dp))
                when {
                    state.isLoading && state.schedule.isEmpty() -> LoadingPlanPanel()
                    state.isPlanMissing -> MissingPlanPanel()
                    else -> ScheduleTrackingPanel(
                        state = state,
                        onWeightKgChanged = onWeightKgChanged,
                        onSetsChanged = onSetsChanged,
                        onRepsChanged = onRepsChanged,
                        onNoteChanged = onNoteChanged,
                        onAnalyzeDay = onAnalyzeDay,
                        onConfirmDay = onConfirmDay,
                    )
                }
                if (state.errorMessage != null) {
                    Spacer(Modifier.height(14.dp))
                    AiptPanel {
                        Text("Analysis failed", style = MaterialTheme.typography.titleLarge, color = Ember)
                        Spacer(Modifier.height(8.dp))
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProgressChartsPanel(
    charts: ProgressChartsUiState,
    onExerciseChartSelected: (String) -> Unit,
) {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Progress charts", style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Black)
            AiptPill("Volume = sets x reps x kg", containerColor = Volt, contentColor = Ink900)
        }
        Spacer(Modifier.height(12.dp))
        if (charts.exerciseOptions.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                charts.exerciseOptions.forEach { exercise ->
                    if (exercise == charts.selectedExerciseName) {
                        ElevatedAssistChip(onClick = { onExerciseChartSelected(exercise) }, label = { Text(exercise) })
                    } else {
                        AssistChip(onClick = { onExerciseChartSelected(exercise) }, label = { Text(exercise) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        LineChartBlock(
            title = charts.selectedExerciseName?.let { "Weight progression - $it" } ?: "Weight progression by exercise",
            emptyText = "No exercise weight logs yet.",
            points = charts.exerciseWeightPoints,
            color = Sea,
            suffix = " kg",
        )
        Spacer(Modifier.height(14.dp))
        BarChartBlock(
            title = "Weekly volume",
            emptyText = "No weekly volume yet. Save sets, reps, and weight to populate this chart.",
            points = charts.weeklyVolumePoints,
            color = ActionBlue,
            suffix = " kg",
        )
        Spacer(Modifier.height(14.dp))
        BodyMetricsChartBlock(points = charts.bodyMetricPoints)
    }
}

@Composable
private fun LoadingPlanPanel() {
    AiptPanel {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Column {
                Text("Creating workout blueprint", style = MaterialTheme.typography.titleLarge, color = Ink900)
                Text("Using your saved profile to prepare the tracking schedule.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MissingPlanPanel() {
    AiptPanel {
        Text("Workout plan required", style = MaterialTheme.typography.titleLarge, color = Ink900)
        Spacer(Modifier.height(8.dp))
        Text("Create a weekly training blueprint first, then return here to track each planned exercise.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ScheduleTrackingPanel(
    state: ProgressTrackingUiState,
    onWeightKgChanged: (String, String) -> Unit,
    onSetsChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
    onAnalyzeDay: (Int) -> Unit,
    onConfirmDay: (Int) -> Unit,
) {
    state.schedule.groupBy { it.day to it.dayTitle }.forEach { (day, exercises) ->
        val dayNumber = day.first
        val dayStatus = state.dayStatuses[dayNumber]
        AiptPanel {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AiptPill("Day $dayNumber", containerColor = Volt, contentColor = Ink900)
                AiptPill(day.second, containerColor = Ink900)
            }
            Spacer(Modifier.height(12.dp))
            exercises.forEach { exercise ->
                ExerciseTrackingRow(
                    exercise = exercise,
                    onWeightKgChanged = onWeightKgChanged,
                    onSetsChanged = onSetsChanged,
                    onRepsChanged = onRepsChanged,
                    onNoteChanged = onNoteChanged,
                )
                Spacer(Modifier.height(14.dp))
            }
            Button(
                onClick = { onAnalyzeDay(dayNumber) },
                enabled = state.canAnalyzeDay(dayNumber),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (dayStatus?.isLoading == true) CircularProgressIndicator(color = Ink900) else Text("Save day and update next week")
            }
            if (dayStatus?.errorMessage != null) {
                Spacer(Modifier.height(10.dp))
                Text(dayStatus.errorMessage, color = Ember)
            }
            if (dayStatus?.response != null) {
                Spacer(Modifier.height(12.dp))
                DayAnalysisPanel(
                    response = dayStatus.response,
                    isConfirmed = dayStatus.isConfirmed,
                    canConfirm = state.canConfirmDay(dayNumber),
                    onConfirm = { onConfirmDay(dayNumber) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun ExerciseTrackingRow(
    exercise: ProgressExerciseLogUiState,
    onWeightKgChanged: (String, String) -> Unit,
    onSetsChanged: (String, String) -> Unit,
    onRepsChanged: (String, String) -> Unit,
    onNoteChanged: (String, String) -> Unit,
) {
    Column {
        Text(exercise.exerciseName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Ink900)
        if (exercise.plannedPrescription.isNotBlank()) {
            Text(exercise.plannedPrescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (exercise.equipment.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                exercise.equipment.forEach { item -> AssistChip(onClick = {}, label = { Text(item) }) }
            }
        }
        Spacer(Modifier.height(10.dp))
        DecimalField(
            value = exercise.weightKg,
            onValueChange = { onWeightKgChanged(exercise.id, it) },
            label = "Weight kg",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NumberField(
                value = exercise.sets,
                onValueChange = { onSetsChanged(exercise.id, it) },
                label = "Sets",
                modifier = Modifier.weight(1f),
            )
            NumberField(
                value = exercise.reps,
                onValueChange = { onRepsChanged(exercise.id, it) },
                label = "Reps",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = exercise.note,
            onValueChange = { onNoteChanged(exercise.id, it) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            label = { Text("Note") },
            leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
        )
    }
}

@Composable
private fun DayAnalysisPanel(
    response: WorkoutDayProgressAnalysisResponse,
    isConfirmed: Boolean,
    canConfirm: Boolean,
    onConfirm: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AiptPill("Day updated", containerColor = Sea, contentColor = Ink900)
        response.model?.let { AssistChip(onClick = {}, label = { Text(it) }) }
    }
    Spacer(Modifier.height(10.dp))
    response.analysisSummary?.takeIf { it.isNotBlank() }?.let {
        Text(it, style = MaterialTheme.typography.bodyLarge, color = Ink900)
        Spacer(Modifier.height(10.dp))
    }
    response.advice?.takeIf { it.isNotBlank() }?.let {
        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
    }
    AdviceList("Recommendations", response.recommendations, Sea)
    AdviceList("Next steps", response.nextSteps, Ink900)
    AdviceList("Safety notes", response.safetyNotes, Ember)
    if (response.nextWeekDay != null) {
        Spacer(Modifier.height(8.dp))
        if (isConfirmed) {
            Text("Confirmed and saved to workout schedule.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("API suggested a next-week schedule for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text("Confirm and save this day")
            }
        }
    }
}

@Composable
private fun AdviceList(title: String, items: List<String>?, accent: Color) {
    if (items.isNullOrEmpty()) return
    Text(title, style = MaterialTheme.typography.titleMedium, color = accent)
    Spacer(Modifier.height(6.dp))
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Text("- $item", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun LineChartBlock(
    title: String,
    emptyText: String,
    points: List<ChartPointUiState>,
    color: Color,
    suffix: String,
) {
    ChartShell(title = title, emptyText = emptyText, hasData = points.isNotEmpty()) {
        val axisColor = BoneDark
        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val bounds = PlotBounds(width = size.width, height = size.height)
            drawAxes(bounds, axisColor)
            if (points.size == 1) {
                val y = bounds.yFor(points.first().value, points.first().value, points.first().value)
                drawCircle(color = color, radius = 6f, center = Offset(bounds.left, y))
            } else {
                val minValue = points.minOf { it.value }
                val maxValue = points.maxOf { it.value }
                val step = bounds.plotWidth / (points.lastIndex).coerceAtLeast(1)
                val path = Path()
                points.forEachIndexed { index, point ->
                    val x = bounds.left + step * index
                    val y = bounds.yFor(point.value, minValue, maxValue)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = color, style = Stroke(width = 5f, cap = StrokeCap.Round))
                points.forEachIndexed { index, point ->
                    drawCircle(color = color, radius = 5f, center = Offset(bounds.left + step * index, bounds.yFor(point.value, minValue, maxValue)))
                }
            }
        }
        ChartFooter(points = points, suffix = suffix, textColor = textColor)
    }
}

@Composable
private fun BarChartBlock(
    title: String,
    emptyText: String,
    points: List<ChartPointUiState>,
    color: Color,
    suffix: String,
) {
    ChartShell(title = title, emptyText = emptyText, hasData = points.isNotEmpty()) {
        val axisColor = BoneDark
        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val bounds = PlotBounds(width = size.width, height = size.height)
            drawAxes(bounds, axisColor)
            val maxValue = points.maxOf { it.value }.coerceAtLeast(1.0)
            val slot = bounds.plotWidth / points.size.coerceAtLeast(1)
            val barWidth = max(10f, slot * 0.58f)
            points.forEachIndexed { index, point ->
                val barHeight = ((point.value / maxValue) * bounds.plotHeight).toFloat()
                val left = bounds.left + slot * index + (slot - barWidth) / 2f
                drawRoundRect(
                    color = color,
                    topLeft = Offset(left, bounds.bottom - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f),
                )
            }
        }
        ChartFooter(points = points, suffix = suffix, textColor = textColor)
    }
}

@Composable
private fun BodyMetricsChartBlock(points: List<BodyMetricChartPointUiState>) {
    val series = listOf(
        BodyMetricSeries("Weight", Sea) { it.weightKg },
        BodyMetricSeries("Body fat", Ember) { it.bodyFatPercent },
        BodyMetricSeries("SMM", ActionBlue) { it.skeletalMuscleMassKg },
    )
    ChartShell(title = "Body metrics over time", emptyText = "Save profile body metrics on different days to see trends.", hasData = points.isNotEmpty()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            series.forEach { item -> LegendItem(item.label, item.color) }
        }
        Spacer(Modifier.height(8.dp))
        val axisColor = BoneDark
        Canvas(modifier = Modifier.fillMaxWidth().height(190.dp)) {
            val bounds = PlotBounds(width = size.width, height = size.height)
            drawAxes(bounds, axisColor)
            val allValues = series.flatMap { metric -> points.mapNotNull(metric.value) }
            if (allValues.isEmpty()) return@Canvas
            val minValue = allValues.minOrNull() ?: 0.0
            val maxValue = allValues.maxOrNull() ?: 1.0
            val step = bounds.plotWidth / (points.lastIndex).coerceAtLeast(1)
            series.forEach { metric ->
                val metricPoints = points.mapIndexedNotNull { index, point ->
                    metric.value(point)?.let { value -> Offset(bounds.left + step * index, bounds.yFor(value, minValue, maxValue)) }
                }
                if (metricPoints.size == 1) {
                    drawCircle(metric.color, radius = 5f, center = metricPoints.first())
                } else if (metricPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(metricPoints.first().x, metricPoints.first().y)
                        metricPoints.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, color = metric.color, style = Stroke(width = 4f, cap = StrokeCap.Round))
                    metricPoints.forEach { drawCircle(metric.color, radius = 4.5f, center = it) }
                }
            }
        }
        val first = points.firstOrNull()?.label.orEmpty()
        val last = points.lastOrNull()?.label.orEmpty()
        if (first.isNotBlank() && last.isNotBlank()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(first, style = MaterialTheme.typography.labelMedium, color = Steel)
                Text(last, style = MaterialTheme.typography.labelMedium, color = Steel)
            }
        }
    }
}

@Composable
private fun ChartShell(
    title: String,
    emptyText: String,
    hasData: Boolean,
    content: @Composable () -> Unit,
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink900, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        if (hasData) {
            content()
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ChartFooter(points: List<ChartPointUiState>, suffix: String, textColor: Color) {
    if (points.isEmpty()) return
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(points.first().label, style = MaterialTheme.typography.labelMedium, color = textColor)
        Text("Latest ${points.last().value.formatNumber()}$suffix", style = MaterialTheme.typography.labelMedium, color = Ink900, fontWeight = FontWeight.Bold)
        Text(points.last().label, style = MaterialTheme.typography.labelMedium, color = textColor)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.width(14.dp).height(14.dp)) {
            drawCircle(color = color, radius = size.minDimension / 2f)
        }
        Text(label, style = MaterialTheme.typography.labelMedium, color = Ink900)
    }
}

private data class BodyMetricSeries(
    val label: String,
    val color: Color,
    val value: (BodyMetricChartPointUiState) -> Double?,
)

private data class PlotBounds(
    val width: Float,
    val height: Float,
) {
    val left = 42f
    val right = width - 12f
    val top = 12f
    val bottom = height - 26f
    val plotWidth = (right - left).coerceAtLeast(1f)
    val plotHeight = (bottom - top).coerceAtLeast(1f)

    fun yFor(value: Double, minValue: Double, maxValue: Double): Float {
        val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0
        return bottom - (((value - minValue) / range) * plotHeight).toFloat()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAxes(bounds: PlotBounds, axisColor: Color) {
    val gridColor = axisColor.copy(alpha = 0.65f)
    repeat(4) { index ->
        val y = bounds.top + bounds.plotHeight * index / 3f
        drawLine(gridColor, Offset(bounds.left, y), Offset(bounds.right, y), strokeWidth = 1.5f)
    }
    drawLine(axisColor, Offset(bounds.left, bounds.top), Offset(bounds.left, bounds.bottom), strokeWidth = 2f)
    drawLine(axisColor, Offset(bounds.left, bounds.bottom), Offset(bounds.right, bounds.bottom), strokeWidth = 2f)
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
        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
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
        leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
    )
}

private fun Double.formatNumber(): String = NumberFormat.format(this)

private val NumberFormat = DecimalFormat("#,##0.#")

