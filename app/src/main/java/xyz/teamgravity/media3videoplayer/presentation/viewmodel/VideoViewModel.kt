package xyz.teamgravity.media3videoplayer.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.teamgravity.media3videoplayer.core.util.MetaDataReader
import xyz.teamgravity.media3videoplayer.data.model.VideoItem
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val player: ExoPlayer,
    private val reader: MetaDataReader,
) : ViewModel() {

    companion object {
        private const val VIDEO_URIS = "videoUris"
        private const val NO_NAME = "No name"
        private const val STOP_TIMEOUT = 5_000L
    }

    private val videoUris: StateFlow<List<Uri>> = handle.getStateFlow(VIDEO_URIS, emptyList())

    val videoItems: StateFlow<List<VideoItem>> = videoUris.map { uris ->
        uris.map { uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = reader.getMetaData(uri)?.name ?: NO_NAME
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), emptyList())

    init {
        player.prepare()
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun onAddVideo(value: Uri) {
        handle[VIDEO_URIS] = videoUris.value + value
        player.setMediaItem(MediaItem.fromUri(value))
    }

    fun onSelectVideo(value: MediaItem) {
        player.setMediaItem(value)
    }

    fun getPlayer(): Player {
        return player
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}