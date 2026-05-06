package com.example.aipt.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aipt.ui.theme.Bone
import com.example.aipt.ui.theme.CardDark
import com.example.aipt.ui.theme.Ink900
import com.example.aipt.ui.theme.Volt

@Composable
fun AiptScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            content()
        }
    }
}

@Composable
fun AiptPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun AiptHeroHeader(
    eyebrow: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = CardDark,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Volt,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Bone,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Bone.copy(alpha = 0.76f),
                )
            }
            if (trailing != null) {
                trailing()
            }
        }
    }
}

@Composable
fun AiptPill(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Ink900,
    contentColor: Color = Bone,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
        )
    }
}

@Composable
fun AiptMetricRow(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEach { (value, label) ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                    Text(label, style = MaterialTheme.typography.bodyMedium, color = Bone.copy(alpha = 0.78f))
                }
            }
        }
    }
}
