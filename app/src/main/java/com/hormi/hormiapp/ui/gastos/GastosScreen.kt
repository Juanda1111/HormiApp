package com.hormi.hormiapp.ui.gastos

import com.hormi.hormiapp.ui.theme.AntBackground
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.components.categoryIcon
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GastosScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddExpense: () -> Unit = {},
    onNavigateToDetalleGasto: (Int) -> Unit = {},
    onLogoClick: () -> Unit = {},
    viewModel: GastosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val money = rememberMoneyFormatter()

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            HormiAppHeader(
                title = "Mis gastos",
                onBackClick = null,
            onLogoClick = onLogoClick, // Es un tab principal, no necesita volver atrás
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Filtros de tiempo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(GastosFilter.SEMANA to "Esta semana", GastosFilter.MES to "Este mes").forEach { (filter, label) ->
                    val selected = uiState.filter == filter
                    if (selected) {
                        Button(
                            onClick = { viewModel.setFilter(filter) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(label, color = Color.White, fontSize = 14.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.setFilter(filter) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(label, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
            ) {
                // Tarjeta de Resumen
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Total
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Total", color = Color.Gray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = money(uiState.totalExpenses),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            
                            // Divider vertical
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // En hormigas
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = Color(0xFFC48615), // Naranja oscuro
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "En hormigas", color = Color.Gray, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = money(uiState.totalAntExpenses),
                                    color = Color(0xFFC48615), // Naranja oscuro para hormigas
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (uiState.groupedExpenses.isEmpty()) {
                    item {
                        Text(
                            text = "No hay gastos en este período.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                // Lista agrupada
                uiState.groupedExpenses.forEach { (dateLabel, transactions) ->
                    // Header de la fecha
                    item {
                        val dayTotal = transactions.sumOf { it.amount }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateLabel,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = money(dayTotal),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Transacciones de ese día
                    items(transactions) { transaction ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetalleGasto(transaction.id) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono según categoría
                            val icon = categoryIcon(transaction.category)
                            val bgColor = PrimaryGreen.copy(alpha = 0.15f)

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Textos
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = transaction.category,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = transaction.description,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            
                            // Monto y Tag
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "-${money(transaction.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                )
                                
                                if (transaction.isAntExpense) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AntBackground)
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BugReport,
                                            contentDescription = null,
                                            tint = Color(0xFF6B4E0D),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "hormiga",
                                            color = Color(0xFF6B4E0D),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onNavigateToAddExpense,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar gasto", modifier = Modifier.size(28.dp))
        }
    }
}
