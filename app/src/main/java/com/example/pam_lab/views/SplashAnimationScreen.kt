package com.example.pam_lab.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pam_lab.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashAnimationScreen(onAnimationFinished: () -> Unit) {
    val rotation = remember { Animatable(0f) }
    val orbitRadius = 140.dp

    LaunchedEffect(Unit) {
        // Animacja jednego pełnego obrotu (2 sekundy)
        rotation.animateTo(
            targetValue = 2 * PI.toFloat(),
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        onAnimationFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            // CENTRALNE LOGO
            Surface(
                modifier = Modifier.size(160.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = null,
                        modifier = Modifier.size(240.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // PIESZY (Góra)
            OrbitingIcon(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                angle = rotation.value,
                radius = orbitRadius,
                color = Color(0xFF4CAF50)
            )

            // ROWER (Dół - przesunięty o PI, czyli 180 stopni)
            OrbitingIcon(
                icon = Icons.AutoMirrored.Filled.DirectionsBike,
                angle = rotation.value + PI.toFloat(),
                radius = orbitRadius,
                color = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun OrbitingIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    angle: Float,
    radius: androidx.compose.ui.unit.Dp,
    color: Color
) {
    // Obliczanie pozycji na okręgu
    val x = with(androidx.compose.ui.platform.LocalDensity.current) { (radius.toPx() * sin(angle)) }
    val y = with(androidx.compose.ui.platform.LocalDensity.current) { (-radius.toPx() * cos(angle)) }

    Box(
        modifier = Modifier
            .offset(
                x = with(androidx.compose.ui.platform.LocalDensity.current) { x.toDp() },
                y = with(androidx.compose.ui.platform.LocalDensity.current) { y.toDp() }
            )
            .size(56.dp)
            .clip(CircleShape)
            .background(color)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize()
        )
    }
}
