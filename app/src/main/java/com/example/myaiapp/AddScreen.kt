package com.example.myaiapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var menuExpanded by remember { mutableStateOf(false) } // Trạng thái của menu mở rộng hay thu gọn
    var showCheckboxes by remember { mutableStateOf(false) } // Trạng thái của checkbox hiển thị hay ẩn
    var checkedStates by remember { mutableStateOf<List<Boolean>>(emptyList()) } // Trạng thái của checkbox
    var isDeleteVisible by remember { mutableStateOf(false) } // Trạng thái của nút xóa
    var showDeleteConfirmation by remember { mutableStateOf(false) } // Trạng thái của hộp thoại xác nhận xóa
    var showDeleteSuccessDialog by remember { mutableStateOf(false) }

    // Use LaunchedEffect to launch a coroutine and fetch data asynchronously
    LaunchedEffect(Unit) {
        // Inside LaunchedEffect, you can call suspend functions
        notes = firestoreRepository.getAddCollection().reversed() // Sắp xếp danh sách ngược lại
        checkedStates = MutableList(notes.size) { false } // Khởi tạo trạng thái của checkbox
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Ghi chú",
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Black, // Set background color to black
                actions = {
                    if (isDeleteVisible) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addNote")
                },
                modifier = Modifier.padding(bottom = 8.dp),
                backgroundColor = Color.Black // Đặt màu đen cho nút
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                    tint = Color.White
                )
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row {
                    if (menuExpanded) {
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                // Xử lý sự kiện khi lựa chọn
                                menuExpanded = false
                                showCheckboxes = true // Hiển thị checkbox khi chọn từ menu
                            }) {
                                Text(text = "Chọn")
                            }
                            // Thêm các lựa chọn khác nếu cần
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp) // Khoảng cách giữa topAppBar và nội dung phía dưới
                ) {
                    itemsIndexed(notes) { index, note ->
                        NoteItem(
                            note = note,
                            onItemClick = {
                                navController.navigate("detailAdd/${note.title}")
                            },
                            isChecked = checkedStates.getOrElse(index) { false },
                            onCheckedChange = { isChecked ->
                                checkedStates = checkedStates.toMutableList().also {
                                    it[index] = isChecked
                                }
                                isDeleteVisible = checkedStates.any { it } // Hiển thị nút xóa khi có ít nhất một checkbox được tích
                            },
                            showCheckboxes = showCheckboxes
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                val coroutineScope = rememberCoroutineScope()

                if (showDeleteConfirmation) {
                    DeleteConfirmationDialog(
                        onDeleteConfirmed = {
                            coroutineScope.launch {
                                // Xóa ghi chú từ Firestore
                                firestoreRepository.deleteNoteFromCollection("add", notes.filterIndexed { index, _ -> checkedStates[index] })
                                showDeleteConfirmation = false

                                // Cập nhật lại danh sách ghi chú sau khi xóa
                                notes = firestoreRepository.getAddCollection().reversed()

                                // Reset trạng thái của checkbox
                                checkedStates = MutableList(notes.size) { false }

                                showCheckboxes = false

                                showDeleteSuccessDialog = true

                                // Ẩn nút xóa sau khi đã xóa
                                isDeleteVisible = false
                            }
                        },
                        onDismiss = { showDeleteConfirmation = false }
                    )
                }
                if (showDeleteSuccessDialog) {
                    AutoDismissDialog(onDismiss = { showDeleteSuccessDialog = false })
                }

            }
        }
    )
}
@Composable
fun AutoDismissDialog(
    onDismiss: () -> Unit
) {
    LaunchedEffect(true) {
        delay(2000) // Thời gian hiển thị thông báo (2 giây)
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = { Text("Bạn đã xóa ghi chú thành công.") },
        buttons = {}
    )
}


@Composable
fun NoteItem(
    note: Note,
    onItemClick: () -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showCheckboxes: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() } // Handle click event
    ) {
        Row {
            if (showCheckboxes) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onCheckedChange(it) },
                    modifier = Modifier.padding(end = 8.dp) // Khoảng cách giữa checkbox và nội dung
                )
            }
            Column {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold) // Chữ to và in đậm
                )
                Text(
                    text = note.content
                )
                Text(
                    text = formatTimestamp(note.timestamp),
                    style = MaterialTheme.typography.body2.copy(fontStyle = FontStyle.Italic) // Chữ nghiêng
                )
            }

        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("THÔNG BÁO") },
        text = { Text("Bạn có chắc chắn muốn xóa ghi chú này không?") },
        confirmButton = {
            Button(
                onClick = {
                    onDeleteConfirmed()
                    onDismiss()
                }
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy bỏ")
            }
        }
    )
}
private fun formatTimestamp(timestamp: Timestamp?): String {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    return timestamp?.toDate()?.let { dateFormat.format(it) } ?: "N/A"
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
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
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
                        timestamp = Timestamp.now() // Sử dụng Timestamp.now() để lấy thời gian hiện tại
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
            Text("Add Note")
        }
    }
}
