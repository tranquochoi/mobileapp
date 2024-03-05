package com.example.myaiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myaiapp.ui.theme.MyAIAppTheme
import com.example.myapp.MyApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyAIAppTheme {
              MyApp()
            }
        }
    }
}