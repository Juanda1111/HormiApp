package com.hormi.hormiapp.ui.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hormi.hormiapp.R
import com.hormi.hormiapp.ui.components.HormiAppHeader
import com.hormi.hormiapp.ui.theme.AccentYellow
import com.hormi.hormiapp.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddExpense: () -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    formatter.maximumFractionDigits = 0

    val backgroundColor = Color(0xFFF9F6F0) // Beige muy claro como en el diseño

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            HormiAppHeader(
                title = "Hola, ${uiState.userName}",
                onBackClick = null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp) // Más espacio para FAB
            ) {
                // Tarjeta de Saldo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Saldo actual",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatter.format(uiState.availableBalance),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Ingresos
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Ingresos",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(text = "Ingresos", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                        Text(text = formatter.format(uiState.totalIncome), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                // Gastos
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Gastos",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(text = "Gastos", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                        Text(text = formatter.format(uiState.totalExpenses), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Tarjeta Presupuesto Semanal
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Presupuesto semanal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "${(uiState.weeklyPercent * 100).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Barra de progreso
                            LinearProgressIndicator(
                                progress = { uiState.weeklyPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrimaryGreen,
                                trackColor = AccentYellow.copy(alpha = 0.3f)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${formatter.format(uiState.weeklySpent)} de ${formatter.format(uiState.weeklyBudget)}",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Te quedan ${formatter.format(uiState.weeklyRemaining)}",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Tarjeta Mayor Gasto Hormiga
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AccentYellow.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Coffee,
                                    contentDescription = "Hormiga",
                                    tint = Color(0xFF6B4E0D)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Mayor gasto hormiga", color = Color(0xFF5D4037), fontSize = 10.sp)
                                Text(text = uiState.highestAntExpenseCategory, color = Color(0xFF5D4037), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "${uiState.highestAntExpenseCount} gastos", color = Color(0xFF5D4037), fontSize = 10.sp)
                                Text(text = formatter.format(uiState.highestAntExpenseTotal), color = Color(0xFF5D4037), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Botones rápidos
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Ingresos
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ingresos", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        
                        // Botón Metas
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Metas de ahorro", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Título Últimos gastos
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Últimos gastos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Ver todos",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Lista
                items(uiState.recentTransactions.filter { it.type == "EXPENSE" }) { transaction ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (transaction.category.contains("Café", ignoreCase = true) || transaction.category.contains("Hormiga")) Icons.Default.LocalCafe else Icons.Default.DirectionsBus,
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
                                color = Color.Black,
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
                                text = "-$ ${formatter.format(transaction.amount).replace("$", "").trim()}",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            
                            if (transaction.isAntExpense) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentYellow.copy(alpha = 0.4f))
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
                    Spacer(modifier = Modifier.height(8.dp))
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
