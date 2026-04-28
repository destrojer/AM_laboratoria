package com.example.pam_lab.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pam_lab.database.Route
import com.example.pam_lab.database.RouteTimer
import com.example.pam_lab.viewmodel.TimerViewModel

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