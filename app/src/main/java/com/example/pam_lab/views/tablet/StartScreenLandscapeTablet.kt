package com.example.pam_lab.views.tablet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreenLandscapeTablet(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Wybierz rodzaj trasy - Tablet", fontSize = 28.sp) }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    routeViewModel.setRoute(false)
                    navController.navigate("main")
                },
                modifier = Modifier.padding(16.dp).weight(1f)
            ) {
                Text(text = "Pieszo", fontSize = 36.sp)
            }
            Button(
                onClick = {
                    routeViewModel.setRoute(true)
                    navController.navigate("main")
                },
                modifier = Modifier.padding(16.dp).weight(1f)
            ) {
                Text(text = "Rowerem", fontSize = 36.sp)
            }
        }
    }
}
