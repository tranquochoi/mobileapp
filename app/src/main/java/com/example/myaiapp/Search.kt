package com.example.myapp

import JishoApiService
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.JishoApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun SearchScreen(navController: NavController) {
        val apiService = remember { JishoApiService.create() }
        var query by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<JishoApiResponse?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Tìm kiếm", style = MaterialTheme.typography.subtitle1)
                    },
                    backgroundColor = Color(0xFFE4B4BF),
                    contentColor = Color.White,
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Nhập từ tiếng Anh, ví dụ: hello") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black, // Màu của con trỏ
                        focusedIndicatorColor = Color.Black, // Màu của gạch dưới khi TextField có focus
                        unfocusedIndicatorColor = Color.Black // Màu của gạch dưới khi TextField không có focus
                    )
                )

                Button(
                    onClick = {
                        GlobalScope.launch(Dispatchers.Main) {
                            try {
                                val response = apiService.searchWords(query)
                                result = response
                            } catch (e: Exception) {
                                result = null
                                // Handle error
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Black, // Màu nền đen
                        contentColor = Color.White // Màu chữ trắng
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Search")
                }

                result?.let { response ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        items(response.data) { word ->
                            val japaneseWord = word.japanese.firstOrNull()?.word ?: ""
                            val japaneseReading = word.japanese.firstOrNull()?.reading ?: "" // Lấy giá trị reading từ JishoJapanese
                            val englishDefinitions = word.senses.flatMap { it.english_definitions }.joinToString(", ")

                            DetailSearchScreen(navController, japaneseWord, japaneseReading, englishDefinitions) // Truyền japaneseReading vào DetailSearchScreen
                        }
                    }
                }

            }
        }
    }
@Composable
fun DetailSearchScreen(
    navController: NavController,
    japaneseWord: String,
    japaneseReading: String, // Nhận thêm tham số japaneseReading
    englishDefinitions: String
)
    {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Japanese: $japaneseWord",
            modifier = Modifier.clickable {
                // Navigation to detail screen
                navController.navigate("detail/$japaneseWord/$japaneseReading/$englishDefinitions")
            }
        )
        Text(
            text = "Reading: $japaneseReading", // Hiển thị giá trị japaneseReading
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "English Definitions: $englishDefinitions",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun processResponse(response: JishoApiResponse): List<Triple<String, String, String>> {
    val result = mutableListOf<Triple<String, String, String>>()

    for (word in response.data) {
        val japaneseWord = word.japanese.firstOrNull()?.word ?: ""
        val japaneseReading = word.japanese.firstOrNull()?.reading ?: "" // Lấy giá trị reading từ JishoJapanese
        val englishDefinitions =
            word.senses.flatMap { it.english_definitions }.joinToString(", ")

        result.add(Triple(japaneseWord, japaneseReading, englishDefinitions))
    }

    return result
}
