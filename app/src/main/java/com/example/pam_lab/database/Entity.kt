package com.example.pam_lab.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val type: String,
    val difficulty: Int,
    val length: Double,
    val duration: Int,
    val imageUri: String? = null // Nowe pole na ścieżkę do zdjęcia (URI zasobu lub pliku)
)

@Entity(tableName = "route_timer")
data class RouteTimer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val timeInSeconds: Int,
    val date: Long = 0L
)
