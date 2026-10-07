package com.hormi.hormiapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registro : Screen("registro")
    object RecoverPin : Screen("recover_pin")
    object Onboarding : Screen("onboarding")
    
    // Bottom Navigation Screens
    object Inicio : Screen("inicio")
    object Gastos : Screen("gastos")
    object Analisis : Screen("analisis")
    object Perfil : Screen("perfil")
    
    // Additional flows
    object RegistrarGasto : Screen("registrar_gasto")
    object DetalleGasto : Screen("detalle_gasto/{gastoId}") {
        fun createRoute(gastoId: Int) = "detalle_gasto/$gastoId"
    }
    object EditarGasto : Screen("editar_gasto/{gastoId}") {
        fun createRoute(gastoId: Int) = "editar_gasto/$gastoId"
    }
    object Ingresos : Screen("ingresos")
    object Metas : Screen("metas")
    object Configuracion : Screen("configuracion")
    object Creditos : Screen("creditos")
}
