package com.example.pam_lab.views.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.views.AddRouteDialog


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
            //Drawer toggle
            DrawerItem(
                icon = if (isExpanded) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
                label = if (isExpanded) "Zwiń" else "",
                isExpanded = isExpanded,
                onClick = onToggleExpand
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Walk routes
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

            //Bike routes
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

            //Add location
            DrawerItem(
                icon = Icons.Default.AddLocationAlt,
                label = "Dodaj trasę",
                isExpanded = isExpanded,
                onClick = { showAddDialog = true }
            )

            //Search toggle
            DrawerItem(
                icon = Icons.Default.Search,
                label = "Szukaj",
                isExpanded = isExpanded,
                selected = isSearchActive,
                onClick = onSearchToggle
            )

            //Theme toggle
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