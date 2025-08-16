package com.song.ply.presentation.ui.view.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.theme.LightPurple
import com.song.ply.presentation.ui.theme.White
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.utils.Const.formatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniPlayer(
    showMiniPlayer: MutableState<Boolean>,
    modifier: Modifier,
    playerManagerViewModel: PlayerManagerViewModel,
    onClose: () -> Unit
) {
    val uiState by playerManagerViewModel.playerUiState.collectAsState()
    val currentPlayingSong by playerManagerViewModel.currentSong.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var dragPosition by remember { mutableStateOf(uiState.currentPosition.toFloat()) }
    var isDragging by remember { mutableStateOf(false) }
    var hasPendingSeek by remember { mutableStateOf(false) }
    var draggedPosition by remember { mutableStateOf<Long?>(null) }

    // Keep slider position in sync with playback unless user is dragging or a seek is pending
    LaunchedEffect(uiState.currentPosition, isDragging, hasPendingSeek) {
        if (!isDragging && !hasPendingSeek) {
            dragPosition = uiState.currentPosition.toFloat()
        }
    }

    val currentDuration =  (draggedPosition ?: uiState.currentPosition).formatDuration()
    val totalDuration = uiState.duration.formatDuration()

    if (showMiniPlayer.value) {
        Card(
            modifier = modifier
                .padding(Dimen.dimen3)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(Dimen.dimen10)
                    .fillMaxWidth()
            ) {
                val (
                    refBtnPlayAndPause,
                    refBtnNext,
                    refBtnPrevious,
                    refName,
                    refAlbum,
                    refProgress,
                    refClose,
                    refCurrentDuration,
                    refDuration
                ) = createRefs()


                Btn(
                    modifier = Modifier.constrainAs(refClose) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                    imageVector = Icons.Rounded.Close,
                    onClick = onClose
                )

                TxtSongTitle(
                    modifier = Modifier.constrainAs(refName) {
                        start.linkTo(parent.start, Dimen.dimen7)
                        top.linkTo(refClose.top, Dimen.dimen7)
                        end.linkTo(refClose.start, Dimen.dimen20)
                        width = Dimension.fillToConstraints
                    },
                    title = currentPlayingSong?.name ?: "Unknown"
                )

                Text(
                    modifier = Modifier.constrainAs(refAlbum) {
                        start.linkTo(parent.start, Dimen.dimen7)
                        top.linkTo(refName.bottom, Dimen.dimen3)
                        end.linkTo(refClose.start, Dimen.dimen20)
                        width = Dimension.fillToConstraints
                    },
                    text = currentPlayingSong?.artist ?: "Unknown",
                    style = MaterialTheme.typography.labelSmall
                        .copy(color = MaterialTheme.colorScheme.tertiary)
                )

                TxtDuration(
                    modifier = Modifier.constrainAs(refCurrentDuration) {
                        start.linkTo(parent.start)
                        top.linkTo(refClose.bottom, Dimen.dimen16)
                    },
                    value = currentDuration
                )

                TxtDuration(
                    modifier = Modifier.constrainAs(refDuration) {
                        top.linkTo(refClose.bottom, Dimen.dimen16)
                        end.linkTo(parent.end)
                    },
                    value = totalDuration
                )

                // Seek bar with drag and tap gestures
                Box(
                    modifier = Modifier
                        .constrainAs(refProgress) {
                            start.linkTo(refCurrentDuration.end, Dimen.dimen7)
                            end.linkTo(refDuration.start, Dimen.dimen7)
                            top.linkTo(refClose.bottom, Dimen.dimen23)
                            width = Dimension.fillToConstraints
                        }
                        .wrapContentHeight()
                        .pointerInput(uiState.duration) {
                            detectDragGestures(
                                onDragStart = {
                                    draggedPosition = uiState.currentPosition
                                },
                                onDragEnd = {
                                    val seekPosition = dragPosition.toLong()
                                    playerManagerViewModel.seekTo(seekPosition)
                                    isDragging = false
                                    hasPendingSeek = true

                                    if (!uiState.isPlaying) {
                                        dragPosition = seekPosition.toFloat()
                                        draggedPosition = seekPosition
                                    }

                                    coroutineScope.launch {
                                        delay(500)
                                        hasPendingSeek = false
                                        draggedPosition = null
                                    }
                                },
                                onDragCancel = { draggedPosition = null }
                            ) { change, _ ->
                                val x = change.position.x
                                val newProgress = (x / size.width).coerceIn(0f, 1f)
                                dragPosition = newProgress * uiState.duration
                                isDragging = true
                                draggedPosition = dragPosition.toLong()
                            }
                        }
                        .pointerInput(uiState.duration) {
                            detectTapGestures { offset ->
                                val tappedProgress = (offset.x / size.width).coerceIn(0f, 1f)
                                playerManagerViewModel.seekTo(
                                    (tappedProgress * uiState.duration).toLong()
                                )
                            }
                        }
                        .drawBehind {
                            drawLine(
                                color = LightPurple,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = 3f
                            )

                            val playedWidth =
                                (dragPosition / uiState.duration).coerceIn(0f, 1f) * size.width
                            drawLine(
                                color = White,
                                start = Offset(0f, size.height / 2),
                                end = Offset(playedWidth, size.height / 2),
                                strokeWidth = 3f
                            )

                            drawCircle(
                                color = White,
                                radius = Dimen.dimen6.toPx(),
                                center = Offset(playedWidth, size.height / 2)
                            )
                        }
                )

                Btn(
                    modifier = Modifier.constrainAs(refBtnPrevious) {
                        start.linkTo(parent.start)
                        top.linkTo(refProgress.bottom, Dimen.dimen23)
                        end.linkTo(refBtnPlayAndPause.start)
                    },
                    imageVector = Icons.Rounded.SkipPrevious,
                    onClick = { playerManagerViewModel.skipToPrevious() }
                )

                Btn(
                    modifier = Modifier.constrainAs(refBtnPlayAndPause) {
                        start.linkTo(parent.start)
                        top.linkTo(refProgress.bottom, Dimen.dimen23)
                        end.linkTo(parent.end)
                    },
                    imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    onClick = { playerManagerViewModel.togglePlayPause() }
                )

                Btn(
                    modifier = Modifier.constrainAs(refBtnNext) {
                        start.linkTo(refBtnPlayAndPause.end)
                        top.linkTo(refProgress.bottom, Dimen.dimen23)
                        end.linkTo(parent.end)
                    },
                    imageVector = Icons.Rounded.SkipNext,
                    onClick = { playerManagerViewModel.skipToNext() }
                )
            }
        }
    }
}

@Composable
private fun TxtSongTitle(modifier: Modifier, title: String) {
    val currentText by rememberUpdatedState(title)

    Text(
        modifier = modifier.basicMarquee(),
        text = currentText,
        style = MaterialTheme.typography.bodyMedium
            .copy(color = MaterialTheme.colorScheme.background),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Btn(modifier: Modifier, imageVector: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .size(Dimen.dimen50)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimen.dimen25),
        border = BorderStroke(
            width = Dimen.borderWidth,
            color = LightPurple
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Mini player button",
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun TxtDuration(modifier: Modifier, value: String) = Text(
    modifier = modifier,
    text = value,
    style = MaterialTheme.typography.labelSmall
        .copy(color = MaterialTheme.colorScheme.background)
)