package com.example.pam_lab.views.tablet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
            // Logo sekcja
            Surface(
                modifier = Modifier.size(240.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "Logo",
                        modifier = Modifier.size(360.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Witaj w PAM Lab",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Wybierz rodzaj trasy, aby rozpocząć przygodę",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                TabletCategoryCard(
                    title = "Pieszo",
                    subtitle = "Górskie szlaki i spacery",
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    gradient = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        routeViewModel.setRoute(false) // Reset wyboru i ustawienie rodzaju
                        navController.navigate("main")
                    }
                )

                TabletCategoryCard(
                    title = "Rowerem",
                    subtitle = "Trasy MTB i szosowe",
                    icon = Icons.AutoMirrored.Filled.DirectionsBike,
                    gradient = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        routeViewModel.setRoute(true) // Reset wyboru i ustawienie rodzaju
                        navController.navigate("main")
                    }
                )
            }
        }
    }
}

@Composable
fun TabletCategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(alpha = 0.3f),
                    tint = Color.White
                )
            }
        }
    }
}
