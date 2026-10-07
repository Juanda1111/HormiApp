package com.hormi.hormiapp.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import java.text.NumberFormat
import java.util.Locale

/** Moneda seleccionada en Configuración, por ejemplo "COP $" o "EUR €". Solo cambia el símbolo mostrado. */
val LocalCurrency = compositionLocalOf { "COP $" }

private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "CO")).apply {
    maximumFractionDigits = 0
}

fun currencySymbol(currency: String): String = currency.substringAfter(' ', "$")

fun formatMoney(amount: Double, currency: String): String =
    "${currencySymbol(currency)}\u00A0${numberFormat.format(amount)}"

/** Devuelve una función que formatea montos con la moneda elegida por el usuario. */
@Composable
fun rememberMoneyFormatter(): (Double) -> String {
    val currency = LocalCurrency.current
    return { amount -> formatMoney(amount, currency) }
}
