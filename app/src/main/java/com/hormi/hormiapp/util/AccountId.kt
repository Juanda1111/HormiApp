package com.hormi.hormiapp.util

/** Identificador de cuenta derivado del nombre: sin distinguir mayúsculas ni espacios de más. */
object AccountId {
    const val DEMO_NAME = "Demo"

    fun normalize(name: String): String = name.trim().replace(Regex("\\s+"), " ").lowercase()

    /** El nombre "Demo" está reservado para la cuenta de demostración. */
    fun isReserved(name: String): Boolean = normalize(name) == normalize(DEMO_NAME)
}
