package com.example.myapp

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.VocabItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(navController: NavController, vocab: String?) {
    var vocabularyList by remember { mutableStateOf(emptyList<VocabItem>()) }

    LaunchedEffect(true) {
        vocabularyList = firestoreRepository.getVocabularyDocuments()
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Bảng từ vựng",
                    onTextLayout = {} // Provide an empty lambda
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        VocabularyList(vocabularyList, navController)
    }
}

@Composable
fun VocabularyList(
    vocabularyList: List<VocabItem>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(vocabularyList) { item ->
            VocabularyItem(item, navController)
        }
    }
}

@Composable
fun VocabularyItem(item: VocabItem, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("vocabulary_detail/${item.go}")
            },
        color = Color.White, // Màu nền của Surface
        shape = MaterialTheme.shapes.medium.copy(all = CornerSize(8.dp)), // Bo góc của Surface
        border = BorderStroke(1.dp, Color.Black) // Viền màu đen
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            Text(
                text = "Từ: ${item.kotoba}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                onTextLayout = {}
            )
            Text(
                text = "Nghĩa: ${item.go}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                onTextLayout = {}
            )
            Text(
                text = "Kanji: ${item.kanji}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                onTextLayout = {}
            )

            Text(
                text = "Romaji: ${item.romaji}",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                onTextLayout = {}
            )
        }
    }
}
