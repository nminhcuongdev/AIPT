package nminhcuong.aipt.feature.chat.presentation.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.chat.presentation.AiTrainerChatBubbleUiState
import nminhcuong.aipt.feature.chat.presentation.AiTrainerChatUiState
import nminhcuong.aipt.ui.theme.Bone
import nminhcuong.aipt.ui.theme.Ember
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Sea
import nminhcuong.aipt.ui.theme.Volt

@Composable
internal fun AiTrainerChatScreen(
    state: AiTrainerChatUiState,
    onBackClick: () -> Unit,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
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
                    description = "Ask about pain, form, recovery, nutrition, or adjustments using your profile and current workout plan.",
                    trailing = {
                        Surface(shape = MaterialTheme.shapes.large, color = Volt) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.padding(14.dp), tint = Ink900)
                        }
                    },
                )
                Spacer(Modifier.height(16.dp))
                ChatMessageList(state)
                ChatInputPanel(
                    state = state,
                    onInputChanged = onInputChanged,
                    onSendClick = onSendClick,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ChatMessageList(state: AiTrainerChatUiState) {
    state.messages.forEach { message ->
        ChatBubble(message)
        Spacer(Modifier.height(10.dp))
    }
    if (state.errorMessage != null) {
        AiptPanel {
            Text("Chat request failed", style = MaterialTheme.typography.titleLarge, color = Ember)
            Spacer(Modifier.height(6.dp))
            Text(state.errorMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun ChatInputPanel(
    state: AiTrainerChatUiState,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    AiptPanel {
        OutlinedTextField(
            value = state.input,
            onValueChange = onInputChanged,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            label = { Text("Message") },
            placeholder = { Text("Please guide me in doing the lat pull-down exercise") },
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onSendClick,
            enabled = state.canSend,
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            if (state.isSending) {
                CircularProgressIndicator(color = Ink900)
            } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Send")
            }
        }
    }
}

@Composable
private fun ChatBubble(message: AiTrainerChatBubbleUiState) {
    if (message.role == "user") {
        UserChatBubble(message)
    } else {
        AssistantChatBubble(message)
    }
}

@Composable
private fun UserChatBubble(message: AiTrainerChatBubbleUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = Ink900,
        shadowElevation = 4.dp,
    ) {
        Text(
            text = message.content,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = Bone,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AssistantChatBubble(message: AiTrainerChatBubbleUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("AI trainer") })
                message.model?.let { AssistChip(onClick = {}, label = { Text(it) }) }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Ink900,
                fontWeight = FontWeight.Normal,
            )
            ChatList("Recommendations", message.recommendations, Sea)
            ChatList("Safety notes", message.safetyNotes, Ember)
            ChatList("Suggested actions", message.suggestedActions, Volt)
        }
    }
}

@Composable
private fun ChatList(title: String, items: List<String>, accent: Color) {
    if (items.isEmpty()) return
    Spacer(Modifier.height(10.dp))
    Text(title, style = MaterialTheme.typography.titleMedium, color = accent, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(4.dp))
    items.forEach { item ->
        Text("- $item", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
