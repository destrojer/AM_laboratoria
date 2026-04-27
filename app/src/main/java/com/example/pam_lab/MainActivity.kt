package com.example.pam_lab

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam_lab.database.Route
import com.example.pam_lab.database.RouteTimer
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
    
    val context = LocalContext.current
    val timerViewModel: TimerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimerViewModel(context.applicationContext) as T
            }
        }
    )
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
fun AddRouteDialog(
    onDismiss: () -> Unit,
    onSave: (Route) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isBike by remember { mutableStateOf(false) }
    var difficulty by remember { mutableStateOf(1) }
    var length by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() && 
            (length.toDoubleOrNull() ?: 0.0) > 0.0 && 
            (duration.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj własną trasę") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("Nazwa trasy *") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, 
                    onValueChange = { description = it }, 
                    label = { Text("Opis") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Typ:", fontWeight = FontWeight.SemiBold)
                    Button(
                        onClick = { isBike = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isBike) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (!isBike) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Piesza") }
                    Button(
                        onClick = { isBike = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBike) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isBike) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Rowerowa") }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val (difficultyText, difficultyColor) = when(difficulty) {
                        1 -> "Bardzo łatwa" to Color(0xFF4CAF50)
                        2 -> "Łatwa" to Color(0xFF2196F3)
                        3 -> "Średnia" to Color(0xFFFFA000)
                        4 -> "Trudna" to Color(0xFFF44336)
                        else -> "Bardzo trudna" to Color.Black
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Trudność: ", fontWeight = FontWeight.SemiBold)
                        Text(text = difficultyText, color = difficultyColor, fontWeight = FontWeight.Bold)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) { i ->
                            val level = i + 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (level <= difficulty) difficultyColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { difficulty = level },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (level <= difficulty) difficultyColor else Color.Gray.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = length, 
                    onValueChange = { length = it }, 
                    label = { Text("Dystans (km) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = length.isNotEmpty() && (length.toDoubleOrNull() == null || length.toDoubleOrNull()!! <= 0)
                )
                OutlinedTextField(
                    value = duration, 
                    onValueChange = { duration = it }, 
                    label = { Text("Czas (min) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = duration.isNotEmpty() && (duration.toIntOrNull() == null || duration.toIntOrNull()!! <= 0)
                )
                
                Text(
                    text = "* Pola wymagane",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                enabled = isFormValid,
                onClick = {
                    onSave(Route(
                        name = name,
                        description = description,
                        type = if (isBike) "rowerowa" else "piesza",
                        difficulty = difficulty,
                        length = length.toDoubleOrNull() ?: 0.0,
                        duration = duration.toIntOrNull() ?: 0
                    ))
                }
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
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
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddRouteDialog(
            onDismiss = { showAddDialog = false },
            onSave = {
                routeViewModel.addCustomRoute(it)
                showAddDialog = false
            }
        )
    }
    
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

            // DODAJ TRASĘ
            DrawerItem(
                icon = Icons.Default.AddLocationAlt,
                label = "Dodaj trasę",
                isExpanded = isExpanded,
                onClick = { showAddDialog = true }
            )
            
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
fun DetailContent(route: Route, timerViewModel: TimerViewModel) {
    val savedTimes by timerViewModel.allSavedTimes.collectAsState()
    val routeTimes = savedTimes.filter { it.routeName == route.name }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var timeToDelete by remember { mutableStateOf<RouteTimer?>(null) }

    if (showDeleteDialog && timeToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false 
                timeToDelete = null
            },
            title = { Text("Usuwanie czasu") },
            text = { Text("Czy na pewno chcesz usunąć ten wynik? Tej operacji nie da się cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        timerViewModel.deleteTime(timeToDelete!!)
                        showDeleteDialog = false
                        timeToDelete = null
                    }
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    timeToDelete = null
                }) {
                    Text("Anuluj")
                }
            }
        )
    }

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
                        1 -> "Bardzo łatwa" to Color(0xFF4CAF50)
                        2 -> "Łatwa" to Color(0xFF2196F3)
                        3 -> "Średnia" to Color(0xFFFFA000)
                        4 -> "Trudna" to Color(0xFFF44336)
                        else -> "Bardzo trudna" to Color.Black
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SignalCellularAlt, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Trudność:", fontWeight = FontWeight.SemiBold, modifier = Modifier.width(100.dp))
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(difficultyColor))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(difficultyText, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        if (routeTimes.isNotEmpty()) {
            item {
                Text(
                    text = "Twoje czasy:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(routeTimes) { timeRecord ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = timerViewModel.formatTime(timeRecord.timeInSeconds),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = timerViewModel.formatDate(timeRecord.date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        IconButton(onClick = { 
                            timeToDelete = timeRecord
                            showDeleteDialog = true 
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń czas",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Text(
                    text = "Brak zapisanych czasów dla tej trasy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(100.dp)) }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerControls(
    name: String?,
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val timerRunning by timerViewModel.running.collectAsState()
    val currentRouteName by timerViewModel.currentRouteName.collectAsState()
    val context = LocalContext.current
    
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
            modifier = Modifier
                .padding(16.dp)
                .shadow(6.dp, if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp))
                .clip(if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp)),
            shape = if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp),
            containerColor = if (fabState == "idle") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
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
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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

                            AnimatedVisibility(
                                visible = !timerRunning && timerState > 0 && isCorrectRoute,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = "Przytrzymaj ikonę wznowienia \naby zapisać trasę",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
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
                                        .combinedClickable(
                                            onClick = { timerViewModel.toggleTimer(name) },
                                            onLongClick = { 
                                                if (!timerRunning && timerState > 0) {
                                                    timerViewModel.saveTimeToDb(context)
                                                }
                                            }
                                        ),
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
                                        imageVector = if (timerRunning) Icons.Default.Stop else Icons.Default.Refresh,
                                        contentDescription = if (timerRunning) "Zamknij" else "Resetuj",
                                        tint = if (timerRunning) MaterialTheme.colorScheme.onErrorContainer 
                                               else MaterialTheme.colorScheme.onSurfaceVariant,
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
