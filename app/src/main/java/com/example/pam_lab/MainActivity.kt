package com.example.pam_lab

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam_lab.ui.theme.Lab2Theme
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            
            Lab2Theme(darkTheme = isDarkTheme) {
                Main(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Main(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val routeViewModel: RouteViewModel = viewModel()
    val timerViewModel: TimerViewModel = viewModel()
    val activity: Activity = LocalActivity.current as Activity

    var isDrawerExpanded by rememberSaveable { mutableStateOf(false) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showDrawer = currentRoute != "viewType" && currentRoute != "start"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showDrawer) {
                LeftSideDrawer(
                    isExpanded = isDrawerExpanded,
                    onToggleExpand = { isDrawerExpanded = !isDrawerExpanded },
                    routeViewModel = routeViewModel,
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    isSearchActive = isSearchActive,
                    onSearchToggle = { isSearchActive = !isSearchActive }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                NavHost(navController = navController, startDestination = "viewType") {
                    composable("viewType") {
                        val windowSizeClass = calculateWindowSizeClass(activity = activity)
                        when (windowSizeClass.widthSizeClass) {
                            WindowWidthSizeClass.Compact -> {
                                StartScreenMobile(navController, routeViewModel, timerViewModel)
                            }
                            else -> {
                                MainScreenTablet(routeViewModel, timerViewModel, isSearchActive)
                            }
                        }
                    }
                    composable("start") {
                        StartScreenMobile(navController, routeViewModel, timerViewModel)
                    }
                    composable("main") {
                        MainScreen(navController, routeViewModel, timerViewModel, isSearchActive)
                    }
                    composable(
                        route = "detail/{name}",
                        arguments = listOf(
                            navArgument("name") { type = NavType.StringType }
                        )
                    ) { backstackEntry ->
                        val name = backstackEntry.arguments?.getString("name")
                        DetailScreen(navController, name, routeViewModel, timerViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LeftSideDrawer(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    routeViewModel: RouteViewModel,
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isSearchActive: Boolean,
    onSearchToggle: () -> Unit
) {
    val drawerWidth = if (isExpanded) 150.dp else 48.dp
    val selectedType by routeViewModel.bool.collectAsState()
    
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth)
            .shadow(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DrawerItem(
                icon = if (isExpanded) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                label = if (isExpanded) "Zwiń" else "",
                isExpanded = isExpanded,
                onClick = onToggleExpand
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                label = "Piesza",
                isExpanded = isExpanded,
                selected = selectedType == false,
                onClick = { 
                    routeViewModel.setRoute(false)
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.DirectionsBike,
                label = "Rowerowa",
                isExpanded = isExpanded,
                selected = selectedType == true,
                onClick = { 
                    routeViewModel.setRoute(true)
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DrawerItem(
                icon = Icons.Default.Search,
                label = "Szukaj",
                isExpanded = isExpanded,
                selected = isSearchActive,
                onClick = onSearchToggle
            )
            
            DrawerItem(
                icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                label = if (isDarkTheme) "Jasny" else "Ciemny",
                isExpanded = isExpanded,
                onClick = onThemeToggle
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isExpanded: Boolean,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isExpanded) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SearchBarComponent(routeViewModel: RouteViewModel) {
    val searchQuery by routeViewModel.searchQuery.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { routeViewModel.setSearchQuery(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusRequester(focusRequester),
        placeholder = { Text("Wyszukaj trasę...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { routeViewModel.setSearchQuery("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTablet(
    routeViewModel: RouteViewModel, 
    timerViewModel: TimerViewModel,
    isSearchActive: Boolean
) {
    val routes by routeViewModel.routes.collectAsState()
    val selectedRoute by routeViewModel.selectedRoute.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Panel Zarządzania Trasami")
                }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isSearchActive) {
                    SearchBarComponent(routeViewModel)
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (routes.isEmpty()) {
                        item {
                            Text(
                                text = "Brak wyników lub nie wybrano kategorii.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(routes) { item ->
                        Text(
                            text = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { routeViewModel.selectRoute(item) }
                                .padding(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedRoute != null) {
                    DetailContent(selectedRoute!!, timerViewModel)
                } else {
                    Text(
                        text = "Wybierz trasę z listy, aby zobaczyć szczegóły.",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreenMobile(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Wybierz rodzaj trasy",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                },
                modifier = Modifier.height(120.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        routeViewModel.setRoute(false)
                        navController.navigate("main")
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(text = "Pieszo", fontSize = 42.sp)
                }
                Button(
                    onClick = {
                        routeViewModel.setRoute(true)
                        navController.navigate("main")
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(text = "Rowerem", fontSize = 42.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel,
    isSearchActive: Boolean
) {
    val routes by routeViewModel.routes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Lista tras")
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (isSearchActive) {
                SearchBarComponent(routeViewModel)
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (routes.isEmpty()) {
                    item {
                        Text(
                            text = "Brak wyników lub nie wybrano kategorii.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(routes) { item ->
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val encodedName = Uri.encode(item.name)
                                navController.navigate("detail/$encodedName")
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    name: String?,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel
) {
    val routes by routeViewModel.routes.collectAsState()
    val route = routes.find { it.name == name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = name ?: "Brak nazwy") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            TimerControls(name = name, timerViewModel = timerViewModel)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (route != null) {
                DetailContent(route, timerViewModel)
            } else {
                Text(text = "Nie znaleziono trasy.")
            }
        }
    }
}

@Composable
fun DetailContent(route: com.example.pam_lab.database.Route, timerViewModel: TimerViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "O trasie:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = route.description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(icon = Icons.Default.Route, label = "Dystans", value = "${route.length} km")
                    InfoRow(icon = Icons.Default.History, label = "Czas", value = "${route.duration} min")
                    
                    val (difficultyText, difficultyColor) = when(route.difficulty) {
                        1 -> "Bardzo łatwa" to Color(0xFF4CAF50) // Zielony
                        2 -> "Łatwa" to Color(0xFF2196F3)        // Niebieski
                        3 -> "Średnia" to Color(0xFFFFEB3B)      // Żółty
                        4 -> "Trudna" to Color(0xFFF44336)       // Czerwony
                        else -> "Bardzo trudna" to Color.Black
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Trudność:", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(difficultyColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = difficultyText, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "$label:", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TimerControls(
    name: String?,
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val timerRunning by timerViewModel.running.collectAsState()
    val currentRouteName by timerViewModel.currentRouteName.collectAsState()
    
    // Explicitly check collected state variables to ensure Compose tracks them for recomposition
    timerState; timerRunning; currentRouteName

    val isTimerSessionActive = currentRouteName != null
    val isCorrectRoute = currentRouteName == name

    val fabState = when {
        !isTimerSessionActive -> "idle"
        !isCorrectRoute -> "conflict"
        else -> "active"
    }

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = {
                if (fabState == "idle") {
                    timerViewModel.startTimer(name)
                }
            },
            modifier = Modifier.padding(16.dp),
            shape = if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp),
            containerColor = if (fabState == "idle") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            AnimatedContent(
                targetState = fabState,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(clip = false))
                },
                label = "TimerStateTransition"
            ) { target ->
                when (target) {
                    "idle" -> {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Start Timer",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    "conflict" -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Timer aktywny na innej trasie:",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentRouteName ?: "Inna trasa",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            IconButton(
                                onClick = { timerViewModel.restartTimer() },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Zresetuj timer",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    "active" -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    text = timerViewModel.displayTime(),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    letterSpacing = 1.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (timerRunning) MaterialTheme.colorScheme.secondaryContainer 
                                            else MaterialTheme.colorScheme.secondary
                                        )
                                        .clickable { timerViewModel.toggleTimer(name) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (timerRunning) "Wstrzymaj" else "Wznów",
                                        tint = if (timerRunning) MaterialTheme.colorScheme.onSecondaryContainer 
                                               else MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (!timerRunning) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.errorContainer
                                        )
                                        .clickable { 
                                            if (!timerRunning) {
                                                timerViewModel.restartAndStart(name)
                                                timerViewModel.stopTimer()
                                            } else {
                                                timerViewModel.restartTimer()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (!timerRunning) Icons.Default.Refresh else Icons.Default.Stop,
                                        contentDescription = if (!timerRunning) "Resetuj" else "Zamknij",
                                        tint = if (!timerRunning) MaterialTheme.colorScheme.onSurfaceVariant
                                               else MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
