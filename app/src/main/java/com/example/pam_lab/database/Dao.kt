package com.example.pam_lab.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM route")
    fun getAllFlow(): Flow<List<Route>>

    @Query("SELECT COUNT(*) FROM route")
    suspend fun getCount(): Int

    @Insert
    suspend fun insertRoute(route: Route)

    @Insert
    suspend fun insertRoutes(routes: List<Route>)

    @Update
    suspend fun updateRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route: Route)
}

@Dao
interface RouteTimerDao {
    @Query("SELECT * FROM route_timer")
    fun getAllFlow(): Flow<List<RouteTimer>>

    @Insert
    suspend fun insertTimer(routeTimer: RouteTimer)

    @Delete
    suspend fun deleteTimer(routeTimer: RouteTimer)
}
