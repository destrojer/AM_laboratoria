package com.example.pam_lab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_lab.database.AppDatabase
import com.example.pam_lab.database.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val routeDao = AppDatabase.getInstance(application).routeDao()
    private val packageName = application.packageName

    private val _isBike = MutableStateFlow<Boolean?>(null)
    val bool: StateFlow<Boolean?> = _isBike.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtry
    private val _filterDifficulty = MutableStateFlow<Int?>(null)
    val filterDifficulty = _filterDifficulty.asStateFlow()

    private val _filterMaxTime = MutableStateFlow<Int?>(null)
    val filterMaxTime = _filterMaxTime.asStateFlow()

    private val _filterMaxDistance = MutableStateFlow<Double?>(null)
    val filterMaxDistance = _filterMaxDistance.asStateFlow()

    // Widoczność panelu filtrów
    private val _isFilterVisible = MutableStateFlow(false)
    val isFilterVisible = _isFilterVisible.asStateFlow()

    private val allRoutes = routeDao.getAllFlow()

    val routes: StateFlow<List<Route>> = combine(
        allRoutes, 
        _isBike, 
        _searchQuery, 
        _filterDifficulty, 
        _filterMaxTime, 
        _filterMaxDistance
    ) { args: Array<Any?> ->
        val routesList = args[0] as List<Route>
        val isBike = args[1] as? Boolean
        val query = args[2] as String
        val difficulty = args[3] as? Int
        val maxTime = args[4] as? Int
        val maxDistance = args[5] as? Double

        if (isBike == null) emptyList()
        else {
            val type = if (isBike) "rowerowa" else "piesza"
            routesList.filter { route ->
                val matchesType = route.type == type
                val matchesQuery = query.isEmpty() || route.name.contains(query, ignoreCase = true)
                val matchesDifficulty = difficulty == null || route.difficulty == difficulty
                val matchesTime = maxTime == null || route.duration <= maxTime
                val matchesDistance = maxDistance == null || route.length <= maxDistance
                
                matchesType && matchesQuery && matchesDifficulty && matchesTime && matchesDistance
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedRoute = MutableStateFlow<Route?>(null)
    val selectedRoute: StateFlow<Route?> = _selectedRoute.asStateFlow()

    init {
        seedDatabase()
    }

    private fun getResUri(name: String): String {
        return "android.resource://$packageName/drawable/$name"
    }

    private fun seedDatabase() {
        viewModelScope.launch {
            if (routeDao.getCount() == 0) {
                val initialRoutes = listOf(
                    // 20 Tras Pieszych
                    Route(name = "Szlak na Morskie Oko", description = "Asfaltowa trasa z Palenicy Białczańskiej, dostępna dla każdego. Prowadzi do najsłynniejszego jeziora w Tatrach.", type = "piesza", difficulty = 1, length = 9.0, duration = 150, imageUri = getResUri("szlak_morskie_oko")),
                    Route(name = "Szlak na Giewont", description = "Klasyk tatrzański z Kuźnic przez Halę Kondratową. Wymaga kondycji i braku lęku wysokości przy kopule.", type = "piesza", difficulty = 4, length = 6.0, duration = 210, imageUri = getResUri("giewont_szlak")),
                    Route(name = "Szlak na Rysy", description = "Najwyższy szczyt Polski. Trudna, wysokogórska wyprawa znad Morskiego Oka. Tylko dla doświadczonych.", type = "piesza", difficulty = 5, length = 5.0, duration = 240, imageUri = getResUri("rysy")),
                    Route(name = "Szlak do Doliny Pięciu Stawów", description = "Piękna trasa doliną Roztoki. Nagrodą są niesamowite widoki na kaskadę Siklawa i jeziora.", type = "piesza", difficulty = 3, length = 8.0, duration = 150, imageUri = getResUri("dolina_pieciu_stawow")),
                    Route(name = "Szlak na Kasprowy Wierch", description = "Podejście z Kuźnic przez Myślenickie Turnie. Alternatywa dla kolejki linowej z pięknymi panoramami.", type = "piesza", difficulty = 3, length = 7.0, duration = 180, imageUri = getResUri("kasprowy_wierch")),
                    Route(name = "Szlak na Trzy Korony", description = "Pieniński klasyk ze Sromowiec Niżnych. Widok z platformy na przełom Dunajca zapiera dech.", type = "piesza", difficulty = 2, length = 4.0, duration = 120, imageUri = getResUri("trzy_korony")),
                    Route(name = "Szlak na Sokolicę", description = "Krótki, ale stromy szlak z Krościenka nad Dunajcem do słynnej reliktowej sosny.", type = "piesza", difficulty = 2, length = 3.0, duration = 90, imageUri = getResUri("szlak_sokolica")),
                    Route(name = "Szlak na Tarnicę", description = "Najwyższy szczyt Bieszczadów z Wołosatego. Krótka, ale intensywna wędrówka na dach Bieszczad.", type = "piesza", difficulty = 3, length = 4.5, duration = 120, imageUri = getResUri("tarnica_szlak")),
                    Route(name = "Szlak na Połoninę Wetlińską", description = "Spacer z Przełęczy Wyżnej do schroniska Chatka Puchatka. Kultowe miejsce w Bieszczadach.", type = "piesza", difficulty = 2, length = 3.0, duration = 60, imageUri = getResUri("szlak_polonina_wetlinska")),
                    Route(name = "Szlak na Śnieżkę", description = "Z Karpacza przez kocioł Łomniczki lub Śląski Dom. Najwyższy i najbardziej wietrzny szczyt Karkonoszy.", type = "piesza", difficulty = 3, length = 6.0, duration = 150, imageUri = getResUri("szlak_sniezka")),
                    Route(name = "Szlak na Szczeliniec Wielki", description = "Góry Stołowe i labirynty skalne. Trasa po schodach z Karłowa, idealna dla rodzin.", type = "piesza", difficulty = 1, length = 2.0, duration = 60, imageUri = getResUri("szlak_szczeliniec_wielki")),
                    Route(name = "Szlak Błędne Skały", description = "Niesamowity skalny labirynt na płaskowyżu Skalniaka. Trasa łatwa i bardzo efektowna.", type = "piesza", difficulty = 1, length = 1.0, duration = 45, imageUri = getResUri("szlak_bledne_skaly")),
                    Route(name = "Szlak na Babią Górę", description = "Królowa Beskidów. Start z Przełęczy Krowiarki. Pogoda bywa tu bardzo kapryśna.", type = "piesza", difficulty = 4, length = 5.0, duration = 150, imageUri = getResUri("babia_qora")),
                    Route(name = "Szlak na Turbacz", description = "Najwyższy punkt Gorców. Z Nowego Targu Kowańca łagodnym podejściem do dużego schroniska.", type = "piesza", difficulty = 2, length = 10.0, duration = 210, imageUri = getResUri("szlak_turbacz")),
                    Route(name = "Szlak na Skrzyczne", description = "Najwyższy szczyt Beskidu Śląskiego. Podejście ze Szczyrku przez Halę Jaworzynę.", type = "piesza", difficulty = 3, length = 5.0, duration = 150, imageUri = getResUri("szlak_skrzyczne")),
                    Route(name = "Szlak na Baranią Górę", description = "Wędrówka do źródeł Wisły z Wisły Czarne przez dolinę Białej Wisełki.", type = "piesza", difficulty = 3, length = 8.0, duration = 180, imageUri = getResUri("szlak_barania_gora")),
                    Route(name = "Pętla przez Czerwone Wierchy", description = "Długa, grzbietowa trasa z Kuźnic. Panoramiczne widoki na Tatry Wysokie i Zachodnie.", type = "piesza", difficulty = 4, length = 15.0, duration = 420, imageUri = getResUri("szlak_czerwone_wierchy")),
                    Route(name = "Szlak Doliną Chochołowską", description = "Najduższa dolina w Tatrach. Idealna na wiosenny spacer wśród krokusów.", type = "piesza", difficulty = 1, length = 7.5, duration = 120, imageUri = getResUri("szlak_dolina_chocholowska")),
                    Route(name = "Szlak Doliną Kościeliską", description = "Jedna z najpiękniejszych dolin reglowych z licznymi jaskiniami i wąwozem Kraków.", type = "piesza", difficulty = 1, length = 6.0, duration = 90, imageUri = getResUri("szlak_dolina_koscieliska")),
                    Route(name = "Szlak na Halę Gąsienicową", description = "Z Kuźnic przez Boczań. Kultowe miejsce z widokiem na Kościelec i Granaty.", type = "piesza", difficulty = 2, length = 6.0, duration = 120, imageUri = getResUri("szlak_hala_gasienicowa")),

                    // 20 Tras Rowerowych
                    Route(name = "Velo Dunajec (Czorsztyn - Szczawnica)", description = "Prawdopodobnie najpiękniejsza trasa w Polsce, biegnąca wzdłuż jeziora i przełomu Dunajca.", type = "rowerowa", difficulty = 2, length = 40.0, duration = 180, imageUri = getResUri("velo_dunajec")),
                    Route(name = "Wiślana Trasa Rowerowa (Kraków - Niepołomice)", description = "Płaska i szybka trasa wałami Wisły z widokiem na Opactwo in Tyńcu i Puszczę Niepołomicką.", type = "rowerowa", difficulty = 1, length = 25.0, duration = 90, imageUri = getResUri("wislana_trasa_rowerowa")),
                    Route(name = "Szlak Orlich Gniazd (Olsztyn - Mirów)", description = "Malowniczy odcinek jury krakowsko-częstochowskiej z ruinami średniowiecznych zamków.", type = "rowerowa", difficulty = 4, length = 50.0, duration = 240, imageUri = getResUri("szlak_orlich_gniazd")),
                    Route(name = "Green Velo (Elbląg - Frombork)", description = "Północny odcinek szlaku nad Zalewem Wiślanym. Wymagające podjazdy na Wysoczyźnie Elbląskiej.", type = "rowerowa", difficulty = 3, length = 35.0, duration = 150, imageUri = getResUri("green_velo_elblag_frombork")),
                    Route(name = "Green Velo (Białystok i okolice)", description = "Spokojna trasa przez podlaskie wioski i lasy w okolicach stolicy regionu.", type = "rowerowa", difficulty = 2, length = 40.0, duration = 180, imageUri = getResUri("green_velo_bialystok")),
                    Route(name = "Pętla Tatrzańska", description = "Wymagająca trasa wokół Tatr. Legendarne podjazdy pod Głodówkę i zjazd do Zakopanego.", type = "rowerowa", difficulty = 5, length = 100.0, duration = 360, imageUri = getResUri("petla_tatrzanska")),
                    Route(name = "Szlak wokół Jeziora Czorsztyńskiego", description = "Velo Czorsztyn to nowoczesna ścieżka z widokami na dwa zamki i góry.", type = "rowerowa", difficulty = 3, length = 40.0, duration = 180, imageUri = getResUri("rower_jezioro_czorsztynskie")),
                    Route(name = "EuroVelo 10 (Półwysep Helski)", description = "Unikalna trasa wzdłuż zatoki z Władysławowa na sam cypel Helu.", type = "rowerowa", difficulty = 2, length = 35.0, duration = 150, imageUri = getResUri("eurovelo10_helski")),
                    Route(name = "Kampinoski Szlak Rowerowy", description = "Główny pierścień wokół Puszczy Kampinoskiej. Piaszczyste odcinki wymagają szerokich opon.", type = "rowerowa", difficulty = 4, length = 140.0, duration = 480, imageUri = getResUri("kampinoski_park_narodowy_sciezka")),
                    Route(name = "Puszcza Białowieska (Białowieża loop)", description = "Przejazd przez serce ostatniego pierwotnego lasu Europy. Szerokie leśne drogi.", type = "rowerowa", difficulty = 1, length = 30.0, duration = 120, imageUri = getResUri("puszcza_bialowieska")),
                    Route(name = "Szlak Stu Jezior (Okolice Sierakowa)", description = "Piękne krajobrazy Pojezierza Międzychodzko-Sierakowskiego w Wielkopolsce.", type = "rowerowa", difficulty = 3, length = 50.0, duration = 240, imageUri = getResUri("szlak_stu_jezior")),
                    Route(name = "Małopolska Droga św. Jakuba (Velo Metropolis)", description = "Odcinek EuroVelo 4 w Małopolsce, łączący historyczne miasta i miasteczka.", type = "rowerowa", difficulty = 4, length = 70.0, duration = 300, imageUri = getResUri("malopolska_velo_metropolis")),
                    Route(name = "Szlak Architektury Drewnianej (Tarnów loop)", description = "Trasa prowadząca do zabytkowych kościołów wpisanych na listę UNESCO.", type = "rowerowa", difficulty = 3, length = 40.0, duration = 180, imageUri = getResUri("szlak_arch_drew_tarnow")),
                    Route(name = "Trasa Tyniecka (Centrum Krakowa - Tyniec)", description = "Najpopularniejsza ścieżka rekreacyjna w Krakowie, idealna na krótki wypad.", type = "rowerowa", difficulty = 1, length = 12.0, duration = 45, imageUri = getResUri("trasa_tyniecka")),
                    Route(name = "Odrzańska Droga Rowerowa (Wrocław - Brzeg Dolny)", description = "Wygodna trasa wzdłuż Odry, z dala od ruchu samochodowego.", type = "rowerowa", difficulty = 2, length = 50.0, duration = 180, imageUri = getResUri("brzeg_dolny_wroclaw")),
                    Route(name = "Bory Tucholskie (Kaszubska Marszruta)", description = "System świetnych dróg rowerowych w sercu narodowego parku.", type = "rowerowa", difficulty = 2, length = 45.0, duration = 180, imageUri = getResUri("bory_tucholeskie_kaszub")),
                    Route(name = "Szlak Latarni Morskich (Wybrzeże Środkowe)", description = "Jazda wzdłuż Bałtyku, łącząca najciekawsze punkty nawigacyjne wybrzeża.", type = "rowerowa", difficulty = 3, length = 80.0, duration = 300, imageUri = getResUri("szlak_latarni_morskich")),
                    Route(name = "Szlak Krainy Otwartych Okiennic", description = "Wędrówka rowerowa przez unikalne wioski doliny Narwi: Soce, Puchły, Trześcianka.", type = "rowerowa", difficulty = 1, length = 25.0, duration = 120, imageUri = getResUri("kraina_otwartych_okiennic")),
                    Route(name = "Szlak przez Dolinę Baryczy", description = "Raj dla ornitologów. Płaska trasa wśród Stawów Milickich.", type = "rowerowa", difficulty = 1, length = 40.0, duration = 180, imageUri = getResUri("dolina_baryczy")),
                    Route(name = "Roztoczański Szlak Rowerowy (Zwierzyniec - Górecko)", description = "Przejazd przez Roztocze Środkowe, krainę szumów i gęstych borów jodłowych.", type = "rowerowa", difficulty = 3, length = 35.0, duration = 180, imageUri = getResUri("roztoczanski_szlak_rowerowy"))
                )
                routeDao.insertRoutes(initialRoutes)
            }
        }
    }

    fun addCustomRoute(route: Route) {
        viewModelScope.launch {
            routeDao.insertRoute(route)
        }
    }

    fun setRoute(newBool: Boolean) {
        _isBike.value = newBool
        _selectedRoute.value = null
        clearFilters()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDifficultyFilter(difficulty: Int?) {
        _filterDifficulty.value = difficulty
    }

    fun setMaxTimeFilter(time: Int?) {
        _filterMaxTime.value = time
    }

    fun setMaxDistanceFilter(distance: Double?) {
        _filterMaxDistance.value = distance
    }

    fun toggleFilterVisibility() {
        _isFilterVisible.value = !_isFilterVisible.value
    }

    fun clearFilters() {
        _filterDifficulty.value = null
        _filterMaxTime.value = null
        _filterMaxDistance.value = null
        _searchQuery.value = ""
    }

    fun selectRoute(route: Route?) {
        _selectedRoute.value = route
    }
}
