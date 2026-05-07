package com.example.aipt.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun NavButtons(onBack: () -> Unit, onNext: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp)) { Text("Back") }
        Button(onClick = onNext, modifier = Modifier.weight(1f).height(54.dp)) { Text("Continue") }
    }
}

@Composable
internal fun NumberField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(label) },
        leadingIcon = { Icon(numberFieldIcon(label), contentDescription = null) },
    )
}

@Composable
internal fun DecimalField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        label = { Text(label) },
        leadingIcon = { Icon(decimalFieldIcon(label), contentDescription = null) },
    )
}

private fun numberFieldIcon(label: String): ImageVector = when {
    label.contains("Age", ignoreCase = true) -> Icons.Default.Person
    label.contains("Height", ignoreCase = true) -> Icons.Default.Height
    label.contains("Weight", ignoreCase = true) -> Icons.Default.MonitorWeight
    label.contains("BMR", ignoreCase = true) -> Icons.Default.LocalFireDepartment
    label.contains("Visceral", ignoreCase = true) -> Icons.Default.AccessibilityNew
    else -> Icons.Default.Numbers
}

private fun decimalFieldIcon(label: String): ImageVector = when {
    label.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
    label.contains("arm", ignoreCase = true) || label.contains("leg", ignoreCase = true) || label.contains("Trunk", ignoreCase = true) -> Icons.Default.FitnessCenter
    label.contains("SMM", ignoreCase = true) -> Icons.Default.FitnessCenter
    label.contains("Body fat", ignoreCase = true) -> Icons.Default.MonitorWeight
    else -> Icons.Default.Numbers
}
