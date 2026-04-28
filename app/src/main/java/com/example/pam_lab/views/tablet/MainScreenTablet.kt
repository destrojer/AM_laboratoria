package com.example.pam_lab.views.tablet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pam_lab.database.Route
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel
import com.example.pam_lab.views.viewElements.FilterPanelComponent
import com.example.pam_lab.views.viewElements.SearchBarComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTablet(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel,
    isSearchActive: Boolean
) {
    val routes by routeViewModel.routes.collectAsState()
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
                            text = if (isBiking == true) "Eksploruj Trasy Rowerowe" else "Eksploruj Trasy Piesze",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSearchActive) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    SearchBarComponent(routeViewModel)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Panel filtrów przewijany razem z siatką tras
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AnimatedVisibility(visible = isFilterVisible) {
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            FilterPanelComponent(routeViewModel)
                        }
                    }
                }

                if (routes.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Brak tras spełniających kryteria.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                items(routes) { route ->
                    val routeTimes = savedTimes.filter { it.routeName == route.name }
                    val bestTimeSeconds = routeTimes.minByOrNull { it.timeInSeconds }?.timeInSeconds
                    val formattedBestTime = bestTimeSeconds?.let { timerViewModel.formatTime(it) }

                    TabletGridItem(
                        route = route,
                        bestTime = formattedBestTime,
                        onClick = {
                            navController.navigate("detail/${route.name}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TabletGridItem(
    route: Route,
    bestTime: String?,
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
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(difficultyColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = difficultyText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row {
                    InfoChip(icon = Icons.Default.Map, text = "${route.length} km")
                    Spacer(modifier = Modifier.width(12.dp))
                    InfoChip(icon = Icons.Default.Timer, text = "${route.duration} min")
                }

                if (bestTime != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EmojiEvents, 
                            null, 
                            modifier = Modifier.size(18.dp), 
                            tint = Color(0xFFFFA000)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = bestTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
