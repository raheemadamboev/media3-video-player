package xyz.teamgravity.media3videoplayer.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import xyz.teamgravity.media3videoplayer.R
import xyz.teamgravity.media3videoplayer.presentation.viewmodel.VideoViewModel

@Composable
fun VideoScreen(
    viewmodel: VideoViewModel = hiltViewModel(),
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(viewmodel::onAddVideo)
    }
    val owner = LocalLifecycleOwner.current
    val videos by viewmodel.videoItems.collectAsState()
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }

    DisposableEffect(key1 = owner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewmodel.getPlayer()
                }
            },
            update = { player ->
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        player.onPause()
                        player.player?.pause()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        player.onResume()
                    }
                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9F)
        )
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(onClick = { launcher.launch("video/mp4") }) {
            Icon(
                imageVector = Icons.Default.FileOpen,
                contentDescription = stringResource(id = R.string.cd_open_video_file)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(videos) { video ->
                Text(
                    text = video.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { viewmodel.onSelectVideo(video.mediaItem) }
                )
            }
        }
    }
}