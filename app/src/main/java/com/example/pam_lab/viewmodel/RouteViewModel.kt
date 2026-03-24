package com.example.pam_lab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_lab.api_access.OsmElement
import com.example.pam_lab.api_access.RouteApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RouteViewModel : ViewModel() {

//    private val _routeOne = listOf("Sciezka Rowerowa 1", "Sciezka Rowerowa 2", "Sciezka Rowerowa 3")
//    private val _routeTwo = listOf("Sciezka Piesza 1", "Sciezka Piesza 2", "Sciezka Piesza 3")

    private var _routeBike = emptyList<OsmElement>()
    private var _routeFoot = emptyList<OsmElement>()

//    State flow values
    private val _bool = MutableStateFlow(false)
    val bool: StateFlow<Boolean> = _bool.asStateFlow()

//    private val _route = MutableStateFlow(_routeTwo)
//    val route: StateFlow<List<String>> = _route.asStateFlow()
    private val _routes = MutableStateFlow<List<OsmElement>>(emptyList())
    val routes: StateFlow<List<OsmElement>> = _routes.asStateFlow()



//    False is for foot, true is for bike
    fun setRoute(newBool: Boolean){
        _bool.value = newBool

//        _route.value = if (newBool) _routeOne else _routeTwo
        when{
            (_bool.value && _routeBike.isEmpty()) -> fetchRoutes(newBool)
            (_bool.value && _routeBike.isNotEmpty()) -> _routes.value = _routeBike
            (!_bool.value && _routeFoot.isEmpty()) -> fetchRoutes(newBool)
            (!_bool.value && _routeFoot.isNotEmpty()) -> _routes.value = _routeFoot
        }
    }


//    Api code
    private val retrofit = Retrofit.Builder()
    .baseUrl("https://overpass-api.de/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

    private val apiService = retrofit.create(RouteApiService::class.java)

    fun fetchRoutes(isBike: Boolean) {
        val type = if (isBike) "bicycle" else "hiking"

        val query = """
            [out:json];
            relation["route"="$type"]["name"](49.20,19.80,49.30,20.10);
            out tags;
        """.trimIndent()

        viewModelScope.launch {
            try {
                val response = apiService.getTouristRoutes(query)
                _routes.value = response.elements
                if (isBike) {
                    _routeBike = _routes.value
                } else {
                    _routeFoot = _routes.value
                }
            } catch (e: Exception) {
                _routes.value = emptyList()
            }
        }
    }
}