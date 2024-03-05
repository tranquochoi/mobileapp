package com.example.myapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myaiapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var mojiList by remember { mutableStateOf(emptyList<String>()) }
    var kanjiList by remember { mutableStateOf(emptyList<String>()) }
    var testList by remember { mutableStateOf(emptyList<String>()) }


    LaunchedEffect(true) {
        mojiList = firestoreRepository.getHomeCollections()
        kanjiList = firestoreRepository.getHomeKajiCollections()
        testList = firestoreRepository.getHomeTestCollections()

    }

    Column {
        Column {
            mojiList.forEach { moji ->
                Card(
                    onClick = {
                        navigateToDetailScreen(navController, moji)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(72.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            navigateToDetailScreen(navController, moji)
                        },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.moji),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                        )
                        Text(
                            text = "Bảng chữ Hiragana + Katakana",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(end = 18.dp)
                        )
                    }
                }
            }
        }

        Column {
            kanjiList.forEach { kanji ->
                Card(
                    onClick = {
                        navigateToDetailKanjiScreen(navController, kanji)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(72.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            navigateToDetailKanjiScreen(navController, kanji)
                        },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.kanji),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                        )
                        Text(
                            text = "Bảng chữ Kanji",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(end = 138.dp)
                        )
                    }
                }
            }
        }
        Column {
            testList.forEach { test ->
                Card(
                    onClick = {
                        navigateToTestScreen(navController, test)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(72.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            navigateToTestScreen(navController, test)
                        },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.test),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                        )
                        Text(
                            text = "Quiz",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(end = 218.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun navigateToDetailScreen(navController: NavController, moji: String) {
    navController.navigate("detail/$moji")
}

private fun navigateToDetailKanjiScreen(navController: NavController, kanji: String) {
    navController.navigate("detailkanji/$kanji")
}
private fun navigateToTestScreen(navController: NavController, test: String) {
    navController.navigate("detailtest/$test")
}
