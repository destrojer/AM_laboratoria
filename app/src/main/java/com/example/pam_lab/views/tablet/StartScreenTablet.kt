package com.example.pam_lab.views.tablet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun StartScreenTablet(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Wybierz rodzaj trasy - Tablet",
                        fontSize = 40.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
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
            Button(
                onClick = {
                    routeViewModel.setRoute(false)
                    navController.navigate("main")
                },
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .width(400.dp)
            ) {
                Text(text = "Pieszo", fontSize = 48.sp)
            }
            Button(
                onClick = {
                    routeViewModel.setRoute(true)
                    navController.navigate("main")
                },
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .width(400.dp)
            ) {
                Text(text = "Rowerem", fontSize = 48.sp)
            }
        }
    }
}
