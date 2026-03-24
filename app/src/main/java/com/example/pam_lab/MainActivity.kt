package com.example.pam_lab

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam_lab.ui.theme.Lab2Theme
import com.example.pam_lab.viewmodel.RouteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2Theme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("detail/{name}/{description}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType},
                navArgument("description") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "Brak opisu dla tej trasy."
                })
            ) { backstackEntry ->
            val name = backstackEntry.arguments?.getString("name")
            val description = backstackEntry.arguments?.getString("description")
            DetailScreen(navController = navController, name = name, description=description)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, routeViewModel: RouteViewModel = viewModel()) {
    val isBike by routeViewModel.bool.collectAsState()
    val routes by routeViewModel.routes.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    ) {
                        Button(
                            onClick = {routeViewModel.setRoute(false)},
                            enabled = isBike || routes.isEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Pieszo")
                        }
                        Button(
                            onClick = {routeViewModel.setRoute(true)},
                            enabled = !isBike || routes.isEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(text = "Rowerem")
                        }
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        items(routes) { item ->
            Text(
            text = item.name,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val encodedName = Uri.encode(item.name)
                    val encodedDescription = Uri.encode(item.description?: "Brak opisu")
                    navController.navigate("detail/$encodedName/$encodedDescription")
                }
                .padding(16.dp)
            )}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, name: String?, description: String?) {
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
        }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = " O trasie:",
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Text(
                text = description ?: "Trasa nie posiada opisu",
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

    }
}
