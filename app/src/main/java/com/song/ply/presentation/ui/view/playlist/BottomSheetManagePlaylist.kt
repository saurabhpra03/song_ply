package com.song.ply.presentation.ui.view.playlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.song.ply.R
import com.song.ply.framework.database.entity.PlaylistEntity
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.view.home.PlayListTitleField
import com.song.ply.presentation.ui.viewModel.PlaylistViewModel
import com.song.ply.presentation.utils.Const.toast
import com.song.ply.presentation.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetManagePlayList(
    showBottomSheet: MutableState<Boolean>,
    playListEntity: MutableState<PlaylistEntity?>,
    onUpdate: () -> Unit,
    playListViewModel: PlaylistViewModel = hiltViewModel()
){
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()

    // Collection Field
    val title = remember { mutableStateOf("") }
    val titleError = remember { mutableStateOf("") }

    playListEntity.value?.let { playList ->  title.value = playList.name}

    playListViewModel.createFlow.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> {}

            is Resource.Success -> {
                showBottomSheet.value = false
                playListViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                showBottomSheet.value = false
                playListViewModel.clearFlow()
            }
        }
    }

    playListViewModel.updateNameFlow.collectAsState().value?.let {
        when(it){
            is Resource.Loading -> {}

            is Resource.Success -> {
                playListEntity.value = null
                showBottomSheet.value = false
                onUpdate()
                playListViewModel.clearFlow()
            }

            is Resource.Failed -> {
                context.toast(it.message)
                playListEntity.value = null
                showBottomSheet.value = false
                playListViewModel.clearFlow()
            }
        }
    }

    if (showBottomSheet.value){
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState
        ) {
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
                    text = stringResource(if(playListEntity.value == null) R.string.new_playlist else R.string.update_playlist),
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
                            start.linkTo(parent.start, Dimen.dimen30)
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
                            start.linkTo(parent.start, Dimen.dimen30)
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
                                playListEntity.value?.let {
                                    playListViewModel.updatePlayListName(PlaylistEntity(id = it.id,name = title.value.trim()))
                                } ?: run {
                                    playListViewModel.createPlayList(PlaylistEntity(name = title.value.trim()))
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(if(playListEntity.value == null) R.string.create else R.string.update),
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
                    onClick = { showBottomSheet.value = false }
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleLarge
                            .copy(color = MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}