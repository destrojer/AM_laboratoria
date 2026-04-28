package com.example.pam_lab.views.viewElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pam_lab.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanelComponent(routeViewModel: RouteViewModel) {
    val filterDifficulty by routeViewModel.filterDifficulty.collectAsState()
    val filterMaxTime by routeViewModel.filterMaxTime.collectAsState()
    val filterMaxDistance by routeViewModel.filterMaxDistance.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Trudność (Tekstowa)
        Text(
            text = "Trudność:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        val levels = listOf(
            1 to "Bardzo łatwa", 2 to "Łatwa", 3 to "Średnia",
            4 to "Trudna", 5 to "Bardzo trudna"
        )
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(levels) { (level, name) ->
                val color = when(level) {
                    1 -> Color(0xFF4CAF50)
                    2 -> Color(0xFF2196F3)
                    3 -> Color(0xFFFFA000)
                    4 -> Color(0xFFF44336)
                    else -> Color.Black
                }

                FilterChip(
                    selected = filterDifficulty == level,
                    onClick = {
                        routeViewModel.setDifficultyFilter(if (filterDifficulty == level) null else level)
                    },
                    label = { Text(name, fontSize = 12.sp) },
                    leadingIcon = {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                    }
                )
            }
        }

        // Czas
        Text(
            text = "Maksymalny czas:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        val timeOptions = listOf(30, 60, 120, 180, 300)
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timeOptions) { time ->
                FilterChip(
                    selected = filterMaxTime == time,
                    onClick = {
                        routeViewModel.setMaxTimeFilter(if (filterMaxTime == time) null else time)
                    },
                    label = { Text("Do $time min", fontSize = 12.sp) }
                )
            }
        }

        // Dystans
        Text(
            text = "Maksymalny dystans:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        val distanceOptions = listOf(2.0, 5.0, 10.0, 20.0, 50.0, 100.0)
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(distanceOptions) { dist ->
                FilterChip(
                    selected = filterMaxDistance == dist,
                    onClick = {
                        routeViewModel.setMaxDistanceFilter(if (filterMaxDistance == dist) null else dist)
                    },
                    label = { Text("Do ${dist.toInt()} km", fontSize = 12.sp) }
                )
            }
        }

        TextButton(
            onClick = { routeViewModel.clearFilters() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Wyczyść wszystko", fontSize = 12.sp)
        }
    }
}
