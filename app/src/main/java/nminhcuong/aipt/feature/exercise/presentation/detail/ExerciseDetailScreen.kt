package nminhcuong.aipt.feature.exercise.presentation.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import nminhcuong.aipt.core.ui.components.AiptHeroHeader
import nminhcuong.aipt.core.ui.components.AiptPanel
import nminhcuong.aipt.core.ui.components.AiptPill
import nminhcuong.aipt.core.ui.components.AiptScreen
import nminhcuong.aipt.feature.exercise.domain.model.Exercise
import nminhcuong.aipt.ui.theme.Bone
import nminhcuong.aipt.ui.theme.Ink900
import nminhcuong.aipt.ui.theme.Sea
import nminhcuong.aipt.ui.theme.Volt

@Composable
fun ExerciseDetailRoute(
    onBackClick: () -> Unit,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    val exercise by viewModel.exercise.collectAsState()
    ExerciseDetailScreen(exercise, onBackClick, viewModel::onFavoriteClicked)
}

@Composable
private fun ExerciseDetailScreen(
    exercise: Exercise?,
    onBackClick: () -> Unit,
    onFavoriteClicked: (Exercise) -> Unit,
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        AiptScreen(modifier = Modifier.padding(padding), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)) {
            if (exercise == null) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
                return@AiptScreen
            }

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Spacer(Modifier.height(8.dp))
                AiptHeroHeader(
                    eyebrow = exercise.muscleGroup,
                    title = exercise.name,
                    description = "${exercise.equipment} based movement. Review form notes before adding it to your plan.",
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiptPill(exercise.muscleGroup)
                    AiptPill(exercise.equipment, containerColor = Sea, contentColor = Ink900)
                    if (exercise.lastViewedAt != null) AiptPill("Viewed", containerColor = Volt, contentColor = Ink900)
                }
                Spacer(Modifier.height(18.dp))
                AiptPanel {
                    Text("Coach notes", style = MaterialTheme.typography.titleLarge, color = Ink900)
                    Spacer(Modifier.height(8.dp))
                    Text(exercise.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(18.dp))
                ExerciseMediaCard(exercise.videoUrl)
                Spacer(Modifier.height(18.dp))
                Button(onClick = { onFavoriteClicked(exercise) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text(if (exercise.isFavorite) "Remove from saved" else "Save exercise")
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ExerciseMediaCard(videoUrl: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Ink900),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Movement guide", style = MaterialTheme.typography.titleLarge, color = Volt)
            Text("Embedded reference. Use external player if the page blocks preview.", style = MaterialTheme.typography.bodyMedium, color = Bone.copy(alpha = 0.72f))
            Spacer(Modifier.height(14.dp))
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(240.dp),
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
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Open guide externally") }
        }
    }
}
