package com.example.myapp

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myaiapp.AddNoteScreen
import com.example.myaiapp.AddScreen
import com.example.myaiapp.Chatbot

import com.example.myaiapp.DetailKanjiScreen
import com.example.myaiapp.DetailNoteScreen
import com.example.myaiapp.DetailScreen
import com.example.myaiapp.DetailTestScreen
import com.example.myaiapp.FirestoreRepository
import com.example.myaiapp.GrammarScreen
import com.example.myaiapp.KaiwaScreen
import com.example.myaiapp.Note
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

val firebaseDatabase = FirebaseDatabase.getInstance()
val databaseReference: DatabaseReference = firebaseDatabase.reference

val firestoreRepository = FirestoreRepository()

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    var selectedNavItem by remember { mutableStateOf("home") }
    var showBottomNav by remember { mutableStateOf(true) } // Thêm biến này để kiểm soát việc ẩn hiện BottomNavigation
    var isQuizStarted by remember { mutableStateOf(false) }


    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavigation(
                    modifier = Modifier
                        .padding(bottom = 42.dp), // Tăng khoảng cách phía dưới BottomNavigation
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.Gray, // Color of unselected icons and text
                    elevation = 16.dp, // Tăng độ nâng của BottomAppBar
                ) {
                    BottomNavigationItem(
                        selected = selectedNavItem == "chat",
                        onClick = {
                            selectedNavItem = "chat"
                            navController.navigate("chat")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = if (selectedNavItem == "chat") MaterialTheme.colorScheme.scrim else LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        }
                    )
                    BottomNavigationItem(
                        selected = selectedNavItem == "add",
                        onClick = {
                            selectedNavItem = "add"
                            navController.navigate("add")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (selectedNavItem == "add") MaterialTheme.colorScheme.primary else LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        }
                    )

                    BottomNavigationItem(
                        selected = selectedNavItem == "home",
                        onClick = {
                            selectedNavItem = "home"
                            navController.navigate("home")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (selectedNavItem == "home") MaterialTheme.colorScheme.primaryContainer else LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        }
                    )

                    BottomNavigationItem(
                        selected = selectedNavItem == "search",
                        onClick = {
                            selectedNavItem = "search"
                            navController.navigate("search")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (selectedNavItem == "search") MaterialTheme.colorScheme.tertiary else LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        }
                    )


                }

            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash_screen", // SplashScreen là màn hình khởi đầu
                    modifier = Modifier.padding(innerPadding)

        ) {

            composable("splash_screen") {
                SplashScreen(navController)
                showBottomNav = false
            } // Đặt SplashScreen ở đây
            composable("home") {
                HomeScreen(navController)

                showBottomNav = true
            }
            composable("search") {
                SearchScreen(navController)
            }
            composable("chat") {
                Chatbot(navController)
            }
            composable(
                "detail/{japaneseWord}/{japaneseReading}/{englishDefinitions}",
                arguments = listOf(
                    navArgument("japaneseWord") { defaultValue = "" },
                    navArgument("japaneseReading") { defaultValue = "" },
                    navArgument("englishDefinitions") { defaultValue = "" }
                )
            ) { backStackEntry ->
                val japaneseWord = backStackEntry.arguments?.getString("japaneseWord") ?: ""
                val japaneseReading = backStackEntry.arguments?.getString("japaneseReading") ?: ""
                val englishDefinitions = backStackEntry.arguments?.getString("englishDefinitions") ?: ""

                val searchResult = SearchResult(japaneseWord, japaneseReading, englishDefinitions)

                DetailSearchScreen(navController, searchResult)
            }

            composable("detail/{moji}") { backStackEntry ->
                val moji = backStackEntry.arguments?.getString("moji")
                DetailScreen(navController, moji)
                showBottomNav = false

            }
            composable("detailkanji/{kanji}") { backStackEntry ->
                val kanji = backStackEntry.arguments?.getString("kanji")
                DetailKanjiScreen(navController,kanji)
                showBottomNav = false

            }
            composable("detailtest/{test}") { backStackEntry ->
                val test = backStackEntry.arguments?.getString("test")
                DetailTestScreen(navController,test)
                showBottomNav = false

            }
            composable("add") { AddScreen(navController, firestoreRepository) }

            composable("addScreen") {
                AddScreen(navController, firestoreRepository = FirestoreRepository())
            }

            composable("addNote") {
                AddNoteScreen(navController, firestoreRepository = FirestoreRepository())

            }


            composable("detailAdd/{title}") { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title")

                // State to store the list of notes
                var notesState by remember { mutableStateOf<List<Note>>(emptyList()) }

                // Use LaunchedEffect to call the suspend function
                LaunchedEffect(Unit) {
                    // Call the suspend function to fetch the list of notes from Firestore
                    val notes = firestoreRepository.getAddCollection()
                    // Store the list of notes in notesState
                    notesState = notes
                }

                // Find the note with the corresponding title
                val note = notesState.find { it.title == title }

                // Check if the note is found
                note?.let { note ->
                    val coroutineScope = rememberCoroutineScope()
                    val onSave: (Note) -> Unit = { updatedNote ->
                        // Save the updated note to Firestore
                        coroutineScope.launch {
                            firestoreRepository.updateNoteInCollection("add", updatedNote)
                        }
                    }

                    // Navigate to the DetailNoteScreen and pass the onSave function
                    DetailNoteScreen(navController, note, onSave)
                    showBottomNav = false

                }
            }
            composable("detailAdd/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")

                // State to store the list of notes
                var notesState by remember { mutableStateOf<List<Note>>(emptyList()) }

                // Use LaunchedEffect to call the suspend function
                LaunchedEffect(Unit) {
                    // Call the suspend function to fetch the list of notes from Firestore
                    val notes = firestoreRepository.getAddCollection()
                    // Store the list of notes in notesState
                    notesState = notes
                }

                // Find the note with the corresponding ID
                val note = notesState.find { it.id == id }

                // Check if the note is found
                note?.let { note ->
                    val coroutineScope = rememberCoroutineScope()
                    val onSave: (Note) -> Unit = { updatedNote ->
                        // Save the updated note to Firestore
                        coroutineScope.launch {
                            firestoreRepository.updateNoteInCollection("add", updatedNote)
                        }
                    }

                    // Navigate to the DetailNoteScreen and pass the onSave function
                    DetailNoteScreen(navController, note, onSave)
                }
            }
            composable("grammar/{grammarName}") { backStackEntry ->
                val grammarName = backStackEntry.arguments?.getString("grammarName")
                grammarName?.let { name ->
                    // Ẩn BottomNavigation khi điều hướng đến màn hình Grammar
                    GrammarScreen(navController = navController, homeName = name)
                }
                showBottomNav = false

            }
            composable("vocab_detail/{vocab}") { backStackEntry ->
                val vocab = backStackEntry.arguments?.getString("vocab")
                vocab?.let { name ->
                    // Ẩn BottomNavigation khi điều hướng đến màn hình Vocabulary
                    VocabularyScreen(navController, name)
                }
                showBottomNav = false

            }

            composable("kaiwa_detail/{kaiwa}") { backStackEntry ->
                val kaiwa = backStackEntry.arguments?.getString("kaiwa")
                kaiwa?.let { name ->
                    // Ẩn BottomNavigation khi điều hướng đến màn hình Vocabulary
                    KaiwaScreen(navController, name)
                }
                showBottomNav = false

            }

        }
    }
}


