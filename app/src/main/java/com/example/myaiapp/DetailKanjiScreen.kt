package com.example.myaiapp

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentSnapshot

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
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

    // Initialize MediaPlayer
    val mediaPlayer = remember { MediaPlayer() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // TopAppBar with back button
        TopAppBar(
            title = {
                Text(text = "Bảng chữ cái Kanji")
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
                text = { Text("Cơ bản", color = Color.Black) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Nâng cao", color = Color.Black) }
            )
        }

        // Display content based on selected tab
        when (selectedTabIndex) {
            0 -> KanjiGrid(easyKanjiDocuments, mediaPlayer)
            1 -> KanjiGrid(advancedKanjiDocuments, mediaPlayer)
        }
    }
}

@Composable
fun KanjiGrid(kanjiDocuments: List<DocumentSnapshot>, mediaPlayer: MediaPlayer) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(kanjiDocuments.chunked(5)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp), // Bo góc hình vuông
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black), // Màu nền đen
                        contentPadding = PaddingValues(8.dp) // Thêm padding cho nút
                    ) {
                        Text(
                            text = document.id,
                            color = Color.Black // Màu văn bản đen
                        )
                    }
                }
            }
        }
    }
}
