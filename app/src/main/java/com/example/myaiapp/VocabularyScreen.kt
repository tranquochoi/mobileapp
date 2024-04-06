package com.example.myapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun VocabularyScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var vocabularyDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            vocabularyDocuments = firestoreRepository.getVocabularyDocuments(homeName)
        }
    }

    CompositionLocalProvider(LocalContentColor provides Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Từ vựng",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    onTextLayout = {}, // hoặc null nếu không cần
                )
            }

            // Hiển thị Tab và ViewPager
            VocabularyTabs(navController = navController, vocabularyDocuments = vocabularyDocuments)
        }
    }
}

@Composable
fun VocabularyTabs(navController: NavController, vocabularyDocuments: List<DocumentSnapshot>) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Hiển thị các tab dưới dạng LazyRow để lướt
        LazyRow {
            items(vocabularyDocuments) { document ->
                val isSelected = vocabularyDocuments.indexOf(document) == selectedTabIndex
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = vocabularyDocuments.indexOf(document) },
                    modifier = Modifier
                        .background(if (isSelected) Color.Black else Color.Transparent, MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Add padding here
                ) {
                    Text(
                        text = "Bài ${vocabularyDocuments.indexOf(document) + 1}",
                        color = if (isSelected) Color.White else Color.Black,
                        onTextLayout = {}, // hoặc null nếu không cần
                    )
                }
            }
        }

        // Hiển thị nội dung tương ứng với tab được chọn
        vocabularyDocuments.getOrNull(selectedTabIndex)?.let { document ->
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
            .border(1.dp, Color.Black, MaterialTheme.shapes.small),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Tiêu đề và dữ liệu cho từ vựng
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Từ vựng: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = kotoba,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Tiêu đề và dữ liệu cho kanji
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Kanji: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = kanji,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Tiêu đề và dữ liệu cho go
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nghĩa: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = go,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Tiêu đề và dữ liệu cho romaji
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Romaji: ",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = romaji,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

fun processVocabularyText(text: String): AnnotatedString {

    val builder = AnnotatedString.Builder()

    val words = text.split(", ")

    words.forEachIndexed { index, word ->
        builder.append(word)
        if (index < words.size - 1) {
            builder.append(", ") // Thêm dấu phẩy sau mỗi từ
        }
    }

    return builder.toAnnotatedString()
}
