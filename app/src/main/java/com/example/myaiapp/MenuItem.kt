package com.example.myaiapp

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val route: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)
