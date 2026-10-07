package com.hormi.hormiapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "goals", indices = [Index("userId")])
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val iconName: String = "Laptop",
    val userId: String = "" // Cuenta a la que pertenece (se asigna al guardar)
)
