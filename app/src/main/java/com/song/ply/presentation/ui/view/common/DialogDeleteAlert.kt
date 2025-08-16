package com.song.ply.presentation.ui.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.song.ply.R
import com.song.ply.presentation.ui.theme.Dimen

@Composable
fun DialogDeleteAlert(
    showDeleteFileAlert: MutableState<Boolean>,
    title: String,
    fileName: String,
    onClickDelete: () -> Unit
) {

    if (showDeleteFileAlert.value) {
        Dialog(
            onDismissRequest = {
                showDeleteFileAlert.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    val (refTitle,
                        refMessage,
                        refDelete,
                        refCancel) = createRefs()

                    Text(
                        modifier = Modifier
                            .constrainAs(refTitle) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(Dimen.dimen17),
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                            .copy(color = MaterialTheme.colorScheme.background),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier
                            .constrainAs(refMessage) {
                                start.linkTo(parent.start, Dimen.dimen20)
                                top.linkTo(refTitle.bottom, Dimen.dimen30)
                                end.linkTo(parent.end, Dimen.dimen20)
                                width = Dimension.fillToConstraints
                            },
                        text = stringResource(R.string.are_you_sure, fileName),
                        style = MaterialTheme.typography.bodySmall
                            .copy(color = MaterialTheme.colorScheme.onBackground),
                        textAlign = TextAlign.Center
                    )


                    DialogBtn(
                        modifier = Modifier
                            .constrainAs(refDelete) {
                                start.linkTo(refCancel.end)
                                top.linkTo(refMessage.bottom, Dimen.dimen30)
                                end.linkTo(parent.end, Dimen.dimen20)
                            },
                        text = stringResource(R.string.delete),
                        onClick = onClickDelete
                    )

                    DialogBtn(
                        modifier = Modifier
                            .constrainAs(refCancel) {
                                start.linkTo(parent.start, Dimen.dimen20)
                                top.linkTo(refMessage.bottom, Dimen.dimen30)
                                end.linkTo(refDelete.start, Dimen.dimen16)
                            },
                        text = stringResource(R.string.cancel),
                        onClick = { showDeleteFileAlert.value = false }
                    )
                }
            }
        }
    }

}