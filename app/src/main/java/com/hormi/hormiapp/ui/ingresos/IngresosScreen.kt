package com.hormi.hormiapp.ui.ingresos

import com.hormi.hormiapp.util.ThousandsVisualTransformation
import com.hormi.hormiapp.util.LocalCurrency
import com.hormi.hormiapp.util.currencySymbol
import com.hormi.hormiapp.util.rememberMoneyFormatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Wallet
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
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    onBackClick: () -> Unit,
    onAddExtraIncomeClick: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    viewModel: IngresosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val money = rememberMoneyFormatter()
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))

    var showEditDialog by remember { mutableStateOf(false) }
    var editIncomeValue by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var addAmount by remember { mutableStateOf("") }
    var addDescription by remember { mutableStateOf("") }
    var addDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = addDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { addDate = it }
                    showDatePicker = false
                }) { Text("Aceptar", color = PrimaryGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddDialog) {
        val amountValid = (addAmount.toLongOrNull() ?: 0L) > 0L
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nuevo ingreso extra") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = addAmount,
                        onValueChange = { addAmount = it.filter { c -> c.isDigit() }.take(12) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandsVisualTransformation(),
                        label = { Text("Monto") },
                        prefix = { Text("${currencySymbol(LocalCurrency.current)} ") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addDescription,
                        onValueChange = { addDescription = it.take(60) },
                        label = { Text("Descripción (opcional)") },
                        singleLine = true
                    )
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text("Fecha: ${dateFormatter.format(Date(addDate))}", color = PrimaryGreen)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = amountValid,
                    onClick = {
                        viewModel.addExtraIncome(addAmount, addDescription, addDate)
                        showAddDialog = false
                    }
                ) { Text("Guardar", color = if (amountValid) PrimaryGreen else Color.Gray) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Ingreso Mensual") },
            text = {
                OutlinedTextField(
                    value = editIncomeValue,
                    onValueChange = { editIncomeValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandsVisualTransformation(),
                    label = { Text("Nuevo monto") },
                    prefix = { Text("${currencySymbol(LocalCurrency.current)} ") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateMonthlyIncome(editIncomeValue)
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar", color = PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            HormiAppHeader(
                title = "Ingresos",
                onBackClick = onBackClick,
            onLogoClick = onLogoClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
            ) {
                // Tarjeta 1: Total
                item {
                    val grandTotal = uiState.baseIncome + uiState.totalExtraIncome
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "Total de ${uiState.currentMonthName}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = money(grandTotal),
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Tarjeta 2: Ingreso Mensual
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color(0xFF007AFF), RoundedCornerShape(16.dp)), // Borde azul brillante
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícono Wallet
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Wallet,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ingreso mensual",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = money(uiState.baseIncome),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                // Lapicito de edición
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = PrimaryGreen,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            editIncomeValue = uiState.baseIncome.toLong().toString()
                                            showEditDialog = true
                                        }
                                        .padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Mesada, salario o beca que recibes cada mes.",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Título lista extra
                item {
                    Text(
                        text = "Ingresos extra este mes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Lista de ingresos extra
                if (uiState.extraIncomesList.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no tienes ingresos extra este mes.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(uiState.extraIncomesList) { income ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer, // El ícono de etiqueta del figma
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = income.description.ifEmpty { income.category },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dateFormatter.format(Date(income.dateTimestamp)),
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Text(
                                text = "+${money(income.amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PrimaryGreen
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                    }
                }
            }
        }

        // FAB Extendido
        ExtendedFloatingActionButton(
            onClick = {
                addAmount = ""
                addDescription = ""
                addDate = System.currentTimeMillis()
                showAddDialog = true
                onAddExtraIncomeClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
            text = { Text("Ingreso extra", fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
