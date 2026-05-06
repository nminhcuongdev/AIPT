package com.example.aipt.feature.chat.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aipt.core.ui.components.AiptHeroHeader
import com.example.aipt.core.ui.components.AiptPanel
import com.example.aipt.core.ui.components.AiptScreen
import com.example.aipt.ui.theme.Ink900

@Composable
fun AiTrainerChatRoute(
    onBackClick: () -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = "AI Trainer",
                    title = "Personal trainer chat",
                    description = "This chat entry point is ready. The conversation flow, API contract, and trainer behavior can be added after you describe the details.",
                )
                Spacer(Modifier.height(16.dp))
                AiptPanel {
                    Text("Chat bot placeholder", style = MaterialTheme.typography.titleLarge, color = Ink900)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "The next step is to connect this screen to your AI personal trainer API and define what context should be sent with each message.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Message") },
                        placeholder = { Text("Coming soon") },
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) {
                        Text("Send")
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

