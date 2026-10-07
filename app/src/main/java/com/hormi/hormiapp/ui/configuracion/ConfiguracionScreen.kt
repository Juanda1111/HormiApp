package com.hormi.hormiapp.ui.configuracion

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
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))

    val backgroundColor = Color(0xFFF9F6F0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HormiAppHeader(
            title = "Configuración",
            onBackClick = onBackClick,
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
                        onCheckedChange = { viewModel.toggleReminder(it) },
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
                        subtitle = "$ ${formatter.format(uiState.weeklyBudget)} (calculado de tu ingreso)"
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                    SettingsNavigationRow(
                        title = "Umbral de gasto hormiga",
                        subtitle = "Gastos de $ ${formatter.format(uiState.antExpenseThreshold)} o menos"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Borrar Datos
            OutlinedButton(
                onClick = { /* TODO */ },
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color.Black,
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
        color = if (isSelected) PrimaryGreen else Color.White,
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
                    tint = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SettingsNavigationRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.Black, fontSize = 15.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
