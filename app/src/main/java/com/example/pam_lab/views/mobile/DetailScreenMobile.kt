package com.example.pam_lab.views.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel
import com.example.pam_lab.views.viewElements.DetailContent
import com.example.pam_lab.views.viewElements.TimerControls

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