package com.hormi.hormiapp.ui.metas_ahorro

import androidx.compose.runtime.*
import com.hormi.hormiapp.util.LocalCurrency
import com.hormi.hormiapp.util.currencySymbol
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.util.rememberMoneyFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import com.hormi.hormiapp.data.local.entity.GoalEntity
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MetasAhorroScreen(
    onBackClick: () -> Unit,
    onNavigateToNuevaMeta: () -> Unit = {},
    onNavigateToAbonar: (Int) -> Unit = {},
    onLogoClick: () -> Unit = {},
    viewModel: MetasAhorroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundColor = MaterialTheme.colorScheme.background

    var showNewGoalDialog by remember { mutableStateOf(false) }
    var goalToContribute by remember { mutableStateOf<GoalEntity?>(null) }
    var goalToDelete by remember { mutableStateOf<GoalEntity?>(null) }

    if (showNewGoalDialog) {
        NewGoalDialog(
            onDismiss = { showNewGoalDialog = false },
            onCreate = { name, target, icon ->
                viewModel.createGoal(name, target, icon)
                showNewGoalDialog = false
            }
        )
    }

    goalToContribute?.let { goal ->
        AbonarDialog(
            goal = goal,
            onDismiss = { goalToContribute = null },
            onConfirm = { amount ->
                viewModel.addToGoal(goal, amount)
                goalToContribute = null
            }
        )
    }

    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Eliminar meta") },
            text = { Text("¿Quieres eliminar la meta \"${goal.name}\"? Se perderá el progreso registrado.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGoal(goal)
                    goalToDelete = null
                }) { Text("Eliminar", color = Color(0xFFD32F2F)) }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) { Text("Cancelar", color = Color.Gray) }
            }
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            HormiAppHeader(
                title = "Metas de ahorro",
                onBackClick = onBackClick,
            onLogoClick = onLogoClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showNewGoalDialog = true
                    onNavigateToNuevaMeta()
                },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva meta")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva meta", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = PrimaryGreen,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.goals.isEmpty()) {
                Text(
                    text = "No tienes metas de ahorro registradas",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(uiState.goals) { goal ->
                        GoalItem(
                            goal = goal,
                            onDelete = { goalToDelete = goal },
                            onAbonar = {
                                goalToContribute = goal
                                onNavigateToAbonar(goal.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GoalItem(
    goal: GoalEntity,
    onDelete: () -> Unit,
    onAbonar: () -> Unit
) {
    val money = rememberMoneyFormatter()
    val percentage = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0) else 0.0
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
    val isCompleted = goal.currentAmount >= goal.targetAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icono, Titulo, Basura
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                val iconVector = goalIcon(goal.iconName)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentYellow.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = iconVector, contentDescription = null, tint = Color(0xFF6B4E0D))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Textos
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${money(goal.currentAmount)} de ${money(goal.targetAmount)}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                // Basura
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { percentage.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PrimaryGreen,
                trackColor = Color(0xFFEBE6D8),
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Footer: Info y Botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Meta cumplida!", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    val percentInt = (percentage * 100).toInt()
                    Text(
                        text = "$percentInt% • faltan ${money(remaining)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    OutlinedButton(
                        onClick = onAbonar,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, PrimaryGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Abonar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


private val goalIconOptions = listOf(
    "Laptop" to Icons.Default.Computer,
    "Airplane" to Icons.Default.Flight,
    "Headphones" to Icons.Default.Headphones,
    "Home" to Icons.Default.Home,
    "Savings" to Icons.Default.Savings,
    "Star" to Icons.Default.Star
)

private fun goalIcon(name: String) = goalIconOptions.firstOrNull { it.first == name }?.second ?: Icons.Default.Star

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NewGoalDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, target: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf(goalIconOptions.first().first) }
    val valid = name.isNotBlank() && (target.toLongOrNull() ?: 0L) > 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva meta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(40) },
                    label = { Text("Nombre de la meta") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it.filter { c -> c.isDigit() }.take(12) },
                    label = { Text("Monto objetivo") },
                    prefix = { Text("${currencySymbol(LocalCurrency.current)} ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Text("Ícono", color = Color.Gray, fontSize = 14.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    goalIconOptions.forEach { (key, vector) ->
                        val selected = icon == key
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (selected) PrimaryGreen else AccentYellow.copy(alpha = 0.4f))
                                .clickable { icon = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(vector, contentDescription = key, tint = if (selected) Color.White else Color(0xFF6B4E0D))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onCreate(name, target, icon) }) {
                Text("Crear", color = if (valid) PrimaryGreen else Color.Gray)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AbonarDialog(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    val money = rememberMoneyFormatter()
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
    var amount by remember { mutableStateOf(0.0) }

    // El selector avanza de a 1.000 cuando falta bastante; si falta poco, de a 1
    val step = if (remaining >= 20000) 1000.0 else 1.0
    val quickAmounts = listOf(10000.0, 50000.0, 100000.0).filter { it < remaining }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Abonar a ${goal.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Faltan ${money(remaining)}", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = money(amount),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Slider(
                    value = amount.toFloat(),
                    onValueChange = { amount = (Math.round(it / step) * step).coerceIn(0.0, remaining) },
                    valueRange = 0f..remaining.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(thumbColor = PrimaryGreen, activeTrackColor = PrimaryGreen)
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickAmounts.forEach { quick ->
                        OutlinedButton(
                            onClick = { amount = (amount + quick).coerceAtMost(remaining) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) { Text("+${money(quick)}", color = PrimaryGreen, fontSize = 13.sp) }
                    }
                    OutlinedButton(
                        onClick = { amount = remaining },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) { Text("Completar", color = PrimaryGreen, fontSize = 13.sp) }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = amount > 0, onClick = { onConfirm(amount) }) {
                Text("Abonar", color = if (amount > 0) PrimaryGreen else Color.Gray)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}
