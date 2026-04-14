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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            Lab2Theme {
                Main()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Main() {
    val navController = rememberNavController()
    val routeViewModel: RouteViewModel = viewModel()
    val timerViewModel: TimerViewModel = viewModel()
    val activity: Activity = LocalActivity.current as Activity

    NavHost(navController = navController, startDestination = "viewType") {
        composable("viewType") {
            val windowSizeClass = calculateWindowSizeClass(activity = activity)
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    StartScreenMobile(navController, routeViewModel, timerViewModel)
                }
                else -> {
                    MainScreenTablet(routeViewModel, timerViewModel)
                }
            }
        }
        composable("start") {
            StartScreenMobile(navController, routeViewModel, timerViewModel)
        }
        composable("main") {
            MainScreen(navController, routeViewModel, timerViewModel)
        }
        composable(
            route = "detail/{name}/{description}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("description") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "Brak opisu dla tej trasy."
                }
            )
        ) { backstackEntry ->
            val name = backstackEntry.arguments?.getString("name")
            val description = backstackEntry.arguments?.getString("description")
            DetailScreen(navController, name, description, timerViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTablet(routeViewModel: RouteViewModel, timerViewModel: TimerViewModel) {
    val routes by routeViewModel.routes.collectAsState()
    val isBike by routeViewModel.bool.collectAsState()
    val selectedRoute by routeViewModel.selectedRoute.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Panel Zarządzania Trasami",
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { routeViewModel.setRoute(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBike == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Pieszo")
                        }
                        Button(
                            onClick = { routeViewModel.setRoute(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBike == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Rowerem")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (routes.isEmpty()) {
                    item {
                        Text(
                            text = "Wybierz kategorię, aby zobaczyć trasy.",
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

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (selectedRoute != null) {
                    Text(
                        text = selectedRoute!!.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedRoute!!.description,
                        modifier = Modifier.padding(top = 16.dp)
                    )
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
    timerViewModel: TimerViewModel
) {
    val isBike by routeViewModel.bool.collectAsState()
    val routes by routeViewModel.routes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    ) {
                        Button(
                            onClick = { routeViewModel.setRoute(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBike == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Pieszo")
                        }
                        Button(
                            onClick = { routeViewModel.setRoute(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBike == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Rowerem")
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isBike == null) {
                item {
                    Text(
                        text = "Wybierz kategorię powyżej.",
                        modifier = Modifier.padding(16.dp)
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
                            val encodedDescription = Uri.encode(item.description)
                            navController.navigate("detail/$encodedName/$encodedDescription")
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    name: String?,
    description: String?,
    timerViewModel: TimerViewModel
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val timerRunning by timerViewModel.running.collectAsState()
    val currentRouteName by timerViewModel.currentRouteName.collectAsState()
    
    // Explicitly check collected state variables to ensure Compose tracks them for recomposition
    timerState; timerRunning; currentRouteName

    val isTimerActive = timerState > 0 || timerRunning
    val isCorrectRoute = currentRouteName == name

    // Determine the layout state for AnimatedContent
    val fabState = when {
        !isTimerActive -> "idle"
        !isCorrectRoute -> "conflict"
        else -> "active"
    }

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
            FloatingActionButton(
                onClick = {
                    if (fabState == "idle") {
                        timerViewModel.startTimer(name)
                    }
                },
                modifier = Modifier.padding(16.dp),
                shape = if (fabState == "idle") CircleShape else RoundedCornerShape(24.dp),
                containerColor = if (fabState == "idle") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
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
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        "conflict" -> {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Timer running on:\n${currentRouteName ?: "Other route"}",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                                IconButton(
                                    onClick = { timerViewModel.restartTimer() },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, CircleShape).size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Reset",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        "active" -> {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 4.dp
                                ) {
                                    // Use timerState to ensure recomposition
                                    Text(
                                        text = timerViewModel.displayTime(),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                                            .size(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { timerViewModel.toggleTimer(name) }) {
                                            Icon(
                                                imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (timerRunning) "Pause" else "Start",
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                            .size(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { timerViewModel.restartAndStart(name) }) {
                                            Icon(
                                                Icons.Default.Refresh,
                                                contentDescription = "Restart",
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                                            .size(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(onClick = { timerViewModel.restartTimer() }) {
                                            Icon(
                                                Icons.Default.Stop,
                                                contentDescription = "Reset",
                                                tint = MaterialTheme.colorScheme.onErrorContainer
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "O trasie:",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description ?: "Trasa nie posiada opisu",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}
