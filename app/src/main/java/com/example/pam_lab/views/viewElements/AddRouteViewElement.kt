package com.example.pam_lab.views.viewElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pam_lab.database.Route


@Composable
fun AddRouteDialog(
    onDismiss: () -> Unit,
    onSave: (Route) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isBike by remember { mutableStateOf(false) }
    var difficulty by remember { mutableIntStateOf(1) }
    var length by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() &&
            (length.toDoubleOrNull() ?: 0.0) > 0.0 &&
            (duration.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj własną trasę") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa trasy *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Typ:", fontWeight = FontWeight.SemiBold)
                    Button(
                        onClick = { isBike = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isBike) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (!isBike) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Piesza") }
                    Button(
                        onClick = { isBike = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBike) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isBike) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Rowerowa") }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val (difficultyText, difficultyColor) = when(difficulty) {
                        1 -> "Bardzo łatwa" to Color(0xFF4CAF50)
                        2 -> "Łatwa" to Color(0xFF2196F3)
                        3 -> "Średnia" to Color(0xFFFFA000)
                        4 -> "Trudna" to Color(0xFFF44336)
                        else -> "Bardzo trudna" to Color.Black
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Trudność: ", fontWeight = FontWeight.SemiBold)
                        Text(text = difficultyText, color = difficultyColor, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) { i ->
                            val level = i + 1
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (level <= difficulty) difficultyColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { difficulty = level },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (level <= difficulty) difficultyColor else Color.Gray.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Dystans (km) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = length.isNotEmpty() && (length.toDoubleOrNull() == null || length.toDoubleOrNull()!! <= 0)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Czas (min) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = duration.isNotEmpty() && (duration.toIntOrNull() == null || duration.toIntOrNull()!! <= 0)
                )

                Text(
                    text = "* Pola wymagane",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                enabled = isFormValid,
                onClick = {
                    onSave(Route(
                        name = name,
                        description = description,
                        type = if (isBike) "rowerowa" else "piesza",
                        difficulty = difficulty,
                        length = length.toDoubleOrNull() ?: 0.0,
                        duration = duration.toIntOrNull() ?: 0
                    ))
                }
            ) { Text("Zapisz") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } }
    )
}