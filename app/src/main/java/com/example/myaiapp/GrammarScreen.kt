package com.example.myaiapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun GrammarScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var grammarDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            grammarDocuments = firestoreRepository.getGrammarDocuments(homeName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Ngữ pháp tiếng Nhật",
            style = MaterialTheme.typography.titleSmall
        )

        // Hiển thị Tab và ViewPager
        GrammarTabs(navController = navController, grammarDocuments = grammarDocuments)
    }
}

@Composable
fun GrammarTabs(navController: NavController, grammarDocuments: List<DocumentSnapshot>) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Hiển thị các tab dưới dạng LazyRow để lướt
        LazyRow {
            items(grammarDocuments) { document ->
                val isSelected = grammarDocuments.indexOf(document) == selectedTabIndex
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = grammarDocuments.indexOf(document) },
                    modifier = Modifier
                        .background(if (isSelected) Color.Black else Color.Transparent, MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Add padding here
                ) {
                    Text(
                        text = "Bài ${grammarDocuments.indexOf(document) + 1}",
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }

        // Hiển thị nội dung tương ứng với tab được chọn
        grammarDocuments.getOrNull(selectedTabIndex)?.let { document ->
            GrammarItem(document = document, navController = navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarItem(document: DocumentSnapshot, navController: NavController) {
    val grammar = document.getString("gram")

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Navigate to detail screen */ }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    grammar?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

