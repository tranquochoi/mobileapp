package com.example.myapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var vocab1Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var vocab2Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            vocab1Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab1")
            vocab2Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab2")
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    val vocabularyLists = listOf(vocab1Documents, vocab2Documents)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Từ vựng",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Black // Màu của thanh dưới khi chọn tab
                )
            }
        ) {
            vocabularyLists.forEachIndexed { index, vocabularyDocuments ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text("Bài ${index + 1}", color = Color.Black) },
                    modifier = Modifier
                )
            }
        }


        when (selectedTabIndex) {
            0 -> VocabularyList(vocabularyDocuments = vocab1Documents, navController = navController)
            1 -> VocabularyList(vocabularyDocuments = vocab2Documents, navController = navController)
        }
    }
}

@Composable
fun VocabularyList(vocabularyDocuments: List<DocumentSnapshot>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        items(vocabularyDocuments) { document ->
            VocabularyItem(document = document, navController = navController)
        }
    }
}

@Composable
fun VocabularyItem(document: DocumentSnapshot, navController: NavController) {
    val kotoba = document.getString("kotoba") ?: ""
    val kanji = document.getString("kanji") ?: ""
    val go = document.getString("go") ?: ""
    val romaji = document.getString("romaji") ?: ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            VocabularyRow(title = "Từ vựng:", content = kotoba)
            VocabularyRow(title = "Kanji:", content = kanji)
            VocabularyRow(title = "Nghĩa:", content = go)
            VocabularyRow(title = "Romaji:", content = romaji)
        }
    }
}

@Composable
fun VocabularyRow(title: String, content: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black
        )
    }
}

