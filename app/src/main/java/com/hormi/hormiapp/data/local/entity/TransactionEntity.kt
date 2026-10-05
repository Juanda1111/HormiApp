package com.hormi.hormiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val type: String, // "INCOME" o "EXPENSE"
    val category: String, // Ejemplo: "Comida", "Transporte", "Suscripciones"
    val description: String,
    val dateTimestamp: Long, // Fecha en la que se realizó
    val isAntExpense: Boolean // Identificador de si es "Gasto Hormiga" (Ej: menor a 15k)
)
