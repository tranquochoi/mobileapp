package com.example.myaiapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.FirestoreRepository
import android.media.MediaPlayer
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
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // TabRow with two tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0; selectedMoji = null },
                text = { Text("Bảng Hiragana") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1; selectedMoji = null },
                text = { Text("Bảng Katakana") }
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
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { document ->
                    Button(
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
                            .size(width = 80.dp, height = 80.dp) // Đặt kích thước cố định cho nút
                            .padding(2.dp) // Thêm padding cho nút
                    ) {
                        Text(text = document.id) // Hiển thị tên của mỗi document
                    }
                }
            }
        }
    }
}
