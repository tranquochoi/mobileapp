    package com.example.myaiapp

    import android.net.Uri
    import android.view.ViewGroup
    import androidx.appcompat.app.ActionBar
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.aspectRatio
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material.Button
    import androidx.compose.material.Icon
    import androidx.compose.material.IconButton
    import androidx.compose.material.Tab
    import androidx.compose.material.TabRow
    import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
    import androidx.compose.material.Text
    import androidx.compose.material.TopAppBar
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
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
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.unit.dp
    import androidx.media3.common.MediaItem
    import androidx.media3.common.util.UnstableApi
    import androidx.media3.exoplayer.SimpleExoPlayer
    import androidx.media3.ui.PlayerView
    import androidx.navigation.NavController

    import com.google.firebase.firestore.DocumentSnapshot

    @UnstableApi
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

        var selectedTabIndex by remember { mutableStateOf(0) }

        val kaiwaLists = listOf(kaiwa1Documents, kaiwa2Documents)

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

            androidx.compose.material3.TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    androidx.compose.material.TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color.Black // Màu của thanh dưới khi chọn tab
                    )
                }
            ) {
                kaiwaLists.forEachIndexed { index, kaiwaDocuments ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text("Bài ${index + 1}", color = Color.Black, onTextLayout = {}) },
                        modifier = Modifier
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> KaiwaList(kaiwaDocuments = kaiwa1Documents, navController = navController)
                1 -> KaiwaList(kaiwaDocuments = kaiwa2Documents, navController = navController)
            }
        }
    }

    @UnstableApi @Composable
    fun VideoPlayer(url: String) {
        val context = LocalContext.current
        var exoPlayer by remember { mutableStateOf<SimpleExoPlayer?>(null) }
        var playWhenReady by remember { mutableStateOf(true) }
        var currentWindow by remember { mutableStateOf(0) }
        var playbackPosition by remember { mutableStateOf(0L) }

        DisposableEffect(Unit) {
            exoPlayer = SimpleExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.playWhenReady = playWhenReady
            exoPlayer?.seekTo(currentWindow, playbackPosition)
            exoPlayer?.prepare()
            onDispose {
                exoPlayer?.release()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 2.dp, color = Color.Black)
        ) {
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    }


    @UnstableApi @Composable
    fun VideoItem(title: String, url: String?) {
        url?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                VideoPlayer(url = it)
            }
        } ?: run {
            Text(
                text = "Error: Video URL not found!",
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    @UnstableApi @Composable
    fun KaiwaList(kaiwaDocuments: List<DocumentSnapshot>, navController: NavController) {
        LazyColumn {
            items(kaiwaDocuments) { document ->
                val videoUrl = document.getString("video")
                val videoTitle = document.getString("title")
                videoUrl?.let {
                    VideoItem(title = videoTitle ?: "", url = it)
                }
            }
        }
    }