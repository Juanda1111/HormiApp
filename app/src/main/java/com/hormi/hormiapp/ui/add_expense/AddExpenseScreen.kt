package com.hormi.hormiapp.ui.add_expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBackClick: () -> Unit,
    onExpenseSaved: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))

    // Efecto de guardado exitoso
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onExpenseSaved()
        }
    }

    val backgroundColor = Color(0xFFF9F6F0)

    val quickExpenses = listOf(
        QuickExpense("Tinto", 2500.0, "Café y snacks", true),
        QuickExpense("Snack", 4000.0, "Café y snacks", true),
        QuickExpense("Pasaje", 3200.0, "Transporte", false),
        QuickExpense("Almuerzo", 14000.0, "Comida", false)
    )

    val categories = listOf(
        "Comida", "Café y snacks", "Transporte", "Domicilios",
        "Suscripciones", "Salidas", "Universidad", "Otros"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        HormiAppHeader(
            title = "Registrar gasto",
            onBackClick = onBackClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // Gastos rápidos
            item {
                Text("Gastos rápidos", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickExpenses.forEach { qe ->
                        OutlinedButton(
                            onClick = { viewModel.applyQuickExpense(qe) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.DarkGray
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            val icon = when {
                                qe.category.contains("Café") -> Icons.Default.LocalCafe
                                qe.category.contains("Transporte") -> Icons.Default.DirectionsBus
                                else -> Icons.Default.Fastfood
                            }
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${qe.name} $${formatter.format(qe.amount)}", fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Monto
            item {
                Text("Monto", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    ),
                    leadingIcon = {
                        Text(
                            text = "$",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true
                )

                if (uiState.isImpulsive) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentYellow.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                tint = Color(0xFF6B4E0D),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "hormiga",
                                color = Color(0xFF6B4E0D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Se contará como gasto hormiga",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Categoría
            item {
                Text("Categoría", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = uiState.category == category
                        val bgColor = if (isSelected) PrimaryGreen else Color.White
                        val contentColor = if (isSelected) Color.White else Color.DarkGray
                        val borderColor = if (isSelected) PrimaryGreen else Color.LightGray.copy(alpha = 0.5f)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateCategory(category) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = category, color = contentColor, fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Fecha
            item {
                OutlinedTextField(
                    value = SimpleDateFormat("d MMM yyyy", Locale("es", "ES")).format(Date(uiState.dateTimestamp)),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryGreen)
                    },
                    prefix = {
                        Text("Fecha", color = Color.Gray, modifier = Modifier.padding(end = 16.dp))
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Switch Impulsivo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("¿Fue impulsivo?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Lo compraste sin haberlo planeado", fontSize = 14.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = uiState.isImpulsive,
                        onCheckedChange = { viewModel.toggleImpulsive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        thumbContent = if (uiState.isImpulsive) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = PrimaryGreen
                                )
                            }
                        } else null
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Nota
            item {
                Text("Nota (opcional)", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = { viewModel.updateNote(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Botón Guardar
            item {
                Button(
                    onClick = { viewModel.saveExpense() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar gasto", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
