package com.song.ply.presentation.ui.view.home

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import com.song.ply.R
import com.song.ply.framework.data.model.Songs
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetMoreOptions(
    context: Context,
    showBottomSheet: MutableState<Boolean>,
    songs: MutableState<Songs?>,
    songsViewModel: SongsViewModel,
    playerManagerViewModel: PlayerManagerViewModel,
    onAddToPlaylist: (Songs) -> Unit,
    onDelete: (Songs) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                showBottomSheet.value = false
                songs.value = null
            },
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
                        text = songs.value?.name ?: "",
                        style = MaterialTheme.typography.titleLarge
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
                    count = songsViewModel.fetchMenuOptions().size
                ){ index ->
                    val menuOptions =  songsViewModel.fetchMenuOptions()[index]

                    Row(
                        modifier = Modifier
                            .padding(start = Dimen.dimen10, top = Dimen.dimen20, end = Dimen.dimen10)
                            .fillMaxWidth()
                            .clickable(
                                enabled = true,
                                onClick = {
                                    songs.value?.let { itAudioData ->
                                        when(menuOptions.name){
                                            context.getString(R.string.add_to_queue) -> playerManagerViewModel.addToQueue(itAudioData)
                                            context.getString(R.string.add_to_playlist) -> onAddToPlaylist(itAudioData)
                                            context.getString(R.string.delete) -> onDelete(itAudioData)

                                        }
                                    }

                                    showBottomSheet.value = false
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = menuOptions.icon,
                            contentDescription = "Option icons"
                        )

                        Text(
                            modifier = Modifier
                                .padding(start = Dimen.dimen10),
                            text = menuOptions.name,
                            style = MaterialTheme.typography.bodyMedium
                                .copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            }
        }
    }
}