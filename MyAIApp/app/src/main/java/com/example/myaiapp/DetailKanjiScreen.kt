package com.example.myaiapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DetailKanjiScreen(navController: NavController , homeKanjiName: String?) {
    val firestoreRepository = FirestoreRepository()

    var easyDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var advancedDocuments by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(homeKanjiName) {
        if (!homeKanjiName.isNullOrEmpty()) {
            easyDocuments = firestoreRepository.getKanjiDocuments(homeKanjiName, "easy")
            advancedDocuments = firestoreRepository.getKanjiDocuments(homeKanjiName, "advanced")
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

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
            0 -> MojiButtonList(easyDocuments)
            1 -> MojiButtonList(advancedDocuments)
        }
    }
}

@Composable
fun MojiButtonList(kanjiDocument: List<String>) {
    var selectedKanji by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(
            modifier = Modifier.align(Alignment.Center)
        ) {
            items(kanjiDocument) { kanjiName ->
                Button(
                    onClick = { selectedKanji = kanjiName },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = kanjiName)
                }
            }
        }

        selectedKanji?.let { selectedKanji ->
            Text(
                text = selectedKanji,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentSize(Alignment.Center)
                    .background(Color.LightGray),
                fontSize = 86.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
