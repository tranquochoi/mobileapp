package com.example.myapp

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myaiapp.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun VocabularyScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var vocab1Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var vocab2Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var vocab3Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var vocab4Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var vocab5Documents by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            vocab1Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab1")
            vocab2Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab2")
            vocab3Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab3")
            vocab4Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab4")
            vocab5Documents = firestoreRepository.getVocabularyDocuments(homeName, "vocab5")
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    var playAudioClicked by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) } // Trạng thái của âm thanh đang phát
    var showBookDialog by remember { mutableStateOf(false) } // State để điều khiển việc hiển thị hộp thoại
    var expanded by remember { mutableStateOf(false) } // State để theo dõi trạng thái mở / đóng của DropdownMenu

    val vocabularyLists = listOf(vocab1Documents, vocab2Documents, vocab3Documents, vocab4Documents, vocab5Documents)
    val selectedTabText = remember { mutableStateOf("Bài 1") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Từ vựng",
                    onTextLayout = {},
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White // Set text color to white
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) // Set icon color to white
                }
            },
            actions = {
                // Thêm biểu tượng hình ảnh play vào phía bên trái của TopAppBar
                IconButton(
                    onClick = {
                        // Hiển thị hộp thoại dữ liệu từ vựng khi người dùng nhấp vào icon book
                        showBookDialog = true
                    }
                ) {
                    Icon(Icons.Default.Book, contentDescription = "Book", tint = Color.White)
                }
            },
            backgroundColor = Color(0xFFE4B4BF),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown menu
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp) // Thêm padding cho khoảng cách với màn hình
                .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small) // Thêm viền đen
        ) {
            Text(
                text = selectedTabText.value,
                modifier = Modifier
                    .clickable(onClick = { expanded = true }) // Khi click vào text, mở dropdown menu
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DropdownMenu(
                expanded = expanded, // Sử dụng state để mở / đóng dropdown menu
                onDismissRequest = { expanded = false }
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedTabIndex = 0
                            selectedTabText.value = "Bài 1"
                            expanded = false // Khi chọn một mục, đóng dropdown menu
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Đảm bảo rằng mỗi mục trong dropdown menu sẽ có chiều rộng tối đa
                        .background(Color.White)
                ) {
                    Text(
                        text = "Bài 1",
                        style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedTabIndex = 1
                            selectedTabText.value = "Bài 2"
                            expanded = false // Khi chọn một mục, đóng dropdown menu
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Đảm bảo rằng mỗi mục trong dropdown menu sẽ có chiều rộng tối đa
                        .background(Color.White)
                ) {
                    Text(
                        text = "Bài 2",
                        style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedTabIndex = 2
                            selectedTabText.value = "Bài 3"
                            expanded = false // Khi chọn một mục, đóng dropdown menu
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Đảm bảo rằng mỗi mục trong dropdown menu sẽ có chiều rộng tối đa
                        .background(Color.White)
                ) {
                    Text(
                        text = "Bài 3",
                        style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedTabIndex = 3
                            selectedTabText.value = "Bài 4"
                            expanded = false // Khi chọn một mục, đóng dropdown menu
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Đảm bảo rằng mỗi mục trong dropdown menu sẽ có chiều rộng tối đa
                        .background(Color.White)
                ) {
                    Text(
                        text = "Bài 4",
                        style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clickable {
                            selectedTabIndex = 4
                            selectedTabText.value = "Bài 5"
                            expanded = false // Khi chọn một mục, đóng dropdown menu
                        }
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Đảm bảo rằng mỗi mục trong dropdown menu sẽ có chiều rộng tối đa
                        .background(Color.White)
                ) {
                    Text(
                        text = "Bài 5",
                        style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (showBookDialog) {
            VocabularyDataDialog(
                vocabularyDocuments = vocabularyLists[selectedTabIndex],
                onDismiss = { showBookDialog = false }
            )
        }

        if (playAudioClicked) {
            val audioUrls = vocabularyLists[selectedTabIndex].mapNotNull { it.getString("audio") }
            if (audioUrls.isNotEmpty()) {
                playAudioUrls(audioUrls)
            }
        }

        VocabularyList(vocabularyDocuments = vocabularyLists[selectedTabIndex], navController = navController, isPlaying = isPlaying)
    }
}

@Composable
fun VocabularyDataDialog(
    vocabularyDocuments: List<DocumentSnapshot>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentDocument = vocabularyDocuments.getOrNull(currentIndex)

    // Nội dung mặt trước và mặt sau của thẻ từ vựng
    val frontContent = currentDocument?.getString("kotoba") ?: ""
    val backContent = currentDocument?.getString("go") ?: ""

    FlashcardDialog(
        onDismissRequest = onDismiss,
        frontContent = frontContent,
        backContent = backContent
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Button(
                    onClick = {
                        currentIndex = (currentIndex + 1) % vocabularyDocuments.size
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    enabled = vocabularyDocuments.size > 1,
                    modifier = Modifier
                ) {
                    Text("Tiếp", color = Color.White)
                }
            }
        }

    }
}

@Composable
fun FlashcardDialog(
    onDismissRequest: () -> Unit,
    frontContent: String,
    backContent: String,
    confirmButtonContent: @Composable () -> Unit
) {
    var showFrontSide by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .clickable {
                            showFrontSide = !showFrontSide
                        },
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp,
                    backgroundColor = Color.White
                ) {
                    Text(
                        modifier = Modifier.height(500.dp),
                        text = if (showFrontSide) frontContent else backContent,
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 24.sp),
                        color = if (showFrontSide) Color.Red else Color.Blue
                    )
                }
            }
            Text(
                text = if (showFrontSide) "Mặt trước" else "Mặt sau",
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 16.sp, color = if (showFrontSide) Color.Red else Color.Blue)
            )
        },
        buttons = {
            confirmButtonContent()
        }
    )
}




