package com.example.aipt.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Volt,
    onPrimary = Ink900,
    secondary = Sea,
    tertiary = Ember,
    background = Ink900,
    onBackground = Bone,
    surface = CardDark,
    onSurface = Bone,
    surfaceContainer = Ink700,
    surfaceContainerHigh = CardDark,
    onSurfaceVariant = BoneDark,
    outline = Steel,
)

private val LightColorScheme = lightColorScheme(
    primary = Ink900,
    onPrimary = Color.White,
    secondary = VoltDark,
    tertiary = Ember,
    background = Bone,
    onBackground = Ink900,
    surface = CardLight,
    onSurface = Ink900,
    surfaceContainer = BoneDark,
    surfaceContainerHigh = CardLight,
    onSurfaceVariant = Steel,
    outline = BoneDark,
)

private val AiptShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp),
)

@Composable
fun AIPTTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AiptShapes,
        content = content,
    )
}
