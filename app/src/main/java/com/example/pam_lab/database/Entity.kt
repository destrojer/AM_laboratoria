package com.example.pam_lab.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, //nazwa moze miec wiecej niz jedno slowo
    val description: String, //opis trasy
    val type: String, //rowerowa czy piesza
    val difficulty: Int, //trudnosc w skali od 1 do 5 okreslone potem jako gwiazdki
    val length: Double, // dlugosc trasy w kilometrach lub metrach
    val duration: Int, // typowy lub przewidywany czas przejscia trasy
)

@Entity(tableName = "route_timer")
data class RouteTimer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val timeInSeconds: Int
)
