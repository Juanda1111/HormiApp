package com.hormi.hormiapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

/** Ícono asociado a una categoría de gasto. */
fun categoryIcon(category: String): ImageVector = when {
    category.contains("Café", ignoreCase = true) || category.contains("snack", ignoreCase = true) ||
        category.contains("Hormiga", ignoreCase = true) -> Icons.Default.LocalCafe
    category.contains("Comida", ignoreCase = true) || category.contains("Alimentación", ignoreCase = true) ->
        Icons.Default.Restaurant
    category.contains("Transporte", ignoreCase = true) -> Icons.Default.DirectionsBus
    category.contains("Domicilio", ignoreCase = true) -> Icons.Default.DeliveryDining
    category.contains("Suscripci", ignoreCase = true) -> Icons.Default.Subscriptions
    category.contains("Salida", ignoreCase = true) || category.contains("Entretenimiento", ignoreCase = true) ->
        Icons.Default.Celebration
    category.contains("Universidad", ignoreCase = true) -> Icons.Default.School
    category.contains("Otros", ignoreCase = true) -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}
