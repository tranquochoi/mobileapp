package com.example.myaiapp

sealed class NavigationScreens(val route: String) {
    object Home : NavigationScreens("home")
    object Detail : NavigationScreens("detail")
    object DetailKanji : NavigationScreens("detailkanji")
    object DetailTest : NavigationScreens("detailtest")
}