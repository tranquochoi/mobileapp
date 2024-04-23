
package com.example.myaiapp

import JishoApiService
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myaiapp.ui.theme.MyAIAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen() {
    val apiService = JishoApiService.create()
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    MyAIAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Tìm kiếm",
                            style = MaterialTheme.typography.subtitle1,

                    ) },
                    backgroundColor = Color.Black,
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    placeholder = { Text("Nhập từ tiếng Anh, ví dụ: hello") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Button(
                        onClick = {
                            // Sử dụng coroutines để gọi searchWords
                            GlobalScope.launch(Dispatchers.Main) {
                                try {
                                    val response = apiService.searchWords(" $query")
                                    result = processResponse(response)
                                } catch (e: Exception) {
                                    result = "Error fetching data"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Search")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    item {
                        Text(
                            text = result,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}


private fun processResponse(response: JishoApiResponse): String {
    val stringBuilder = StringBuilder()

    for (word in response.data) {
        val japaneseWord = word.japanese.firstOrNull()?.word ?: ""
        val englishDefinitions =
            word.senses.flatMap { it.english_definitions }.joinToString(", ")

        stringBuilder.append("Japanese: $japaneseWord\n")
        stringBuilder.append("English Definitions: $englishDefinitions\n\n")
    }

    return stringBuilder.toString()
}
