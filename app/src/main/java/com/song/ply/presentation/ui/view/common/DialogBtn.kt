package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.song.ply.presentation.ui.theme.Dimen

@Composable
fun DialogBtn(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(Dimen.dimen3),
        shape = RoundedCornerShape(Dimen.cornerShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
                .copy(color = MaterialTheme.colorScheme.primary),
        )
    }
}