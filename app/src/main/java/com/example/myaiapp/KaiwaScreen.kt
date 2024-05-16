package com.example.myaiapp

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.ActionBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun KaiwaScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var kaiwa1Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var kaiwa2Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            kaiwa1Documents = firestoreRepository.getKaiwaDocuments(homeName, "kaiwa1")
            kaiwa2Documents = firestoreRepository.getKaiwaDocuments(homeName, "kaiwa2")
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val kaiwaLists = listOf(kaiwa1Documents, kaiwa2Documents)
    val tabNames = listOf("Bài 1", "Bài 2")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar with back button
        TopAppBar(
            title = {
                Text(
                    text = "Kaiwa",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White // Set text color to white
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",  tint = Color.White )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFE4B4BF), // Set background color to black
        )

        Box {
            TextButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(start = 0.dp, top = 8.dp)
            ) {
                Text(
                    text = tabNames[selectedTabIndex],
                    color = Color.Black, // Set text color to black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tabNames.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        onClick = {
                            selectedTabIndex = index
                            expanded = false
                        }
                    ) {
                        Text(text = text)
                    }
                }
            }
        }


        KaiwaList(kaiwaDocuments = kaiwaLists[selectedTabIndex], navController = navController)
    }
}


@Composable
fun ExoPlayerVideo(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var isFullScreen by remember { mutableStateOf(false) }

    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        }, update = {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        })
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    if (isFullScreen) {
        BackHandler {
            isFullScreen = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
            })
            IconButton(
                onClick = { isFullScreen = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Full Screen", tint = Color.White)
            }
        }
    } else {
        Box {
            AndroidView(factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                }
            })
            IconButton(
                onClick = { isFullScreen = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Fullscreen, contentDescription = "Full Screen", tint = Color.White)
            }
        }
    }
}

@Composable
fun VideoItem(title: String, url: String?, content: String, topic: String) {
    url?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
            text = topic,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
            ) {
                ExoPlayerVideo(url = it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Giải thích:",
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            )

            Text(
                text = content,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
    } ?: run {
        Text(
            text = "Error: Video URL not found!",
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun KaiwaList(kaiwaDocuments: List<DocumentSnapshot>, navController: NavController) {
    LazyColumn {
        items(kaiwaDocuments) { document ->
            val videoUrl = document.getString("video")
            val videoTitle = document.getString("title")
            val content = document.getString("content")
            val topic = document.getString("topic")


            // Hiển thị video
            videoUrl?.let {
                VideoItem(title = videoTitle ?: "", url = it, content = content?:"",topic = topic?:"" )
            }
        }
    }
}
