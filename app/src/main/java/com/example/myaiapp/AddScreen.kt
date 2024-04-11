// AddScreen.kt
package com.example.myaiapp

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }

    // Use LaunchedEffect to launch a coroutine and fetch data asynchronously
    LaunchedEffect(Unit) {
        // Inside LaunchedEffect, you can call suspend functions
        notes = firestoreRepository.getAddCollection()
    }

    Scaffold(
        topBar = {
            // TopAppBar content, if any
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addNote")
                },
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) {
        LazyColumn {
            items(notes) { note ->
                NoteItem(note = note, onItemClick = {
                    navController.navigate("detailAdd/${note.title}")
                })
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
@Composable
fun NoteItem(note: Note, onItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() } // Handle click event
    ) {
        Text(text = "${note.title}", fontWeight = FontWeight.Bold, fontSize = 28.sp,     onTextLayout = {}, // hoặc null nếu không cần
        )
        Text(text = "${note.content}",     onTextLayout = {}, // hoặc null nếu không cần
        )
        Text(text = "${formatTimestamp(note.timestamp)}",     onTextLayout = {}, // hoặc null nếu không cần
        )
    }
}


private fun formatTimestamp(timestamp: Date?): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    return timestamp?.let { dateFormat.format(it) } ?: "N/A"
}
@Composable
fun AddNoteScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input fields for adding a new note
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title",     onTextLayout = {}, // hoặc null nếu không cần
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content",    onTextLayout = {}, // hoặc null nếu không cần
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Button to add a new note
        Button(
            onClick = {
                coroutineScope.launch {
                    val newNote = Note(
                        title = title,
                        content = content,
                        timestamp = Date()
                    )
                    firestoreRepository.addNoteToCollection("add", newNote)
                    // Clear input fields
                    title = ""
                    content = ""
                    // Navigate back to the previous screen
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Add Note",    onTextLayout = {}, // hoặc null nếu không cần
            )
        }
    }
}
// DetailScreen.kt
@Composable
fun DetailAddScreen(note: Note) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "${note.title}", fontSize = 20.sp, fontWeight = FontWeight.Bold,    onTextLayout = {}, // hoặc null nếu không cần
        )
        Text(text = "${note.content}", fontSize = 16.sp,    onTextLayout = {}, // hoặc null nếu không cần
        )
        Text(text = "${formatTimestamp(note.timestamp)}", fontSize = 14.sp, fontStyle = FontStyle.Italic,    onTextLayout = {}, // hoặc null nếu không cần
        )
    }
}
