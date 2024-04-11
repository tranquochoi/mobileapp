package com.example.myaiapp

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.bumptech.glide.Glide
import com.bumptech.glide.gifdecoder.GifDecoder
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val db = Firebase.firestore

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailKanjiScreen(navController: NavController, homeKanjiName: String?) {
    val firestoreRepository = FirestoreRepository()

    var easyKanjiDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var advancedKanjiDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeKanjiName) {
        if (!homeKanjiName.isNullOrEmpty()) {
            easyKanjiDocuments = firestoreRepository.getKanjiDocuments(homeKanjiName, "easy")
            advancedKanjiDocuments = firestoreRepository.getKanjiDocuments(homeKanjiName, "advanced")
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // TopAppBar with back button
        TopAppBar(
            title = {
                Text(text = "Bảng chữ cái Kanji",    onTextLayout = {}, // hoặc null nếu không cần
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // TabRow with two tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Black // Màu của tab được chọn
                )
            },
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Cơ bản", color = Color.Black,    onTextLayout = {}, // hoặc null nếu không cần
                ) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Nâng cao", color = Color.Black,    onTextLayout = {}, // hoặc null nếu không cần
                ) }
            )
        }

        // Display content based on selected tab
        when (selectedTabIndex) {
            0 -> KanjiGrid(easyKanjiDocuments)
            1 -> KanjiGrid(advancedKanjiDocuments)
        }
    }
}

@Composable
fun KanjiGrid(kanjiDocuments: List<DocumentSnapshot>) {
    // State để kiểm soát việc hiển thị dialog
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: KanjiViewModel = viewModel()

    // Get the Coil ImageLoader instance
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .availableMemoryPercentage(0.25)
        .crossfade(true)
        .build()

    // Provide the ImageLoader through LocalImageLoader
    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(kanjiDocuments.chunked(1)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { document ->
                        val url = document.getString("img")
                        val text = document.id
                        val gifUrl = document.getString("gif")

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable {
                                    gifUrl?.let {
                                        // Lưu gifUrl vào ViewModel và hiển thị dialog
                                        viewModel.gifUrl = it
                                        showDialog = true
                                    }
                                }
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (url != null) {
                                    Image(
                                        painter = rememberImagePainter(url),
                                        contentDescription = null,
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                                Text(
                                    text = text,
                                    color = Color.Black,    onTextLayout = {}, // hoặc null nếu không cần

                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog hiển thị hình ảnh GIF
    CustomAlertDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        gifUrl = viewModel.gifUrl
    )
}

// Tạo một @Composable function tùy chỉnh để hiển thị AlertDialog với nút Replay
@Composable
fun CustomAlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    gifUrl: String
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest
        ) {
            AlertDialog(
                onDismissRequest = onDismissRequest,
                title = { Text(text = "Cách viết",    onTextLayout = {}, // hoặc null nếu không cần
                ) },
                text = {
                    GlideGifImage(
                        url = gifUrl,
                        modifier = Modifier.size(240.dp) // Kích thước hình ảnh GIF
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Close",    onTextLayout = {}, // hoặc null nếu không cần
                        )
                    }
                }
            )
        }
    }
}

class KanjiViewModel : ViewModel() {
    var gifUrl: String = ""
}

@Composable
fun GlideGifImage(
    url: String,
    modifier: Modifier = Modifier
) {
    AndroidView(factory = { context ->
        ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }, modifier = modifier) { imageView ->
        Glide.with(imageView)
            .asGif()
            .load(url)
            .into(imageView)
    }
}
