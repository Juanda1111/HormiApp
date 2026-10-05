package com.hormi.hormiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hormi.hormiapp.ui.theme.HormiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HormiAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Navigation will be set up here later
                }
            }
        }
    }
}