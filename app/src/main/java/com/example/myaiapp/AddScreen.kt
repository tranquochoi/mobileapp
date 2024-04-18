package com.example.myaiapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showClearSelection by remember { mutableStateOf(false) } // Trạng thái của chữ "Bỏ chọn" trên TopAppBar

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
                    Text(
                        text = "Ghi chú",
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
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
                    if (showClearSelection) {
                        Text(
                            text = "Bỏ chọn",
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    // Clear all checkbox selections
                                    checkedStates = List(checkedStates.size) { false }
                                    showClearSelection = false
                                    // Reset other states as needed
                                    isDeleteVisible = false // Ẩn nút xóa
                                    showCheckboxes = false // Ẩn checkbox
                                }
                                .padding(horizontal = 8.dp)
                        )
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
                                showClearSelection = checkedStates.any { it } // Hiển thị chữ "Bỏ chọn" nếu có ít nhất một checkbox được tích
                            },
                            showCheckboxes = showCheckboxes,
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
                                showClearSelection = false

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
    showCheckboxes: Boolean,

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick()
            } // Handle click event
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
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red) // Màu viền đỏ cho nút xóa
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black) // Màu viền đen cho nút hủy bỏ
            ) {
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddNoteScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Không hiển thị tiêu đề
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            // Kiểm tra nếu cả tiêu đề và nội dung đều không rỗng
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val newNote = Note(
                                    title = title,
                                    content = content,
                                    timestamp = Timestamp.now() // Use Timestamp.now() to get the current time
                                )
                                firestoreRepository.addNoteToCollection("add", newNote)
                            }
                            // Clear input fields
                            title = ""
                            content = ""
                            // Navigate back to the previous screen
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White, // Set background color to black
                elevation = 0.dp // Remove the elevation to eliminate the shadow
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Tiêu đề", style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold)) }, // Thiết lập fontSize và fontWeight
                singleLine = true,
                textStyle = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold), // Thiết lập fontSize và fontWeight
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )

            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Ghi chú") }, // Thiết lập fontSize và fontWeight
                textStyle = TextStyle(fontSize = 16.sp), // Thiết lập fontSize và fontWeight
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailNoteScreen(navController: NavController, note: Note) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = note.title ?: "Chi tiết ghi chú",
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.Black,
                elevation = 0.dp
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = note.content ?: "Nội dung ghi chú",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

