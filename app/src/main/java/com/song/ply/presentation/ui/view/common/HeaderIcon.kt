package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.song.ply.presentation.ui.theme.Dimen

@Composable
fun HeaderIcon(
    modifier: Modifier,
    icon: ImageVector,
    onClick: () -> Unit
) {

    OutlinedCard(
        modifier = modifier
            .size(Dimen.dimen35)
            .clickable(
                enabled = true,
                onClick = onClick
            ),
        border = BorderStroke(
            width = Dimen.borderWidth,
            color = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(Dimen.cornerShape),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Header icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}