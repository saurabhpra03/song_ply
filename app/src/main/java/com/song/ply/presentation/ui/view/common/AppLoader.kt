package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    }
}