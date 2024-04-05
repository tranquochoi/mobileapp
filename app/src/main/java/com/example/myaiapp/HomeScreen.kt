package com.example.myapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // Import only one Text function
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.R
import com.example.myaiapp.VocabItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var mojiList by remember { mutableStateOf(emptyList<String>()) }
    var kanjiList by remember { mutableStateOf(emptyList<String>()) }
    var testList by remember { mutableStateOf(emptyList<String>()) }
    var grammarList by remember { mutableStateOf(emptyList<String>()) } // Danh sách ngữ pháp
    var vocabularyList by remember { mutableStateOf(emptyList<VocabItem>()) } // Danh sách từ vựng

    LaunchedEffect(true) {
        mojiList = firestoreRepository.getHomeCollections()
        kanjiList = firestoreRepository.getHomeKajiCollections()
        testList = firestoreRepository.getHomeTestCollections()
        grammarList = firestoreRepository.getGrammarCollections() // Lấy danh sách ngữ pháp
        vocabularyList = firestoreRepository.getVocabularyDocuments() // Lấy danh sách từ vựng

    }

    Column(Modifier.padding(16.dp)) {
        SectionTitle("Làm quen tiếng Nhật")
        MojiSection(mojiList, navController)
        GrammarSection(grammarList, navController)
        SectionTitle("Bảng chữ Kanji")
        KanjiSection(kanjiList, navController)
        SectionTitle("Luyện tập")
        TestSection(testList, navController)
        SectionTitle("Bảng từ vựng")
        VocabularySection(vocabularyList, navController)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp),
        onTextLayout = {} // Provide an empty lambda

    )
}

@Composable
fun MojiSection(mojiList: List<String>, navController: NavController) {
    Column {
        mojiList.forEach { moji ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToDetailScreen(navController, moji)
                    },
                shape = MaterialTheme.shapes.medium

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moji),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Bảng chữ Hiragana + Katakana",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        onTextLayout = {} // Provide an empty lambda
                    )

                }
            }
        }
    }
}

@Composable
fun KanjiSection(kanjiList: List<String>, navController: NavController) {
    Column {
        kanjiList.forEach { kanji ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToDetailKanjiScreen(navController, kanji)
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kanji),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Kanji",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        onTextLayout = {} // Provide an empty lambda

                    )
                }
            }
        }
    }
}

@Composable
fun TestSection(testList: List<String>, navController: NavController) {
    Column {
        testList.forEach { test ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToTestScreen(navController, test)
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.test),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Quiz",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        onTextLayout = {} // Provide an empty lambda

                    )
                }
            }
        }
    }
}

@Composable
fun GrammarSection(grammarList: List<String>, navController: NavController) {
    Column {
        grammarList.forEach { grammar ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToGrammarDetailScreen(navController, grammar)
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nihon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Ngữ pháp",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        onTextLayout = {} // Provide an empty lambda

                    )
                }
            }
        }
    }
}
@Composable
fun VocabularySection(vocabularyList: List<VocabItem>, navController: NavController) {
    Column {
        vocabularyList.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToVocabularyDetailScreen(navController, item.go)
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Vocabulary", // Chỉ đơn giản hiển thị "Vocabulary" cho phần này
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        onTextLayout = {} // Provide an empty lambda
                    )
                }
            }
        }
    }
}
private fun navigateToDetailScreen(navController: NavController, moji: String) {
    navController.navigate("detail/$moji")
}

private fun navigateToDetailKanjiScreen(navController: NavController, kanji: String) {
    navController.navigate("detailkanji/$kanji")
}

private fun navigateToTestScreen(navController: NavController, test: String) {
    navController.navigate("detailtest/$test")
}

private fun navigateToGrammarDetailScreen(navController: NavController, grammar: String) {
    navController.navigate("grammar/$grammar")
}
private fun navigateToVocabularyDetailScreen(navController: NavController, go: String) {
    navController.navigate("detailvocab/$go")
}