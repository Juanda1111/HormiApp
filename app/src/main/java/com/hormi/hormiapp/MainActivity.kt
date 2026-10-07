package com.hormi.hormiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import com.hormi.hormiapp.navigation.HormiAppNavigation
import com.hormi.hormiapp.ui.theme.HormiAppTheme
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.util.LocalCurrency

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Ocultar la barra de navegación del sistema (Modo Inmersivo)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            val theme by preferencesRepository.theme.collectAsState(initial = "Sistema")
            val currency by preferencesRepository.currency.collectAsState(initial = "COP $")
            val darkTheme = when (theme) {
                "Oscuro" -> true
                "Claro" -> false
                else -> isSystemInDarkTheme()
            }
            HormiAppTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalCurrency provides currency) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HormiAppNavigation()
                    }
                }
            }
        }
    }
}