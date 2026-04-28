package com.example.pam_lab.views.tablet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pam_lab.database.Route
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel
import com.example.pam_lab.views.viewElements.DetailContent
import com.example.pam_lab.views.viewElements.FilterPanelComponent
import com.example.pam_lab.views.viewElements.SearchBarComponent
import com.example.pam_lab.views.viewElements.TimerControls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenLandscapeTablet(
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel,
    isSearchActive: Boolean
) {
    val routes by routeViewModel.routes.collectAsState()
    val selectedRoute by routeViewModel.selectedRoute.collectAsState()
    val isBiking by routeViewModel.bool.collectAsState()
    val isFilterVisible by routeViewModel.isFilterVisible.collectAsState()
    val savedTimes by timerViewModel.allSavedTimes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isBiking == true) Icons.AutoMirrored.Filled.DirectionsBike else Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBiking == true) "Trasy Rowerowe" else "Trasy Piesze",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = selectedRoute != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TimerControls(name = selectedRoute?.name, timerViewModel = timerViewModel)
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // LISTA TRAS (LEWA STRONA)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 8.dp)
            ) {
                if (isSearchActive) {
                    SearchBarComponent(routeViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AnimatedVisibility(visible = isFilterVisible) {
                            Column {
                                FilterPanelComponent(routeViewModel)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    if (routes.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Brak tras spełniających kryteria.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    items(routes) { route ->
                        val routeTimes = savedTimes.filter { it.routeName == route.name }
                        val bestTimeSeconds = routeTimes.minByOrNull { it.timeInSeconds }?.timeInSeconds
                        val formattedBestTime = bestTimeSeconds?.let { timerViewModel.formatTime(it) }

                        RouteListItem(
                            route = route,
                            bestTime = formattedBestTime,
                            isSelected = selectedRoute?.id == route.id,
                            onClick = { routeViewModel.selectRoute(route) }
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            // SEPARATOR
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // SZCZEGÓŁY (PRAWA STRONA)
            Column(
                modifier = Modifier
                    .weight(1.8f)
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp)
            ) {
                if (selectedRoute != null) {
                    DetailContent(selectedRoute!!, timerViewModel)
                } else {
                    EmptyDetailState()
                }
            }
        }
    }
}

@Composable
fun RouteListItem(
    route: Route,
    bestTime: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (difficultyText, difficultyColor) = when(route.difficulty) {
        1 -> "Bardzo łatwa" to Color(0xFF4CAF50)
        2 -> "Łatwa" to Color(0xFF2196F3)
        3 -> "Średnia" to Color(0xFFFFA000)
        4 -> "Trudna" to Color(0xFFF44336)
        else -> "Bardzo trudna" to Color.Black
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // MINIATURKA ZDJĘCIA
            AsyncImage(
                model = route.imageUri ?: com.example.pam_lab.R.drawable.app_icon,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${route.length} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${route.duration} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (bestTime != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Najlepszy czas",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFA000)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Best: $bestTime",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Kropka trudności
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(difficultyColor)
                    .align(Alignment.Top)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyDetailState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Wybierz trasę z listy",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Szczegóły i pomiar czasu pojawią się tutaj",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
