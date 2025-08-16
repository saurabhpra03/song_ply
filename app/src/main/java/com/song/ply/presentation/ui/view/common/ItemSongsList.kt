package com.song.ply.presentation.ui.view.common

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.song.ply.R
import com.song.ply.presentation.ui.theme.Dimen


@Composable
fun ItemSongsList(
    index: Int,
    selectedSongPath: MutableState<String>,
    audioBitmap: Bitmap?,
    name: String,
    artist: String,
    path: String,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .padding(
                top = if (index == 0) Dimen.dimen3 else Dimen.dimen0,
                bottom = Dimen.dimen20
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                enabled = true,
                onClick = onClick
            ),
        shape = RoundedCornerShape(Dimen.cornerShape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(
                    start = Dimen.dimen10,
                    top = Dimen.dimen10,
                    bottom = Dimen.dimen10
                )
                .fillMaxWidth()
        ) {
            val (
                refImg,
                refName,
                refAlbum,
                refMoreVertical) = createRefs()

            audioBitmap?.let { itImg ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(audioBitmap)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Song thumbnail",
                    modifier = Modifier
                        .constrainAs(refImg) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(Dimen.dimen35)
                        .clip(RoundedCornerShape(Dimen.dimen17)),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Icon(
                    modifier = Modifier
                        .constrainAs(refImg) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(Dimen.dimen35)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(
                            width = Dimen.borderWidth,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    imageVector = Icons.Outlined.Audiotrack,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = "Music icon"
                )
            }

            when(selectedSongPath.value){
                path -> IconButton(
                    modifier = Modifier.constrainAs(refMoreVertical) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    onClick = { }
                ) {
                    LottieEqualizer(
                        isPlaying = true,
                        sizeDp = 24f
                    )
                }
                else -> IconButton(
                    modifier = Modifier
                        .constrainAs(refMoreVertical) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                    onClick = onMoreClick
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More vertical menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                modifier = Modifier
                    .constrainAs(refName) {
                        start.linkTo(refImg.end, Dimen.dimen10)
                        top.linkTo(refMoreVertical.top, Dimen.dimen7)
                        end.linkTo(refMoreVertical.start, Dimen.dimen20)
                        bottom.linkTo(refAlbum.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                text = name,
                style = MaterialTheme.typography.titleLarge
                    .copy(color = if (selectedSongPath.value == path) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                modifier = Modifier
                    .constrainAs(refAlbum) {
                        start.linkTo(refImg.end, Dimen.dimen10)
                        top.linkTo(refName.bottom)
                        end.linkTo(refMoreVertical.start, Dimen.dimen20)
                        bottom.linkTo(refMoreVertical.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                text = artist,
                style = MaterialTheme.typography.bodyMedium
                    .copy(color = if (selectedSongPath.value == path) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun LottieEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Float = 24f
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.equalizer_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(sizeDp.dp)
    )
}
