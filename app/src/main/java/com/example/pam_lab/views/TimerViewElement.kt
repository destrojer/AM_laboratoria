package com.example.pam_lab.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pam_lab.viewmodel.TimerViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerControls(
    name: String?,
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by timerViewModel.timerState.collectAsState()
    val timerRunning by timerViewModel.running.collectAsState()
    val currentRouteName by timerViewModel.currentRouteName.collectAsState()
    val context = LocalContext.current

    timerState; timerRunning; currentRouteName

    val isTimerSessionActive = currentRouteName != null
    val isCorrectRoute = currentRouteName == name

    val fabState = when {
        !isTimerSessionActive -> "idle"
        !isCorrectRoute -> "conflict"
        else -> "active"
    }

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = {
                if (fabState == "idle") {
                    timerViewModel.startTimer(name)
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .shadow(6.dp, if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp))
                .clip(if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp)),
            shape = if (fabState == "idle") CircleShape else RoundedCornerShape(28.dp),
            containerColor = if (fabState == "idle") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            AnimatedContent(
                targetState = fabState,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(clip = false))
                },
                label = "TimerStateTransition"
            ) { target ->
                when (target) {
                    "idle" -> {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Start Timer",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    "conflict" -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Timer aktywny na innej trasie:",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = currentRouteName ?: "Inna trasa",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            IconButton(
                                onClick = { timerViewModel.restartTimer() },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Zresetuj timer",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    "active" -> {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    text = timerViewModel.displayTime(),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    letterSpacing = 1.sp
                                )
                            }

                            AnimatedVisibility(
                                visible = !timerRunning && timerState > 0 && isCorrectRoute,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Text(
                                    text = "Przytrzymaj ikonę wznowienia \naby zapisać trasę",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (timerRunning) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.secondary
                                        )
                                        .combinedClickable(
                                            onClick = { timerViewModel.toggleTimer(name) },
                                            onLongClick = {
                                                if (!timerRunning && timerState > 0) {
                                                    timerViewModel.saveTimeToDb(context)
                                                }
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (timerRunning) "Wstrzymaj" else "Wznów",
                                        tint = if (timerRunning) MaterialTheme.colorScheme.onSecondaryContainer
                                        else MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (!timerRunning) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.errorContainer
                                        )
                                        .clickable {
                                            if (!timerRunning) {
                                                timerViewModel.restartAndStart(name)
                                                timerViewModel.stopTimer()
                                            } else {
                                                timerViewModel.restartTimer()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (timerRunning) Icons.Default.Stop else Icons.Default.Refresh,
                                        contentDescription = if (timerRunning) "Zamknij" else "Resetuj",
                                        tint = if (timerRunning) MaterialTheme.colorScheme.onErrorContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}