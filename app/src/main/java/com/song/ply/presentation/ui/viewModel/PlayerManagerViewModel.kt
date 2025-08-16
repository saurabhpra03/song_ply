package com.song.ply.presentation.ui.viewModel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.song.ply.framework.data.model.PlayerUiState
import com.song.ply.presentation.utils.MediaPlaybackService
import com.google.common.util.concurrent.MoreExecutors
import com.song.ply.framework.data.model.Songs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerManagerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private lateinit var mediaController: MediaController
    private val sessionToken = SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))

    // Current playing audio
    private val _currentSong = MutableStateFlow<Songs?>(null)
    val currentSong: StateFlow<Songs?> get() = _currentSong

    // Playback UI state
    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState: StateFlow<PlayerUiState> get() = _playerUiState

    // Audio queue
    private val _queueSongs = MutableStateFlow<List<Songs>>(emptyList())
    val queueSongs: StateFlow<List<Songs>> get() = _queueSongs

    private var progressUpdateJob: Job? = null


    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playerUiState.value = _playerUiState.value.copy(isPlaying = isPlaying)
                    startOrStopProgressUpdate(isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    _playerUiState.value = _playerUiState.value.copy(duration = mediaController.duration)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val audioData = mediaItem?.mediaMetadata?.let {
                        Songs(
                            path = mediaItem.localConfiguration?.uri.toString(),
                            name = it.title?.toString(),
                            artist = it.artist?.toString(),
                            img = it.artworkUri,
                            album = "",
                            duration = ""
                        )
                    }
                    _currentSong.value = audioData
                    _playerUiState.value = _playerUiState.value.copy(duration = mediaController.duration)
                }
            })
            // Initial state sync
            _playerUiState.value = _playerUiState.value.copy(
                isPlaying = mediaController.isPlaying,
                duration = mediaController.duration
            )
            _currentSong.value = mediaController.currentMediaItem?.mediaMetadata?.let {
                Songs(
                    path = mediaController.currentMediaItem?.localConfiguration?.uri.toString(),
                    name = it.title?.toString(),
                    artist = it.artist?.toString(),
                    img = it.artworkUri,
                    album = "",
                    duration = ""
                )
            }
            startOrStopProgressUpdate(mediaController.isPlaying)
        }, MoreExecutors.directExecutor())
    }

    private fun startOrStopProgressUpdate(isPlaying: Boolean) {
        progressUpdateJob?.cancel()
        if (isPlaying) {
            progressUpdateJob = viewModelScope.launch {
                while (mediaController.isPlaying) {
                    _playerUiState.value = _playerUiState.value.copy(currentPosition = mediaController.currentPosition)
                    delay(500)
                }
            }
        }
    }

    fun isPlaying() = mediaController.isPlaying

    // Play audio from list starting at selected item
    fun playAudio(audioData: Songs, audioList: List<Songs>) {
        // Start the service. This is now necessary for modern Android versions
        // to ensure the service runs in the foreground.
        val serviceIntent = Intent(context, MediaPlaybackService::class.java)
        //context.startForegroundService(serviceIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }


        val mediaItems = audioList.map { audio ->
            MediaItem.Builder()
                .setUri(audio.path!!.toUri())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(audio.name)
                        .setArtist(audio.artist)
                        .setArtworkUri(audio.img)
                        .build()
                )
                .build()
        }
        mediaController.setMediaItems(mediaItems, audioList.indexOf(audioData), 0L)
        mediaController.prepare()
        mediaController.play()
    }

    // Play next by inserting item after current
    fun playNext(audioData: Songs) {
        val mediaItem = MediaItem.Builder()
            .setUri(audioData.path!!.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(audioData.name)
                    .setArtist(audioData.artist)
                    .setAlbumTitle(audioData.album)
                    .setArtworkUri(audioData.img)
                    .build()
            )
            .build()
        val nextIndex = mediaController.currentMediaItemIndex + 1
        mediaController.addMediaItem(nextIndex, mediaItem)
    }

    // Add to end of queue if not already present
    fun addToQueue(audioData: Songs) {
        /* val alreadyQueued = mediaController.mediaItems.any {
             it.mediaMetadata.title == audioData.name
         }
         if (alreadyQueued) {
             context.toast(context.getString(R.string.already_in_queue))
             return
         }

         val mediaItem = MediaItem.Builder()
             .setUri(audioData.path!!.toUri())
             .setMediaMetadata(
                 MediaMetadata.Builder()
                     .setTitle(audioData.name)
                     .setArtist(audioData.artist)
                     .setAlbumTitle(audioData.album)
                     .setArtworkUri(audioData.img)
                     .build()
             )
             .build()

         mediaController.addMediaItem(mediaController.mediaItemCount, mediaItem)*/
    }

    // Toggle playback
    fun togglePlayPause() = if (mediaController.isPlaying) mediaController.pause() else mediaController.play()

    // Seek to position
    fun seekTo(position: Long) {
        mediaController.seekTo(position)
    }

    // Skip to next item
    fun skipToNext() {
        mediaController.seekToNext()
    }

    // Skip to previous item
    fun skipToPrevious() {
        mediaController.seekToPrevious()
    }

    fun clearPlayer(){
        mediaController.clearMediaItems()
    }

    // Reorder items in queue
    fun moveQueueItem(from: Int, to: Int) {
        val currentList = _queueSongs.value.toMutableList()
        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)
            _queueSongs.value = currentList

            val mediaItems = currentList.map {
                MediaItem.Builder()
                    .setUri(it.path!!.toUri())
                    .setTag(it)
                    .build()
            }

            // Preserve the current position
            val currentPlaying = _currentSong.value
            val newIndex = currentList.indexOfFirst { it.path == currentPlaying?.path }

            /*player.setMediaItems(mediaItems, newIndex, player.currentPosition)
            player.prepare()*/
        }
    }

    // Cleanup
    override fun onCleared() {
        mediaController.release()
        progressUpdateJob?.cancel()
        super.onCleared()
    }
}