package nminhcuong.aipt.ui.theme

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
    onPrimary = Volt,
    primaryContainer = MintSoft,
    onPrimaryContainer = Ink900,
    secondary = Sea,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF124E45),
    tertiary = Ember,
    background = Bone,
    onBackground = Ink900,
    surface = CardLight,
    onSurface = Ink900,
    surfaceContainer = Color(0xFFF1F3EA),
    surfaceContainerHigh = Color.White,
    onSurfaceVariant = Steel,
    outline = Color(0xFFD8DDCF),
)

private val AiptShapes = Shapes(
    small = RoundedCornerShape(18.dp),
    medium = RoundedCornerShape(26.dp),
    large = RoundedCornerShape(34.dp),
    extraLarge = RoundedCornerShape(46.dp),
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
