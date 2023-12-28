package com.example.myapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myaiapp.AddNoteScreen
import com.example.myaiapp.AddScreen
import com.example.myaiapp.DetailKanjiScreen
import com.example.myaiapp.DetailTestScreen
import com.example.myaiapp.Favorite
import com.example.myaiapp.FirestoreRepository
import com.example.myaiapp.SearchScreen
import com.example.myaiapp.Settings
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

val firebaseDatabase = FirebaseDatabase.getInstance()
val databaseReference: DatabaseReference = firebaseDatabase.reference

val firestoreRepository = FirestoreRepository()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    var selectedNavItem by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The RyNa",
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("search")
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("settings")
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigation(
                modifier = Modifier,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Gray, // Color of unselected icons and text
                elevation = 8.dp,
            ) {
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
                    selected = selectedNavItem == "favorite",
                    onClick = {
                        selectedNavItem = "favorite"
                        navController.navigate("favorite")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (selectedNavItem == "favorite") MaterialTheme.colorScheme.error else LocalContentColor.current.copy(alpha = 0.6f)
                        )
                    }
                )

            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("search") { SearchScreen() }
            composable("favorite") { Favorite() }
            composable("settings") { Settings() }
            composable("detail/{moji}") { backStackEntry ->
                val moji = backStackEntry.arguments?.getString("moji")
                DetailScreen(navController, moji)
            }
            composable("detailkanji/{kanji}") { backStackEntry ->
                val kanji = backStackEntry.arguments?.getString("kanji")
                DetailKanjiScreen(navController,kanji)
            }
            composable("detailtest/{test}") { backStackEntry ->
                val test = backStackEntry.arguments?.getString("test")
                DetailTestScreen(navController,test)
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

                // Use LaunchedEffect to launch a coroutine and fetch data asynchronously
                LaunchedEffect(title) {
                    val notes = firestoreRepository.getAddCollection()
                    val note = notes.find { it.title == title }
                    if (note != null) {
                    } else {
                        // Handle error or navigate back
                        navController.popBackStack()
                    }
                }
            }

        }
    }
}