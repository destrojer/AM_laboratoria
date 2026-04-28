package com.example.pam_lab.views.mobile

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pam_lab.database.Route
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel
import com.example.pam_lab.views.viewElements.FilterPanelComponent
import com.example.pam_lab.views.viewElements.SearchBarComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
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
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBiking == true) "Trasy Rowerowe" else "Trasy Piesze",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSearchActive) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SearchBarComponent(routeViewModel)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AnimatedVisibility(visible = isFilterVisible) {
                        FilterPanelComponent(routeViewModel)
                    }
                }

                items(routes) { route ->
                    val routeTimes = savedTimes.filter { it.routeName == route.name }
                    val bestTimeSeconds = routeTimes.minByOrNull { it.timeInSeconds }?.timeInSeconds
                    val formattedBestTime = bestTimeSeconds?.let { timerViewModel.formatTime(it) }

                    MobileRouteCard(
                        route = route,
                        bestTime = formattedBestTime,
                        onClick = {
                            val encodedName = Uri.encode(route.name)
                            navController.navigate("detail/$encodedName")
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun MobileRouteCard(
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ZDJĘCIE TRASY
            AsyncImage(
                model = route.imageUri ?: com.example.pam_lab.R.drawable.app_icon,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${route.length} km", style = MaterialTheme.typography.bodySmall)
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(Icons.Default.Timer, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${route.duration} min", style = MaterialTheme.typography.bodySmall)
                }

                if (bestTime != null) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = bestTime, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Kropka trudności na końcu
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(difficultyColor)
                    .align(Alignment.Top)
            )
        }
    }
}
