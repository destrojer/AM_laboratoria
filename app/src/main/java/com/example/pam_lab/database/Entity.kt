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
    )