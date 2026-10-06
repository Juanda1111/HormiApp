package com.hormi.hormiapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hormi.hormiapp.ui.analisis.AnalisisScreen
import com.hormi.hormiapp.ui.auth.LoginScreen
import com.hormi.hormiapp.ui.auth.RecoverPinScreen
import com.hormi.hormiapp.ui.auth.RegistroScreen
import com.hormi.hormiapp.ui.components.HormiAppBottomBar
import com.hormi.hormiapp.ui.gastos.GastosScreen
import com.hormi.hormiapp.ui.inicio.InicioScreen
import com.hormi.hormiapp.ui.onboarding.OnboardingScreen
import com.hormi.hormiapp.ui.perfil.PerfilScreen
import com.hormi.hormiapp.ui.splash.SplashScreen

@Composable
fun HormiAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { HormiAppBottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegistro = {
                        navController.navigate(Screen.Registro.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onForgotPasswordClick = {
                        navController.navigate(Screen.RecoverPin.route)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Registro.route)
                    }
                )
            }
            composable(route = Screen.Registro.route) {
                RegistroScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Registro.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = Screen.RecoverPin.route) {
                RecoverPinScreen(
                    onRecoverSuccess = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            
            // --- Bottom Navigation Screens ---
            composable(route = Screen.Inicio.route) {
                InicioScreen(
                    onNavigateToAddExpense = {
                        navController.navigate(Screen.RegistrarGasto.route)
                    },
                    onNavigateToIngresos = {
                        navController.navigate(Screen.Ingresos.route)
                    },
                    onNavigateToGastos = {
                        navController.navigate(Screen.Gastos.route)
                    },
                    onNavigateToDetalleGasto = { gastoId ->
                        navController.navigate(Screen.DetalleGasto.createRoute(gastoId))
                    }
                )
            }
            composable(route = Screen.Gastos.route) {
                GastosScreen(
                    onNavigateToAddExpense = {
                        navController.navigate(Screen.RegistrarGasto.route)
                    },
                    onNavigateToDetalleGasto = { gastoId ->
                        navController.navigate(Screen.DetalleGasto.createRoute(gastoId))
                    }
                )
            }
            composable(route = Screen.Analisis.route) {
                AnalisisScreen(
                    onNavigateToAddExpense = {
                        navController.navigate(Screen.RegistrarGasto.route)
                    }
                )
            }
            composable(route = Screen.Perfil.route) {
                PerfilScreen(
                    onLogoutClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToAddExpense = {
                        navController.navigate(Screen.RegistrarGasto.route)
                    },
                    onNavigateToIngresos = {
                        navController.navigate(Screen.Ingresos.route)
                    }
                )
            }
            
            // --- Additional Flows ---
            composable(route = Screen.Ingresos.route) {
                com.hormi.hormiapp.ui.ingresos.IngresosScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddExtraIncomeClick = {
                        // TODO: Abrir modal o pantalla de ingreso extra
                    }
                )
            }
            
            composable(route = Screen.RegistrarGasto.route) {
                com.hormi.hormiapp.ui.add_expense.AddExpenseScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onExpenseSaved = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.DetalleGasto.route,
                arguments = listOf(androidx.navigation.navArgument("gastoId") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                com.hormi.hormiapp.ui.detalle_gasto.DetalleGastoScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = { id ->
                        // TODO: Navigate to Edit Expense Screen
                    }
                )
            }
        }
    }
}
