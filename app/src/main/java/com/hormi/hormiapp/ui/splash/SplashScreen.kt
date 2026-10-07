package com.hormi.hormiapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.R
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegistro: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(key1 = destination) {
        delay(2000L) // 2 segundos de animación visual del Splash
        when (destination) {
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Registro -> onNavigateToRegistro()
            null -> { /* Aún cargando el estado de la base de datos */ }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryGreen)
            .systemBarsPadding(), // <- Evita solapamiento con botones del sistema
        contentAlignment = Alignment.Center
    ) {
        // Contenido central (Logo y textos)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo (Círculo amarillo con hormiga)
            Image(
                painter = painterResource(id = R.drawable.hormiapp_logo),
                contentDescription = "Logo HormiApp",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Título
            Text(
                text = "HormiApp",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Subtítulo
            Text(
                text = "Cuida cada peso, hasta el más pequeño",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp
            )
        }

        // Texto inferior (Privacidad)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Privacidad",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tus datos se guardan solo en este celular",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}
