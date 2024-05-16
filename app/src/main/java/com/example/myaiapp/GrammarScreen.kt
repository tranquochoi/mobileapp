package com.example.myaiapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentSnapshot

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun GrammarScreen(navController: NavController, homeName: String?) {
    val firestoreRepository = FirestoreRepository()

    var grammarDocuments by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(homeName) {
        if (!homeName.isNullOrEmpty()) {
            grammarDocuments = firestoreRepository.getGrammarDocuments(homeName)
        }
    }

    CompositionLocalProvider(LocalContentColor provides Color.Black) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Ngữ pháp",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White // Set text color to white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // Set icon color to white
                        )
                    }
                },
                backgroundColor = Color(0xFFE4B4BF), // Set background color to black
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Add spacing between TopAppBar and content
            Spacer(modifier = Modifier.height(4.dp))

            // Hiển thị Tab và ViewPager
            GrammarTabs(navController = navController, grammarDocuments = grammarDocuments)
        }
    }
}
@Composable
fun GrammarTabs(navController: NavController, grammarDocuments: List<DocumentSnapshot>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) } // State để theo dõi trạng thái mở / đóng của DropdownMenu

    val items = grammarDocuments.mapIndexed { index, document ->
        "Bài ${index + 1}" to index
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (grammarDocuments.isNotEmpty()) { // Kiểm tra xem danh sách có rỗng không
            // Hiển thị dropdown menu
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small) // Thêm viền đen
            ) {
                Text(
                    text = items[selectedTabIndex].first,
                    modifier = Modifier
                        .clickable(onClick = { expanded = true }) // Khi click vào text, mở dropdown menu
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                DropdownMenu(
                    expanded = expanded, // Sử dụng state để mở / đóng dropdown menu
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { (title, index) ->
                        Box(
                            modifier = Modifier
                                .clickable {
                                    selectedTabIndex = index
                                    expanded = false // Khi chọn một mục, đóng dropdown menu
                                }
                                .padding(vertical = 8.dp)
                                .background(Color.White)
                        ) {
                            Text(
                                text = title,
                                style = TextStyle(color = Color.Black), // Sử dụng TextStyle để thiết lập màu chữ
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hiển thị nội dung tương ứng với tab được chọn
        grammarDocuments.getOrNull(selectedTabIndex)?.let { document ->
            GrammarItem(document = document, navController = navController)
        }
    }
}


@Composable
fun GrammarItem(document: DocumentSnapshot, navController: NavController) {
    val grammar = document.getString("gram")

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent, // Đặt màu nền là trong suốt
            ) {
                Column(
                    modifier = Modifier.padding(4.dp)
                ) {
                    grammar?.let {
                        val processedGrammar = processGrammarText(it)
                        Text(
                            text = processedGrammar,
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun processGrammarText(text: String): AnnotatedString {

    val builder = AnnotatedString.Builder()

    val sentences = text.split(". ")

    sentences.forEachIndexed { index, sentence ->
        when {
            "Chú ý" in sentence -> {
                builder.withStyle(style = SpanStyle(color = Color.Red)) {
                    append(sentence)
                }
            }
            "Động từ" in sentence -> {
                builder.withStyle(style = SpanStyle(color = Color.Blue)) {
                    append(sentence)
                }
            }
            "Danh từ" in sentence -> {
                builder.withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append(sentence)
                }
            }
            else -> {
                builder.append(sentence)
            }
        }
        if (index < sentences.size - 1) {
            builder.append(".\n") // Thêm dấu xuống dòng sau mỗi câu
        }
    }

    return builder.toAnnotatedString()
}
