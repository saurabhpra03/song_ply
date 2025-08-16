package com.song.ply.presentation.ui.view.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.song.ply.R
import com.song.ply.framework.data.model.PlaylistWithSongCount
import com.song.ply.framework.data.model.Songs
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.framework.database.entity.PlaylistSongsEntity
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.viewModel.PlaylistSongsViewModel
import com.song.ply.presentation.ui.viewModel.PlaylistViewModel
import com.song.ply.presentation.utils.Const.toast
import com.song.ply.presentation.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetAddToPlayList(
    showPlayListBottomSheet: MutableState<Boolean>,
    songs: Songs,
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    playlistSongsViewModel: PlaylistSongsViewModel = hiltViewModel()
) {

    var selectedPlayList by remember { mutableIntStateOf(-1) }

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()

    playlistViewModel.fetchPlayList()
    val playLists = playlistViewModel.playlistFlow.collectAsState().value

    val isNewPlayList = remember { mutableStateOf(false) }

    playlistViewModel.createFlow.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> {}

            is Resource.Success -> {
                isNewPlayList.value = false
                playlistViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                isNewPlayList.value = false
                playlistViewModel.clearFlow()
            }
        }
    }

    playlistSongsViewModel.addSongFlow.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> {}

            is Resource.Success -> {
                selectedPlayList = -1
                showPlayListBottomSheet.value = false
                playlistSongsViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                showPlayListBottomSheet.value = false
                playlistSongsViewModel.clearFlow()
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { showPlayListBottomSheet.value = false },
        sheetState = sheetState
    ) {

        if (playLists.isEmpty() || isNewPlayList.value){
            CreatePlayList(
                showPlayListBottomSheet = showPlayListBottomSheet,
                isNewPlayList = isNewPlayList,
                playLists = playLists,
                onCreate = { playlistEntity ->
                    playlistViewModel.createPlayList(playlistEntity)
                }
            )
        }else{
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (
                    refSongName,
                    refCreateNewPlayList,
                    refTopDivider,
                    refPlayList,
                    refBottomDivider,
                    refDone) = createRefs()

                Text(
                    modifier = Modifier
                        .constrainAs(refSongName){
                            start.linkTo(parent.start, Dimen.screenPadding)
                            top.linkTo(parent.top)
                            end.linkTo(refCreateNewPlayList.start, Dimen.screenPadding)
                            width = Dimension.fillToConstraints
                        },
                    text = songs.name ?: "",
                    style = MaterialTheme.typography.bodyLarge
                        .copy(color = MaterialTheme.colorScheme.onBackground),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier
                        .constrainAs(refCreateNewPlayList) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end, Dimen.dimen20)
                        }
                        .clickable(
                            enabled = true,
                            onClick = {
                                isNewPlayList.value = true
                                selectedPlayList = -1
                            }
                        ),
                    text = stringResource(R.string.new_collection),
                    style = MaterialTheme.typography.titleSmall
                        .copy(color = MaterialTheme.colorScheme.primary)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .constrainAs(refTopDivider){
                            start.linkTo(parent.start)
                            top.linkTo(refSongName.bottom, Dimen.screenPadding)
                            end.linkTo(parent.end)
                        },
                    thickness = Dimen.dimen1,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(refPlayList){
                            start.linkTo(parent.start, Dimen.screenPadding)
                            top.linkTo(refTopDivider.bottom)
                            end.linkTo(parent.end, Dimen.screenPadding)
                        }
                ) {
                    items(playLists){ playList ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        selectedPlayList = playList.id
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPlayList == playList.id,
                                onClick = {}
                            )

                            Text(
                                modifier = Modifier
                                    .padding(start = Dimen.dimen10),
                                text = playList.name,
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(color = if(selectedPlayList == playList.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .constrainAs(refBottomDivider){
                            start.linkTo(parent.start)
                            top.linkTo(refPlayList.bottom, Dimen.dimen7)
                            end.linkTo(parent.end)
                        },
                    thickness = Dimen.dimen1,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                TextButton(
                    modifier = Modifier
                        .constrainAs(refDone){
                            start.linkTo(parent.start)
                            top.linkTo(refBottomDivider.bottom, Dimen.dimen7)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    onClick = {
                        if(selectedPlayList > 0){
                            playlistSongsViewModel.addSongToPlaylist(
                                PlaylistSongsEntity(
                                    playlistID = selectedPlayList,
                                    path = songs.path,
                                    name = songs.name,
                                    album = songs.album,
                                    artist = songs.artist,
                                    duration = songs.duration,
                                    img = songs.img

                                )
                            )
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.done),
                        style = MaterialTheme.typography.titleSmall
                            .copy(MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
private fun CreatePlayList(
    showPlayListBottomSheet: MutableState<Boolean>,
    isNewPlayList: MutableState<Boolean>,
    playLists: List<PlaylistWithSongCount>,
    onCreate: (PlaylistEntity) -> Unit
){

    // Collection Field
    val title = remember { mutableStateOf("") }
    val titleError = remember { mutableStateOf("") }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (
            refTitle,
            refTitleDivider,
            refFieldTitle,
            refField,
            refFieldError,
            refFieldDivider,
            refBtnCancel,
            refBtnCreate) = createRefs()


        Text(
            modifier = Modifier
                .constrainAs(refTitle){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            text = stringResource(R.string.new_playlist),
            style = MaterialTheme.typography.titleMedium
                .copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center
        )

        HorizontalDivider(
            modifier = Modifier
                .constrainAs(refTitleDivider){
                    start.linkTo(parent.start)
                    top.linkTo(refTitle.bottom, Dimen.dimen20)
                    end.linkTo(parent.end)
                },
            color = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            modifier = Modifier
                .constrainAs(refFieldTitle){
                    start.linkTo(parent.start, Dimen.dimen20)
                    top.linkTo(refTitleDivider.bottom, Dimen.dimen20)
                    end.linkTo(parent.end, Dimen.dimen20)
                    width = Dimension.fillToConstraints
                },
            text = stringResource(R.string.name),
            style = MaterialTheme.typography.bodyMedium
                .copy(color = MaterialTheme.colorScheme.primary)
        )

        PlayListTitleField(
            modifier = Modifier
                .constrainAs(refField){
                    start.linkTo(parent.start, Dimen.dimen20)
                    top.linkTo(refFieldTitle.bottom, Dimen.dimen3)
                    end.linkTo(parent.end, Dimen.dimen20)
                    width = Dimension.fillToConstraints
                },
            collectionText = title,
            onValueChange = {
                title.value = it
                titleError.value = ""
            }
        )

        Text(
            modifier = Modifier
                .constrainAs(refFieldError){
                    start.linkTo(parent.start, Dimen.dimen20)
                    top.linkTo(refField.bottom, Dimen.dimen3)
                    end.linkTo(parent.end, Dimen.dimen20)
                    width = Dimension.fillToConstraints
                },
            text = titleError.value,
            style = MaterialTheme.typography.bodyMedium
                .copy(color = Color.Red)
        )

        HorizontalDivider(
            modifier = Modifier
                .constrainAs(refFieldDivider){
                    start.linkTo(parent.start)
                    top.linkTo(refFieldError.bottom, Dimen.screenPadding)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            thickness = Dimen.dimen1,
            color = MaterialTheme.colorScheme.surfaceVariant
        )

        TextButton(
            modifier = Modifier
                .constrainAs(refBtnCreate){
                    start.linkTo(refBtnCancel.end, Dimen.dimen7)
                    top.linkTo(refFieldDivider.bottom, Dimen.dimen10)
                    end.linkTo(parent.end, Dimen.screenPadding)
                    width = Dimension.fillToConstraints
                },
            onClick = {
                when{
                    title.value.isEmpty() -> titleError.value = "Please enter collection name"
                    else -> {
                        onCreate(PlaylistEntity(name = title.value.trim()))
                    }
                }
            }
        ) {
            Text(
                text = stringResource(R.string.create),
                style = MaterialTheme.typography.titleSmall
                    .copy(color = MaterialTheme.colorScheme.primary))
        }

        TextButton(
            modifier = Modifier
                .constrainAs(refBtnCancel){
                    start.linkTo(parent.start, Dimen.screenPadding)
                    top.linkTo(refFieldDivider.bottom, Dimen.dimen10)
                    end.linkTo(refBtnCreate.start)
                    width = Dimension.fillToConstraints
                },
            onClick = {
                if (playLists.isEmpty()) showPlayListBottomSheet.value = false else isNewPlayList.value = false
            }
        ) {
            Text(
                text = stringResource(R.string.cancel),
                style = MaterialTheme.typography.titleSmall
                    .copy(color = MaterialTheme.colorScheme.primary))
        }
    }
}

@Composable
fun PlayListTitleField(
    modifier: Modifier,
    collectionText: MutableState<String>,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = collectionText.value,
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
                Text(
                    text = stringResource(R.string.enter_playlist_name),
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