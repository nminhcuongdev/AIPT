package com.example.aipt.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptMetricRow
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.Volt

@Composable
fun MainMenuRoute(
    onCreatePlanClick: () -> Unit,
    onTrackProgressClick: () -> Unit,
    onExerciseLibraryClick: () -> Unit,
    onAiTrainerChatClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AiptHeroHeader(
                    eyebrow = "AIPT",
                    title = "Main menu",
                    description = "Choose a training workflow: create a plan, track progress, browse exercises, or talk with your AI personal trainer.",
                )
                Spacer(Modifier.height(16.dp))
                AiptMetricRow(
                    items = listOf(
                        "Plan" to "weekly blueprint",
                        "Track" to "daily progress",
                        "Chat" to "AI trainer",
                    ),
                )
                Spacer(Modifier.height(16.dp))
                MainMenuAction(
                    title = "Create workout plan",
                    description = "Generate a weekly training blueprint from your saved profile, body metrics, goal, and equipment.",
                    accent = Volt,
                    onClick = onCreatePlanClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Track workout progress",
                    description = "Log weight, reps, and notes for each exercise, then update the next week plan after AI analysis.",
                    accent = Sea,
                    onClick = onTrackProgressClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Exercise library",
                    description = "Search and filter exercises by muscle group and equipment.",
                    accent = Ink900,
                    onClick = onExerciseLibraryClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "AI personal trainer chat",
                    description = "Chat with your trainer about training, recovery, nutrition, and plan adjustments. Details will be added next.",
                    accent = Ember,
                    onClick = onAiTrainerChatClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Profile and equipment",
                    description = "Update body composition, training goal, preferences, and available gym equipment.",
                    accent = Color(0xFF6C5CE7),
                    onClick = onProfileClick,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MainMenuAction(
    title: String,
    description: String,
    accent: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(shape = MaterialTheme.shapes.large, color = accent) {
                Text(
                    text = title.take(1).uppercase(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (accent == Volt) Ink900 else Bone,
                    fontWeight = FontWeight.Black,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Ink900)
                Spacer(Modifier.height(6.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
