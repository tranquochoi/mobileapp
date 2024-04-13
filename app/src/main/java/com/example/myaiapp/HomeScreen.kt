package com.example.myapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // Import only one Text function
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.R

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    var mojiList by remember { mutableStateOf(emptyList<String>()) }
    var kanjiList by remember { mutableStateOf(emptyList<String>()) }
    var testList by remember { mutableStateOf(emptyList<String>()) }
    var grammarList by remember { mutableStateOf(emptyList<String>()) } // Danh sách ngữ pháp
    var vocabularyList by remember { mutableStateOf(emptyList<String>()) } // Danh sách từ vựng
    var kaiwaList by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(true) {
        mojiList = firestoreRepository.getHomeCollections()
        kanjiList = firestoreRepository.getHomeKajiCollections()
        testList = firestoreRepository.getHomeTestCollections()
        grammarList = firestoreRepository.getGrammarCollections() // Lấy danh sách ngữ pháp
        vocabularyList = firestoreRepository.getVocabularyCollections() // Lấy danh sách từ vựng
        kaiwaList = firestoreRepository.getKaiwaCollections()

    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)) // Màu trắng nhạt nhạt cho nền background
    ) {
        // Đặt TopAppBar ở đây
        TopAppBar(
            title = {
                Text(
                    text = "TheRyna - Học tiếng Nhật cơ bản",
                    style = TextStyle(fontWeight = FontWeight.Bold,color = Color.White.copy(alpha = 0.8f),
                    )
                )
            },
            actions = {
                // Thêm biểu tượng thông báo bên phải ở đây
                IconButton(
                    onClick = {
                        // Xử lý sự kiện khi nhấn vào biểu tượng thông báo
                    }
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.Yellow)
                }
            },
            backgroundColor = MaterialTheme.colorScheme.scrim,
            elevation = 4.dp
        )

        // Đặt nội dung phía dưới TopAppBar
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(top = 56.dp)) { // Dịch chuyển nội dung xuống dưới TopAppBar
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Làm quen tiếng Nhật")
                    MojiSection(mojiList, navController)
                    GrammarSection(grammarList, navController)
                    VocabularySection(vocabularyList, navController)
                    SectionTitle("Bảng chữ Kanji")
                    KanjiSection(kanjiList, navController)

                    SectionTitle("Luyện tập")
                    TestSection(testList, navController)
                    KaiwaSection(kaiwaList, navController)

                }
            }
        }
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
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
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
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
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
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
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
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
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
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
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
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
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
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
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
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
                        onTextLayout = {} // Provide an empty lambda

                    )
                }
            }
        }
    }
}

@Composable
fun VocabularySection(vocabularyList: List<String>, navController: NavController) {
    Column {
        vocabularyList.forEach { vocab ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToVocabularyDetailScreen(navController, vocab)
                    },
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.voca), // Thay 'your_vocabulary_icon' bằng id của biểu tượng từ vựng của bạn
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Từ vựng", // Thay đổi văn bản tùy ý
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
                        onTextLayout = {} // Provide an empty lambda
                    )
                }
            }
        }
    }
}
@Composable
fun KaiwaSection(kaiwaList: List<String>, navController: NavController) {
    var kaiwaList by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(true) {
        kaiwaList = firestoreRepository.getKaiwaCollections()
    }

    Column {
        kaiwaList.forEach { kaiwa ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navigateToKaiwaDetailScreen(navController, kaiwa)
                    },
                shape = MaterialTheme.shapes.medium,
                backgroundColor = Color(0xFF1A1A1A) // Màu đen nhạt vừa phải
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nhkk),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Học giao tiếp qua video cùng NHK",
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f), // Màu đen nhạt
                        onTextLayout = {} // Provide an empty lambda
                    )
                }
            }
        }
    }
}

private fun navigateToKaiwaDetailScreen(navController: NavController, kaiwa: String) {
    navController.navigate("kaiwa_detail/$kaiwa")
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
private fun navigateToVocabularyDetailScreen(navController: NavController, vocab: String) {
    navController.navigate("vocab_detail/$vocab")
}
