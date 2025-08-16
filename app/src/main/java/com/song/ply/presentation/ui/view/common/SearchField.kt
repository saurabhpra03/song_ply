package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.song.ply.R
import com.song.ply.presentation.ui.theme.Dimen

@Composable
fun SearchField(
    modifier: Modifier,
    searchText: MutableState<String>,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        shape = RoundedCornerShape(Dimen.cornerShape),
        value = searchText.value,
        onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
            disabledBorderColor = MaterialTheme.colorScheme.tertiary,
            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.onBackground
        ),
        textStyle = MaterialTheme.typography.titleLarge
            .copy(color = MaterialTheme.colorScheme.onBackground),
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(R.string.search),
                    style = MaterialTheme.typography.titleLarge
                        .copy(color = MaterialTheme.colorScheme.onTertiary)
                )
            }
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}