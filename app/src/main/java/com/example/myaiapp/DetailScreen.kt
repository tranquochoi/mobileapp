package com.example.myaiapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.FirestoreRepository
import android.media.MediaPlayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun DetailScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var mojiHiraganaDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var mojiKatakanaDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            mojiHiraganaDocuments = firestoreRepository.getMojiDocuments(homeName, "moji_hiragana")
            mojiKatakanaDocuments = firestoreRepository.getMojiDocuments(homeName, "moji_katakana")
        }
    }

    var selectedMoji by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    // Initialize MediaPlayer
    val mediaPlayer = remember { MediaPlayer() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar with back button
        TopAppBar(
            title = {
                Text(
                    text = "Bảng chữ cái tiếng Nhật",
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1 // Apply smaller font size

                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White // Set icon color to white
                    )
                }
            },
            backgroundColor = Color.Black, // Set background color to black
            modifier = Modifier.fillMaxWidth()
        )


        // TabRow with two tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.Black // Màu của tab được chọn
                )
            },
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0; selectedMoji = null },
                text = { Text("Bảng Hiragana", color = Color.Black) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1; selectedMoji = null },
                text = { Text("Bảng Katakana", color = Color.Black) }
            )
        }

        // Display content based on selected tab
        when (selectedTab) {
            0 -> MojiGrid(mojiHiraganaDocuments, mediaPlayer, firestoreRepository)
            1 -> MojiGrid(mojiKatakanaDocuments, mediaPlayer, firestoreRepository)
        }
    }
}


@Composable
fun MojiGrid(mojiDocuments: List<DocumentSnapshot>, mediaPlayer: MediaPlayer, firestoreRepository: FirestoreRepository) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(mojiDocuments.chunked(4)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp), // Equal horizontal padding
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { document ->
                    OutlinedButton(
                        onClick = {
                            val url = document.getString("a") // Lấy URL từ trường "a" của document
                            if (url != null) {
                                mediaPlayer.apply {
                                    reset()
                                    setDataSource(url)
                                    prepare()
                                    start()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(width = 76.dp, height = 76.dp) // Đặt kích thước cố định cho nút
                            .padding(2.dp), // Thêm padding cho nút
                        shape = RoundedCornerShape(8.dp), // Bo góc hình vuông
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black), // Màu nền đen
                        contentPadding = PaddingValues(0.dp) // Xóa padding mặc định của nút
                    ) {
                        Text(
                            text = document.id,
                            color = Color.Black, // Màu văn bản đen
                            modifier = Modifier.padding(8.dp), // Thêm padding cho văn bản
                            onTextLayout = {}, // hoặc null nếu không cần
                        )
                    }
                }
            }
        }
    }
}

