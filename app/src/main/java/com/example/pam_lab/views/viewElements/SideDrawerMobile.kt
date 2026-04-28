package com.example.pam_lab.views.viewElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val drawerWidth = if (isExpanded) 240.dp else 56.dp
    val selectedType by routeViewModel.bool.collectAsState()
    val isFilterVisible by routeViewModel.isFilterVisible.collectAsState()
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
                .padding(vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
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
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
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

            // PRZYCISK FILTRU
            DrawerItem(
                icon = Icons.Default.FilterList,
                label = "Filtry",
                isExpanded = isExpanded,
                selected = isFilterVisible,
                onClick = { routeViewModel.toggleFilterVisibility() }
            )

            // LINIA I ZMIANA MOTYWU (Przeniesione wyżej)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))

            DrawerItem(
                icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                label = if (isDarkTheme) "Jasny" else "Ciemny",
                isExpanded = isExpanded,
                onClick = onThemeToggle
            )

            // Elastyczny odstęp na dole
            Spacer(modifier = Modifier.weight(1f))
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
