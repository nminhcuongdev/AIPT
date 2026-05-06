package com.example.aipt.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class StartupDestination {
    Loading,
    ProfileFlow,
    MainMenu,
}

@HiltViewModel
class StartupViewModel @Inject constructor(
    profileRepository: ProfileRepository,
) : ViewModel() {
    val destination: StateFlow<StartupDestination> = profileRepository.observeProfile()
        .map { profile ->
            if (profile == null) StartupDestination.ProfileFlow else StartupDestination.MainMenu
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StartupDestination.Loading,
        )
}

@Composable
fun StartupRoute(
    onProfileMissing: () -> Unit,
    onProfileReady: () -> Unit,
    viewModel: StartupViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            StartupDestination.ProfileFlow -> onProfileMissing()
            StartupDestination.MainMenu -> onProfileReady()
            StartupDestination.Loading -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
