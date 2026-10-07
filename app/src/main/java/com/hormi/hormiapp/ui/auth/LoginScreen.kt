package com.hormi.hormiapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        HormiAppHeader(
            title = "Iniciar sesión"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Titles
        Text(
            text = "¡Hola de nuevo!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingresa con tu PIN",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mostrar Error si lo hay
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Nombre de usuario (se sugiere la última cuenta usada, pero se puede cambiar)
        Text(text = "Nombre", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreChange,
            placeholder = { Text("Tu nombre", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = PrimaryGreen,
                unfocusedIndicatorColor = Color.LightGray,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN
        Text(text = "PIN (4 dígitos)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.pin,
            onValueChange = viewModel::onPinChange,
            placeholder = { Text("4 dígitos", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (uiState.pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = viewModel::togglePinVisibility) {
                    Icon(
                        imageVector = if (uiState.pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle PIN visibility",
                        tint = Color.Gray
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = PrimaryGreen,
                unfocusedIndicatorColor = Color.LightGray,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ¿Olvidaste tu PIN?
        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿Olvidaste tu PIN?",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Ingresar
        Button(
            onClick = {
                viewModel.onLoginClick(onSuccess = onLoginSuccess)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text(text = "Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Seguridad",
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Tus datos se guardan solo en este celular.",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ir a Registro
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
