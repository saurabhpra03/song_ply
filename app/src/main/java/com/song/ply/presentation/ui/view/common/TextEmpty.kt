package com.song.ply.presentation.ui.view.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.song.ply.R
import com.song.ply.presentation.ui.theme.Grey


@Composable
fun TextEmpty(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.no_data_found),
        style = MaterialTheme.typography.headlineLarge
            .copy(color = Grey)
    )
}