package com.hormi.hormiapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hormi.hormiapp.ui.splash.SplashScreen

@Composable
fun HormiAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Login.route) {
            // Login Screen goes here
        }
        composable(route = Screen.Registro.route) {
            // Registro Screen goes here
        }
        composable(route = Screen.Onboarding.route) {
            // Onboarding Screen goes here
        }
        
        // Aquí agregaremos el resto de rutas (Inicio, Gastos, etc.) más adelante
    }
}
