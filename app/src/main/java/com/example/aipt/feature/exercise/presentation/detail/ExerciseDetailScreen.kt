package com.example.aipt.feature.exercise.presentation.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.aipt.feature.exercise.domain.model.Exercise

@Composable
fun ExerciseDetailRoute(
    onBackClick: () -> Unit,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    val exercise by viewModel.exercise.collectAsState()

    ExerciseDetailScreen(
        exercise = exercise,
        onBackClick = onBackClick,
        onFavoriteClicked = viewModel::onFavoriteClicked,
    )
}

@Composable
private fun ExerciseDetailScreen(
    exercise: Exercise?,
    onBackClick: () -> Unit,
    onFavoriteClicked: (Exercise) -> Unit,
) {
    Scaffold { padding ->
        if (exercise == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            TextButton(onClick = onBackClick) {
                Text("Quay lai")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(exercise.muscleGroup) })
                AssistChip(onClick = {}, label = { Text(exercise.equipment) })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mo ta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(20.dp))
            ExerciseMediaCard(exercise.videoUrl)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onFavoriteClicked(exercise) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (exercise.isFavorite) "Bo yeu thich" else "Them vao yeu thich")
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ExerciseMediaCard(videoUrl: String) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Huong dan video/GIF",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                factory = { viewContext ->
                    WebView(viewContext).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        loadUrl(videoUrl)
                    }
                },
                update = { webView -> webView.loadUrl(videoUrl) },
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Mo video ben ngoai")
            }
        }
    }
}
