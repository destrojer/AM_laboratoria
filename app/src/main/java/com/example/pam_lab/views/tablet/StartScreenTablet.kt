package com.example.pam_lab.views.tablet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pam_lab.R
import com.example.pam_lab.viewmodel.RouteViewModel
import com.example.pam_lab.viewmodel.TimerViewModel

@Composable
fun StartScreenTablet(
    navController: NavController,
    routeViewModel: RouteViewModel,
    timerViewModel: TimerViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(240.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.size(380.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Wybierz aktywność",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(80.dp))

            Column(
                modifier = Modifier.widthIn(max = 700.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Użyto unikalnej nazwy TabletCategoryCardPortrait
                TabletCategoryCardPortrait(
                    title = "Pieszo",
                    subtitle = "Górskie szlaki i spokojne spacery",
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    gradient = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
                    onClick = {
                        routeViewModel.setRoute(false)
                        navController.navigate("main")
                    }
                )

                TabletCategoryCardPortrait(
                    title = "Rowerem",
                    subtitle = "Trasy MTB, szosowe i wycieczki krajoznawcze",
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    gradient = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                    onClick = {
                        routeViewModel.setRoute(true)
                        navController.navigate("main")
                    }
                )
            }
        }
    }
}

@Composable
private fun TabletCategoryCardPortrait(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(40.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 48.dp)
                    .size(140.dp)
                    .graphicsLayer(alpha = 0.2f),
                tint = Color.White
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
