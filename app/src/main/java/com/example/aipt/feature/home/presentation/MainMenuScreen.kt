package com.example.aipt.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkUnreadChatAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptMetricRow
import com.example.aipt.core.ui.components.AiptPill
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.CardDark
import com.example.aipt.ui.theme.Ember
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.MintSoft
import com.example.aipt.ui.theme.PeachSoft
import com.example.aipt.ui.theme.Sea
import com.example.aipt.ui.theme.SkySoft
import com.example.aipt.ui.theme.Volt

@Composable
fun MainMenuRoute(
    onDashboardClick: () -> Unit,
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
                    eyebrow = "AI PERSONAL TRAINER",
                    title = "Ready to train?",
                    description = "Build your plan, log every session, and let AI adjust next week from your real performance.",
                    trailing = {
                        Surface(shape = MaterialTheme.shapes.large, color = Volt) {
                            Icon(
                                imageVector = Icons.Default.SportsGymnastics,
                                contentDescription = null,
                                modifier = Modifier.padding(14.dp),
                                tint = Ink900,
                            )
                        }
                    },
                )
                Spacer(Modifier.height(14.dp))
                AiptMetricRow(
                    items = listOf(
                        "5D" to "weekly plan",
                        "1:1" to "AI coach",
                        "PR" to "progress",
                    ),
                )
                Spacer(Modifier.height(16.dp))
                CoachCard(onClick = onAiTrainerChatClick)
                Spacer(Modifier.height(14.dp))
                MainMenuAction(
                    title = "Today dashboard",
                    description = "See today's workout, planned exercises, targets, and session status.",
                    icon = Icons.Default.Dashboard,
                    accent = Ink900,
                    container = SkySoft,
                    onClick = onDashboardClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Create workout plan",
                    description = "Generate and confirm a weekly blueprint from profile, InBody data, goals, and equipment.",
                    icon = Icons.Default.PlayArrow,
                    accent = Volt,
                    container = MintSoft,
                    onClick = onCreatePlanClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Track workout progress",
                    description = "Enter weight, reps, and notes for each planned exercise, then update next week.",
                    icon = Icons.Default.Insights,
                    accent = Sea,
                    container = SkySoft,
                    onClick = onTrackProgressClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Exercise library",
                    description = "Search movements by muscle group and available equipment.",
                    icon = Icons.Default.FitnessCenter,
                    accent = Ink900,
                    container = Bone,
                    onClick = onExerciseLibraryClick,
                )
                Spacer(Modifier.height(12.dp))
                MainMenuAction(
                    title = "Profile and equipment",
                    description = "Update body metrics, training constraints, and gym inventory.",
                    icon = Icons.Default.Person,
                    accent = Ember,
                    container = PeachSoft,
                    onClick = onProfileClick,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CoachCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = CardDark,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(shape = MaterialTheme.shapes.large, color = Volt) {
                Icon(
                    imageVector = Icons.Default.MarkUnreadChatAlt,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = Ink900,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("AI trainer chat", style = MaterialTheme.typography.titleLarge, color = Bone)
                    AiptPill("Soon", containerColor = Volt, contentColor = Ink900)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Ask about form, recovery, nutrition, and plan changes when the chatbot API is ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Bone.copy(alpha = 0.72f),
                )
            }
        }
    }
}

@Composable
private fun MainMenuAction(
    title: String,
    description: String,
    icon: ImageVector,
    accent: Color,
    container: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 5.dp,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(shape = MaterialTheme.shapes.large, color = container) {
                Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = accent)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(5.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}


