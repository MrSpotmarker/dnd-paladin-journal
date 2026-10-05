package com.paladin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.*

@Composable
fun EditMoneyDialog(
    currentGold: Double,
    currentSilver: Int,
    currentCopper: Int,
    onDismiss: () -> Unit,
    onSave: (gold: Double, silver: Int, copper: Int) -> Unit
) {
    var goldInput by remember {
        mutableStateOf(if (currentGold % 1.0 == 0.0) currentGold.toInt().toString() else currentGold.toString())
    }
    var silverInput by remember { mutableStateOf(currentSilver.toString()) }
    var copperInput by remember { mutableStateOf(currentCopper.toString()) }

    fun adjustGold(delta: Double) {
        val parsed = goldInput.toDoubleOrNull() ?: currentGold
        val newVal = maxOf(0.0, parsed + delta)
        goldInput = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else "%.1f".format(newVal).replace(',', '.')
    }

    fun adjustSilver(delta: Int) {
        val parsed = silverInput.toIntOrNull() ?: currentSilver
        val newVal = maxOf(0, parsed + delta)
        silverInput = newVal.toString()
    }

    fun adjustCopper(delta: Int) {
        val parsed = copperInput.toIntOrNull() ?: currentCopper
        val newVal = maxOf(0, parsed + delta)
        copperInput = newVal.toString()
    }

    val parsedGold = goldInput.toDoubleOrNull() ?: currentGold
    val parsedSilver = silverInput.toIntOrNull() ?: currentSilver
    val parsedCopper = copperInput.toIntOrNull() ?: currentCopper
    val totalEquivalent = parsedGold + (parsedSilver / 10.0) + (parsedCopper / 100.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💰", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Geldbeutel verwalten", fontWeight = FontWeight.Bold, color = PaladinGold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Trage deine Münzen getrennt nach Gold, Silber und Kupfer ein:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                // ---------------- GOLD SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🪙", fontSize = 16.sp)
                            Text("Goldstücke (GP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PaladinGold)
                        }

                        OutlinedTextField(
                            value = goldInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("""^\d*([.,]\d{0,2})?$"""))) {
                                    goldInput = input.replace(',', '.')
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                focusedLabelColor = PaladinGold
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(1.0, 5.0, 10.0, 50.0).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustGold(delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = ProficiencyGreen.copy(alpha = 0.2f),
                                        contentColor = ProficiencyGreen
                                    )
                                ) {
                                    Text("+${delta.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            listOf(1.0, 5.0, 10.0).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustGold(-delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = HealthRed.copy(alpha = 0.2f),
                                        contentColor = HealthRed
                                    )
                                ) {
                                    Text("-${delta.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ---------------- SILVER SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🥈", fontSize = 16.sp)
                            Text("Silberstücke (SP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("(10 SP = 1 GP)", fontSize = 11.sp, color = TextSecondary)
                        }

                        OutlinedTextField(
                            value = silverInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("""^\d*$"""))) {
                                    silverInput = input
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 5, 10).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustSilver(delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = ProficiencyGreen.copy(alpha = 0.2f),
                                        contentColor = ProficiencyGreen
                                    )
                                ) {
                                    Text("+$delta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            listOf(1, 5, 10).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustSilver(-delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = HealthRed.copy(alpha = 0.2f),
                                        contentColor = HealthRed
                                    )
                                ) {
                                    Text("-$delta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ---------------- COPPER SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🥉", fontSize = 16.sp)
                            Text("Kupferstücke (CP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PaladinGoldBright)
                            Text("(100 CP = 1 GP)", fontSize = 11.sp, color = TextSecondary)
                        }

                        OutlinedTextField(
                            value = copperInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("""^\d*$"""))) {
                                    copperInput = input
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 5, 10).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustCopper(delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = ProficiencyGreen.copy(alpha = 0.2f),
                                        contentColor = ProficiencyGreen
                                    )
                                ) {
                                    Text("+$delta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            listOf(1, 5, 10).forEach { delta ->
                                FilledTonalButton(
                                    onClick = { adjustCopper(-delta) },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = HealthRed.copy(alpha = 0.2f),
                                        contentColor = HealthRed
                                    )
                                ) {
                                    Text("-$delta", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ---------------- SUMMARY & CONVERSION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkNavyBackground.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Gesamtwert:", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "≈ ${"%.2f".format(totalEquivalent)} GP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGold
                            )
                        }

                        // Auto-optimise conversion button (e.g. 150 CP -> 1 GP + 5 SP)
                        OutlinedButton(
                            onClick = {
                                val totalCp = (parsedGold * 100).toLong() + (parsedSilver * 10).toLong() + parsedCopper.toLong()
                                val optGold = totalCp / 100
                                val remAfterGold = totalCp % 100
                                val optSilver = remAfterGold / 10
                                val optCopper = remAfterGold % 10
                                goldInput = optGold.toString()
                                silverInput = optSilver.toString()
                                copperInput = optCopper.toString()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("⚖️ In höchste Münzen wechseln", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        maxOf(0.0, parsedGold),
                        maxOf(0, parsedSilver),
                        maxOf(0, parsedCopper)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
            ) {
                Text("Speichern", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = TextSecondary)
            }
        }
    )
}
