package com.example.pam_lab

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam_lab.ui.theme.Lab2Theme
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel
import com.example.pam_lab.views.mobile.DetailScreen
import com.example.pam_lab.views.mobile.LeftSideDrawer
import com.example.pam_lab.views.mobile.MainScreen
import com.example.pam_lab.views.mobile.MainScreenLandscapeMobile
import com.example.pam_lab.views.mobile.StartScreenLandscapeMobile
import com.example.pam_lab.views.mobile.StartScreenMobile
import com.example.pam_lab.views.tablet.MainScreenLandscapeTablet
import com.example.pam_lab.views.tablet.MainScreenTablet
import com.example.pam_lab.views.tablet.StartScreenLandscapeTablet
import com.example.pam_lab.views.tablet.StartScreenTablet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.view.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 600L

            slideUp.doOnEnd { splashScreenView.remove() }
            slideUp.start()
        }

        super.onCreate(savedInstanceState)
        
        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { 
                mutableStateOf(sharedPref.getBoolean("isDarkTheme", false)) 
            }
            
            Lab2Theme(darkTheme = isDarkTheme) {
                Main(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { 
                        isDarkTheme = !isDarkTheme
                        sharedPref.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Main(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val routeViewModel: RouteViewModel = viewModel()

    val context = LocalContext.current
    val timerViewModel: TimerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimerViewModel(context.applicationContext) as T
            }
        }
    )
    val activity: Activity = LocalActivity.current as Activity

    var isDrawerExpanded by rememberSaveable { mutableStateOf(false) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showDrawer = currentRoute != "viewType" && currentRoute != "start"

    val windowSizeClass = calculateWindowSizeClass(activity = activity)
    val isTablet = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showDrawer) {
                LeftSideDrawer(
                    isExpanded = isDrawerExpanded,
                    onToggleExpand = { isDrawerExpanded = !isDrawerExpanded },
                    routeViewModel = routeViewModel,
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    isSearchActive = isSearchActive,
                    onSearchToggle = { isSearchActive = !isSearchActive }
                )
            }

            //Navigation to other views
            Box(modifier = Modifier.weight(1f)) {
                NavHost(navController = navController, startDestination = "start") {
                    // Start screen
                    composable("start") {
                        when {
                            isTablet && isLandscape -> {
                                // Tablet Landscape
                                StartScreenLandscapeTablet(
                                    navController,
                                    routeViewModel,
                                    timerViewModel
                                )
                            }

                            isTablet && !isLandscape -> {
                                // Tablet Portrait
                                StartScreenTablet(navController, routeViewModel, timerViewModel)
                            }

                            !isTablet && isLandscape -> {
                                // Phone Landscape
                                StartScreenLandscapeMobile(
                                    navController,
                                    routeViewModel,
                                    timerViewModel
                                )
                            }

                            else -> {
                                // Phone Portrait
                                StartScreenMobile(navController, routeViewModel, timerViewModel)
                            }
                        }
                    }
//
//                    // View type selection screen
//                    composable("viewType") {
//                        when {
//                            isTablet && isLandscape -> {
//                                // Tablet Landscape
//                                MainScreenLandscapeTablet(routeViewModel, timerViewModel, isSearchActive)
//                            }
//                            isTablet && !isLandscape -> {
//                                // Tablet Portrait
//                                MainScreenTablet(routeViewModel, timerViewModel, isSearchActive)
//                            }
//                            !isTablet && isLandscape -> {
//                                // Phone Landscape
//                                StartScreenLandscapeMobile(navController, routeViewModel, timerViewModel)
//                            }
//                            else -> {
//                                // Phone Portrait
//                                StartScreenMobile(navController, routeViewModel, timerViewModel)
//                            }
//                        }
//                    }

                    // Main view with routes
                    composable("main") {
                        when {
                            isTablet && isLandscape -> {
                                // Tablet Landscape
                                MainScreenLandscapeTablet(
                                    routeViewModel,
                                    timerViewModel,
                                    isSearchActive
                                )
                            }

                            isTablet && !isLandscape -> {
                                // Tablet Portrait
                                MainScreenTablet(routeViewModel, timerViewModel, isSearchActive)
                            }

                            !isTablet && isLandscape -> {
                                // Phone Landscape
                                MainScreenLandscapeMobile(
                                    navController,
                                    routeViewModel,
                                    timerViewModel,
                                    isSearchActive
                                )
                            }

                            else -> {
                                // Phone Portrait
                                MainScreen(
                                    navController,
                                    routeViewModel,
                                    timerViewModel,
                                    isSearchActive
                                )
                            }
                        }
                    }

                    // Detail view with route info and timer
                    composable(
                        route = "detail/{name}",
                        arguments = listOf(
                            navArgument("name") { type = NavType.StringType }
                        )
                    ) { backstackEntry ->
                        val name = backstackEntry.arguments?.getString("name")
                        when {
                            isTablet && isLandscape -> {
                                // Tablet Landscape
                                DetailScreen(navController, name, routeViewModel, timerViewModel)
                            }

                            isTablet && !isLandscape -> {
                                // Tablet Portrait
                                DetailScreen(navController, name, routeViewModel, timerViewModel)
                            }

                            !isTablet && isLandscape -> {
                                // Phone Landscape
                                DetailScreen(navController, name, routeViewModel, timerViewModel)
                            }

                            else -> {
                                // Phone Portrait
                                DetailScreen(navController, name, routeViewModel, timerViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}