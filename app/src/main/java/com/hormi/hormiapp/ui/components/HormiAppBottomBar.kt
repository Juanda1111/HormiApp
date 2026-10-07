package com.hormi.hormiapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hormi.hormiapp.navigation.Screen
import com.hormi.hormiapp.ui.theme.PrimaryGreen

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Inicio : BottomNavItem("Inicio", Icons.Default.Home, Screen.Inicio.route)
    object Gastos : BottomNavItem("Gastos", Icons.Default.List, Screen.Gastos.route)
    object Analisis : BottomNavItem("Análisis", Icons.Default.Analytics, Screen.Analisis.route)
    object Perfil : BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil.route)
}

@Composable
fun HormiAppBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Gastos,
        BottomNavItem.Analisis,
        BottomNavItem.Perfil
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Solo mostrar el BottomBar en estas rutas
    val showBottomBar = items.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(Screen.Inicio.route) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}