@Composable
fun VocabularyList(vocabularyDocuments: List<DocumentSnapshot>, navController: NavController, isPlaying: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        items(vocabularyDocuments) { document ->
            VocabularyItem(document = document, navController = navController, isPlaying = isPlaying)
        }
    }
}

@Composable
fun VocabularyItem(document: DocumentSnapshot, navController: NavController, isPlaying: Boolean) {
    val kotoba = document.getString("kotoba") ?: ""
    val kanji = document.getString("kanji") ?: ""
    val go = document.getString("go") ?: ""
    val romaji = document.getString("romaji") ?: ""
    val audioUrl = document.getString("audio")
    var itemIsPlaying by remember { mutableStateOf(isPlaying) } // Sử dụng biến var để theo dõi trạng thái
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                audioUrl?.let { url ->
                    try {
                        val mediaPlayer = MediaPlayer().apply {
                            setDataSource(url)
                            prepareAsync()
                            setOnPreparedListener {
                                it.start()
                                // Cập nhật trạng thái khi âm thanh bắt đầu phát
                                itemIsPlaying = true
                            }
                            setOnCompletionListener {
                                // Cập nhật trạng thái khi âm thanh kết thúc
                                itemIsPlaying = false
                            }
                        }
                    } catch (e: IOException) {
                        // Xử lý lỗi khi phát âm thanh
                    }
                }
            }
            .border(1.dp, if (itemIsPlaying) Color.Red else Color.Black, RoundedCornerShape(8.dp)),
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
            style = MaterialTheme.typography.subtitle1,
            color = Color.Red, // Đổi màu sang đỏ
            modifier = Modifier.padding(end = 4.dp),
            onTextLayout = {}
        )
        Text(
            text = content,
            style = MaterialTheme.typography.subtitle1,
            color = Color.Blue,
        )
    }
}

@Composable
fun playAudioUrls(audioUrls: List<String>) {
    var currentAudioIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            currentAudioIndex++
            if (currentAudioIndex < audioUrls.size) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(audioUrls[currentAudioIndex])
                mediaPlayer.prepare()
                mediaPlayer.start()
            } else {
                mediaPlayer.release()
            }
        }
        mediaPlayer.setDataSource(audioUrls[currentAudioIndex])
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}
