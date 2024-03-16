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
        // TabRow with two tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Cơ bản") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Nâng cao") }
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = document.id) // Hiển thị tên của mỗi document
                    }
                }
            }
        }
    }
}
