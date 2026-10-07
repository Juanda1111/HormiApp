package com.hormi.hormiapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
fun RecoverPinScreen(
    onRecoverSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RecoverPinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        HormiAppHeader(
            title = "Recuperar PIN",
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Titles
        Text(
            text = "Vamos a recuperarlo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Responde tu pregunta de seguridad para crear un nuevo PIN.",
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

        // Pregunta de seguridad
        Text(text = "Pregunta de seguridad: ¿Cuál es tu mayor sueño?", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.securityAnswer,
            onValueChange = viewModel::onSecurityAnswerChange,
            placeholder = { Text("Tu respuesta", color = Color.LightGray) },
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

        // Nuevo PIN
        Text(text = "Nuevo PIN (4 dígitos)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.newPin,
            onValueChange = viewModel::onNewPinChange,
            placeholder = { Text("4 dígitos", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (uiState.newPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = viewModel::toggleNewPinVisibility) {
                    Icon(
                        imageVector = if (uiState.newPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
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

        // Confirmar nuevo PIN
        Text(text = "Confirmar nuevo PIN", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.confirmNewPin,
            onValueChange = viewModel::onConfirmNewPinChange,
            placeholder = { Text("Repite el PIN", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (uiState.confirmNewPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = viewModel::toggleConfirmNewPinVisibility) {
                    Icon(
                        imageVector = if (uiState.confirmNewPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
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

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Guardar
        Button(
            onClick = {
                viewModel.onSaveNewPinClick(onSuccess = onRecoverSuccess)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text(text = "Guardar nuevo PIN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
