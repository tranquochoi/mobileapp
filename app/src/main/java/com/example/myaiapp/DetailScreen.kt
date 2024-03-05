package com.example.myapp

// DetailScreen.kt
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
import com.example.myaiapp.FirestoreRepository

@Composable
fun DetailScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var mojiHiraganaDocuments by remember { mutableStateOf<List<String>>(emptyList()) }
    var mojiKatakanaDocuments by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            mojiHiraganaDocuments = firestoreRepository.getMojiDocuments(homeName, "moji_hiragana")
            mojiKatakanaDocuments = firestoreRepository.getMojiDocuments(homeName, "moji_katakana")
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
                text = { Text("Bảng Hiragana") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Bảng Katakana") }
            )
        }

        // Display content based on selected tab
        when (selectedTabIndex) {
            0 -> MojiButtonList(mojiHiraganaDocuments)
            1 -> MojiButtonList(mojiKatakanaDocuments)
        }
    }
}

@Composable
fun MojiButtonList(mojiDocuments: List<String>) {
    var selectedMoji by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(
            modifier = Modifier.align(Alignment.Center)
        ) {
            items(mojiDocuments) { mojiName ->
                Button(
                    onClick = { selectedMoji = mojiName },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(text = mojiName)
                }
            }
        }

        selectedMoji?.let { selectedMoji ->
            Text(
                text = selectedMoji,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentSize(Alignment.Center)
                    .background(Color.LightGray),
                fontSize = 86.sp, // Điều chỉnh kích thước font chữ tại đây
                textAlign = TextAlign.Center
            )
        }
    }
}
