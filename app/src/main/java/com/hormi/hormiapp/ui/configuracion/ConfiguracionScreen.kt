package com.hormi.hormiapp.ui.configuracion

import com.hormi.hormiapp.util.ThousandsVisualTransformation
import androidx.compose.runtime.*
import com.hormi.hormiapp.util.LocalCurrency
import com.hormi.hormiapp.util.currencySymbol
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Warning
import com.hormi.hormiapp.util.rememberMoneyFormatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfiguracionScreen(
    onBackClick: () -> Unit,
    onLogoClick: () -> Unit = {},
    onDataDeleted: () -> Unit = {},
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val money = rememberMoneyFormatter()

    var showIncomeDialog by remember { mutableStateOf(false) }
    var showThresholdDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Permiso de notificaciones (Android 13+) al activar el recordatorio
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* El recordatorio se programa igual; sin permiso simplemente no se muestra */ }

    if (showIncomeDialog) {
        NumberInputDialog(
            title = "Presupuesto semanal",
            label = "Ingreso mensual",
            initialValue = uiState.monthlyIncome.toLong().toString(),
            helper = { value ->
                val weekly = (value.toLongOrNull() ?: 0L) * 0.25
                "Tu presupuesto semanal será ${money(weekly)} (25% del ingreso mensual)."
            },
            onDismiss = { showIncomeDialog = false },
            onConfirm = {
                viewModel.updateMonthlyIncome(it)
                showIncomeDialog = false
            }
        )
    }

    if (showThresholdDialog) {
        NumberInputDialog(
            title = "Umbral de gasto hormiga",
            label = "Monto máximo",
            initialValue = uiState.antExpenseThreshold.toLong().toString(),
            helper = { value -> "Los gastos de ${money((value.toLongOrNull() ?: 0L).toDouble())} o menos se marcan como hormiga." },
            onDismiss = { showThresholdDialog = false },
            onConfirm = {
                viewModel.updateAntThreshold(it)
                showThresholdDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        DeleteAllDataDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteAllData(onDone = onDataDeleted)
            }
        )
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HormiAppHeader(
            title = "Configuración",
            onBackClick = onBackClick,
            onLogoClick = onLogoClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card: Moneda
            SettingsCard(
                icon = Icons.Default.CurrencyExchange,
                title = "Moneda"
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currencies = listOf("COP $", "USD US$", "EUR €", "MXN MX$")
                    currencies.forEach { currency ->
                        SelectableChip(
                            text = currency,
                            isSelected = uiState.selectedCurrency == currency,
                            onClick = { viewModel.updateCurrency(currency) }
                        )
                    }
                }
            }

            // Card: Recordatorio
            SettingsCard(
                icon = Icons.Default.Notifications,
                title = "Recordatorio diario",
                trailing = {
                    Switch(
                        checked = uiState.isReminderEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.toggleReminder(enabled)
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryGreen
                        )
                    )
                }
            ) {
                if (uiState.isReminderEnabled) {
                    Text(
                        text = "Hora del recordatorio",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val times = listOf("08:00", "12:00", "18:00", "20:00", "22:00")
                        times.forEach { time ->
                            SelectableChip(
                                text = time,
                                isSelected = uiState.selectedReminderTime == time,
                                onClick = { viewModel.updateReminderTime(time) }
                            )
                        }
                    }
                }
            }

            // Card: Tema
            SettingsCard(
                icon = Icons.Default.Palette,
                title = "Tema"
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themes = listOf(
                        Triple("Sistema", Icons.Default.SettingsBrightness, "Sistema"),
                        Triple("Claro", Icons.Default.LightMode, "Claro"),
                        Triple("Oscuro", Icons.Default.DarkMode, "Oscuro")
                    )
                    themes.forEach { (name, icon, id) ->
                        SelectableChip(
                            text = name,
                            icon = icon,
                            isSelected = uiState.selectedTheme == id,
                            onClick = { viewModel.updateTheme(id) }
                        )
                    }
                }
            }

            // Card: Presupuesto y hormiga
            SettingsCard(
                icon = Icons.Default.AccountBalanceWallet,
                title = "Presupuesto y gastos hormiga"
            ) {
                Column {
                    SettingsNavigationRow(
                        title = "Presupuesto semanal",
                        subtitle = "${money(uiState.weeklyBudget)} (calculado de tu ingreso mensual)",
                        onClick = { showIncomeDialog = true }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsNavigationRow(
                        title = "Umbral de gasto hormiga",
                        subtitle = "Gastos de ${money(uiState.antExpenseThreshold)} o menos",
                        onClick = { showThresholdDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Borrar Datos
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Borrar todos mis datos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (trailing != null) {
                    trailing()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SettingsNavigationRow(title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}


@Composable
private fun NumberInputDialog(
    title: String,
    label: String,
    initialValue: String,
    helper: (String) -> String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }
    val valid = (value.toLongOrNull() ?: 0L) > 0L
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it.filter { c -> c.isDigit() }.take(12) },
                    label = { Text(label) },
                    prefix = { Text("${currencySymbol(LocalCurrency.current)} ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = ThousandsVisualTransformation(),
                    singleLine = true
                )
                Text(helper(value), color = Color.Gray, fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onConfirm(value) }) {
                Text("Guardar", color = if (valid) PrimaryGreen else Color.Gray)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}

/** Confirmación severa: hay que marcar la casilla y escribir BORRAR para habilitar el botón. */
@Composable
private fun DeleteAllDataDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val red = Color(0xFFD32F2F)
    var understood by remember { mutableStateOf(false) }
    var typed by remember { mutableStateOf("") }
    val enabled = understood && typed.trim().equals("BORRAR", ignoreCase = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = red) },
        title = { Text("¿Borrar todos tus datos?", color = red, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Se eliminarán de forma permanente tus gastos, ingresos, metas de ahorro, " +
                        "tu cuenta (nombre y PIN) y todos los ajustes. No se puede deshacer."
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = understood,
                        onCheckedChange = { understood = it },
                        colors = CheckboxDefaults.colors(checkedColor = red)
                    )
                    Text("Entiendo que no podré recuperarlos", fontSize = 14.sp)
                }
                OutlinedTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    label = { Text("Escribe BORRAR para confirmar") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(enabled = enabled, onClick = onConfirm) {
                Text("Borrar todo", color = if (enabled) red else Color.Gray, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}
