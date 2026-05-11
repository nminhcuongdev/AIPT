package nminhcuong.aipt.feature.chat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import nminhcuong.aipt.feature.chat.presentation.components.AiTrainerChatScreen

@Composable
fun AiTrainerChatRoute(
    onBackClick: () -> Unit,
    viewModel: AiTrainerChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    AiTrainerChatScreen(
        state = state,
        onBackClick = onBackClick,
        onInputChanged = viewModel::onInputChanged,
        onSendClick = viewModel::sendMessage,
    )
}
