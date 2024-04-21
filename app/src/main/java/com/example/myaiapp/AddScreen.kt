    package com.example.myaiapp
    
    import android.annotation.SuppressLint
    import androidx.activity.compose.BackHandler
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.itemsIndexed
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.AlertDialog
    import androidx.compose.material.Button
    import androidx.compose.material.ButtonDefaults
    import androidx.compose.material.Card
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
    import androidx.compose.material.icons.filled.Book
    import androidx.compose.material.icons.filled.Delete
    import androidx.compose.material.icons.filled.Menu

    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.text.font.FontStyle
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import com.google.firebase.Timestamp
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.Locale
    import java.util.TimeZone
    import java.util.UUID


    @Composable
    fun AddScreen(navController: NavController, firestoreRepository: FirestoreRepository) {
        var notesSnapshot by remember { mutableStateOf<List<Note>>(emptyList()) }
        var checkedStates by remember { mutableStateOf<List<Boolean>>(emptyList()) }
        var menuExpanded by remember { mutableStateOf(false) }
        var showCheckboxes by remember { mutableStateOf(false) }
        var isDeleteVisible by remember { mutableStateOf(false) }
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var showDeleteSuccessDialog by remember { mutableStateOf(false) }
        var showClearSelection by remember { mutableStateOf(false) }
        var shouldReload by remember { mutableStateOf(false) }
        var showBookIcon by remember { mutableStateOf(true) } // New state for book icon visibility

        // Variables for book dialog
        var showBookDialog by remember { mutableStateOf(false) }
        var currentNoteIndex by remember { mutableStateOf(0) }
        var showFrontSide by remember { mutableStateOf(true) } // Track front and back side of flashcard

        LaunchedEffect(shouldReload) {
            if (shouldReload) {
                firestoreRepository.getAddCollection().let { notes ->
                    notesSnapshot = notes.reversed()
                    checkedStates = MutableList(notes.size) { false }
                }
                shouldReload = false
            }
        }

        LaunchedEffect(Unit) {
            firestoreRepository.getAddCollection().let { notes ->
                notesSnapshot = notes.reversed()
                checkedStates = MutableList(notes.size) { false }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Ghi chú",
                            style = MaterialTheme.typography.subtitle1,
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
                        // Show book icon if not in selection mode
                        if (showBookIcon && !showCheckboxes) {
                            IconButton(onClick = {
                                // Handle book icon click
                                showBookDialog = true
                            }) {
                                Icon(Icons.Default.Book, contentDescription = "Book", tint = Color.White)
                            }
                        }
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
                                        checkedStates = List(checkedStates.size) { false }
                                        showClearSelection = false
                                        isDeleteVisible = false
                                        showCheckboxes = false
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
                    backgroundColor = Color.Black
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
                                    menuExpanded = false
                                    showCheckboxes = true
                                }) {
                                    Text(text = "Chọn")
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        itemsIndexed(notesSnapshot) { index, note ->
                            NoteItem(
                                note = note,
                                onItemClick = {
                                    navController.navigate("detailAdd/${note.id}")
                                },
                                isChecked = checkedStates.getOrElse(index) { false },
                                onCheckedChange = { isChecked ->
                                    checkedStates = checkedStates.toMutableList().also {
                                        it[index] = isChecked
                                    }
                                    isDeleteVisible = checkedStates.any { it }
                                    showClearSelection = checkedStates.any { it }
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
                                    firestoreRepository.deleteNoteFromCollection(
                                        "add",
                                        notesSnapshot.filterIndexed { index, _ -> checkedStates[index] })
                                    showDeleteConfirmation = false
                                    notesSnapshot =
                                        firestoreRepository.getAddCollection().reversed()
                                    checkedStates = MutableList(notesSnapshot.size) { false }
                                    showCheckboxes = false
                                    showClearSelection = false
                                    showDeleteSuccessDialog = true
                                    isDeleteVisible = false
                                }
                            },
                            onDismiss = { showDeleteConfirmation = false }
                        )
                    }
                    if (showDeleteSuccessDialog) {
                        AutoDismissDialog(onDismiss = { showDeleteSuccessDialog = false })
                    }

                    if (showBookDialog) {
                        AlertDialog(
                            onDismissRequest = { showBookDialog = false },
                            modifier = Modifier.fillMaxWidth().height(550.dp),
                            title = {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().height(500.dp).clickable {
                                            // Khi thẻ được nhấp, chuyển đổi giữa hiển thị mặt trước và mặt sau
                                            showFrontSide = !showFrontSide
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = 8.dp,
                                        backgroundColor = Color.White
                                    ) {
                                        // Hiển thị nội dung tùy thuộc vào mặt trước/mặt sau
                                        Text(
                                            modifier = Modifier.height(500.dp),
                                            text = if (showFrontSide) notesSnapshot.getOrNull(currentNoteIndex)?.title ?: "" else notesSnapshot.getOrNull(currentNoteIndex)?.content ?: "",
                                            textAlign = TextAlign.Center,
                                            style = TextStyle(fontSize = 24.sp),
                                            color = if (showFrontSide) Color.Red else Color.Blue
                                        )
                                    }
                                }
                                // Hiển thị tiêu đề "Mặt trước" hoặc "Mặt sau" tùy thuộc vào trạng thái hiện tại
                                Text(
                                    text = if (showFrontSide) "Mặt trước" else "Mặt sau",
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 16.sp, color = if (showFrontSide) Color.Red else Color.Blue)
                                )
                            },
                            buttons = {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    ) {
                                        Button(
                                            onClick = {
                                                currentNoteIndex++
                                                if (currentNoteIndex >= notesSnapshot.size) {
                                                    currentNoteIndex = 0
                                                }
                                                // Khi người dùng chuyển sang trang mới, luôn hiển thị mặt trước
                                                showFrontSide = true
                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                                        ) {
                                            Text("Tiếp", color = Color.White)
                                        }
                                    }
                                }
                            }
                        )
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
            delay(2000) // Display message for 2 seconds
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
                .clickable { onItemClick() } // Handle click event
        ) {
            Row {
                if (showCheckboxes) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onCheckedChange(it) },
                        modifier = Modifier.padding(end = 8.dp) // Spacing between checkbox and content
                    )
                }
                Column {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold) // Bold and large text
                    )
                    Text(
                        text = note.content
                    )
                    Text(
                        text = formatTimestamp(note.timestamp),
                        style = MaterialTheme.typography.body2.copy(fontStyle = FontStyle.Italic) // Italic text
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red) // Red border color for delete button
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black) // Black border color for cancel button
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
                    title = { Text(text = "") }, // Không hiển thị tiêu đề
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                // Kiểm tra nếu cả tiêu đề và nội dung đều không rỗng
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    val newNote = Note(
                                        id = UUID.randomUUID().toString(), // Tạo ID mới cho ghi chú
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
    fun DetailNoteScreen(navController: NavController, note: Note, onSave: (Note) -> Unit) {
        var title by remember { mutableStateOf(note.title) }
        var content by remember { mutableStateOf(note.content ?: "") }

        // Handle back button press event
        BackHandler {
            // Save the note when the back button is pressed
            onSave(Note(id = note.id, title = title, content = content)) // Chuyển cả ID khi lưu ghi chú
            // Navigate back
            navController.popBackStack()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Save the note when the navigation icon is clicked
                            onSave(Note(id = note.id, title = title, content = content)) // Chuyển cả ID khi lưu ghi chú
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White,
                    elevation = 0.dp
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
                    placeholder = { Text("Title", style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold)) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Bold),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Content") },
                    textStyle = TextStyle(fontSize = 16.sp),
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
