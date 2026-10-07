package com.hormi.hormiapp.ui.metas_ahorro

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
    viewModel: MetasAhorroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundColor = Color(0xFFF9F6F0)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            HormiAppHeader(
                title = "Metas de ahorro",
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNuevaMeta,
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
                            onDelete = { viewModel.deleteGoal(goal) },
                            onAbonar = { onNavigateToAbonar(goal.id) }
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
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
    val percentage = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0) else 0.0
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
    val isCompleted = goal.currentAmount >= goal.targetAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                val iconVector = when (goal.iconName) {
                    "Laptop" -> Icons.Default.Computer
                    "Airplane" -> Icons.Default.Flight
                    "Headphones" -> Icons.Default.Headphones
                    else -> Icons.Default.Star
                }
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
                        color = Color.Black
                    )
                    Text(
                        text = "$ ${formatter.format(goal.currentAmount)} de $ ${formatter.format(goal.targetAmount)}",
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
                        text = "$percentInt% • faltan $ ${formatter.format(remaining)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
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
