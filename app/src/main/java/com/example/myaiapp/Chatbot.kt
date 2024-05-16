package com.example.myaiapp

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Chatbot(navController: NavController) {
    val model = remember {
        GenerativeModel(
            "gemini-1.5-pro-latest",
            // Thay "YOUR_API_KEY" bằng key API của bạn
            "AIzaSyDw_JLC3UeofVnXn6ildz4l_9nrpEbveAI",
            generationConfig = generationConfig {
                temperature = 1f
                topK = 0
                topP = 0.95f
                maxOutputTokens = 8192
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            )
        )
    }

    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf<List<Pair<String, Content>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat Icon",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 32.dp)
                        )
                        Text(text = "Gemini", color = Color.White)
                    }
                },
                backgroundColor = Color.Black
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                chatHistory.forEach { (question, answer) ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(text = "You", color = Color.Gray, style = MaterialTheme.typography.caption)
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .wrapContentWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Gray
                                ) {
                                    Text(
                                        text = question,
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = "Chatbot", color = Color.Gray, style = MaterialTheme.typography.caption)
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .wrapContentWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.LightGray
                                ) {
                                    Text(
                                        text = answer.parts.firstOrNull()?.asTextOrNull() ?: "",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                if (isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            color = Color.Black
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusModifier(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            keyboardController?.hide()
                            if (userInput.isNotBlank()) {
                                val currentUserInput = userInput
                                chatHistory = chatHistory + (currentUserInput to Content(parts = listOf(TextPart(""))))
                                userInput = ""
                                isLoading = true
                                coroutineScope.launch {
                                    val chat = model.startChat(chatHistory.map { it.second })
                                    val response = chat.sendMessage(currentUserInput)
                                    chatHistory = chatHistory.dropLast(1) + (currentUserInput to (response.candidates.firstOrNull()?.content
                                        ?: Content(parts = listOf(TextPart("")))))
                                    isLoading = false
                                }
                            }
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    placeholder = {
                        Text("Type something...")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                if (userInput.isNotBlank()) {
                                    val currentUserInput = userInput
                                    chatHistory = chatHistory + (currentUserInput to Content(parts = listOf(TextPart(""))))
                                    userInput = ""
                                    isLoading = true
                                    coroutineScope.launch {
                                        val chat = model.startChat(chatHistory.map { it.second })
                                        val response = chat.sendMessage(currentUserInput)
                                        chatHistory = chatHistory.dropLast(1) + (currentUserInput to (response.candidates.firstOrNull()?.content
                                            ?: Content(parts = listOf(TextPart("")))))
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        }
    }
}
