package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import coil3.size.Dimension
import com.song.ply.presentation.ui.theme.Dimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMenuOptions(
    showBottomSheet: MutableState<Boolean>,
    title: String,
    isDeleteOnly: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {

    val menuOptions = if (isDeleteOnly) arrayOf("Delete") else arrayOf("Edit", "Delete")
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { onDismiss() },
            sheetState = sheetState

        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = Dimen.dimen20)
                    .fillMaxWidth()
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = Dimen.dimen20)
                            .fillMaxWidth(),
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                            .copy(color = MaterialTheme.colorScheme.primary),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = Dimen.dimen20)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                items(
                    count = menuOptions.size
                ){ index ->

                    val data = menuOptions[index]

                    Row(
                        modifier = Modifier
                            .padding(start = Dimen.dimen10, top = Dimen.dimen20, end = Dimen.dimen10)
                            .fillMaxWidth()
                            .clickable(
                                enabled = true,
                                onClick = {
                                    when(data){
                                        "Edit" -> onEdit()
                                        "Delete" -> onDelete()
                                    }

                                    showBottomSheet.value = false
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (data == "Edit") Icons.Rounded.Edit else Icons.Rounded.Delete,
                            contentDescription = "Option icons"
                        )

                        Text(
                            modifier = Modifier
                                .padding(start = Dimen.dimen10),
                            text = data,
                            style = MaterialTheme.typography.bodyMedium
                                .copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
        }
    }
}