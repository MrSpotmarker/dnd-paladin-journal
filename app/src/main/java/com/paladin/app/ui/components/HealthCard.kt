package com.paladin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.*

@Composable
fun HealthCard(
    currentHp: Int,
    maxHp: Int,
    tempHp: Int,
    onTakeDamage: (Int) -> Unit,
    onHeal: (Int) -> Unit,
    onSetTempHp: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTempHpDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trefferpunkte (HP)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                if (tempHp > 0) {
                    Surface(
                        color = TempHpCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TempHpCyan),
                        modifier = Modifier.clickable { showTempHpDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "+$tempHp Temp HP",
                                color = TempHpCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("✏️", fontSize = 10.sp)
                        }
                    }
                } else {
                    FilledTonalButton(
                        onClick = { showTempHpDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Temp HP", fontSize = 11.sp, color = TempHpCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HP Display in Schema "BasisHP + tempHP" (z.B. 27+3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTempHpDialog = true },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                if (tempHp > 0) {
                    Text(
                        text = "$currentHp",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentHp <= maxHp / 3) HealthRed else TextPrimary
                    )
                    Text(
                        text = "+$tempHp",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = TempHpCyan,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                    )
                    Text(
                        text = " / $maxHp",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
                    )
                } else {
                    Text(
                        text = "$currentHp",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentHp <= maxHp / 3) HealthRed else TextPrimary
                    )
                    Text(
                        text = " / $maxHp",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dual Health & Temp HP Progress Bar
            val hpFraction = if (maxHp > 0) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
            val tempFraction = if (maxHp > 0) (tempHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
            val totalFraction = (hpFraction + tempFraction).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceCardHighlight)
            ) {
                if (tempHp > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(totalFraction)
                            .background(TempHpCyan)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(hpFraction)
                        .background(if (hpFraction < 0.33f) HealthRed else PaladinGold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onTakeDamage(5) },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRed.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("-5 DMG", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { onTakeDamage(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRed.copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("-1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { onHeal(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen.copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = { onHeal(5) },
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen.copy(alpha = 0.8f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+5 HEAL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    if (showTempHpDialog) {
        TempHpDialog(
            tempHp = tempHp,
            onDismiss = { showTempHpDialog = false },
            onConfirm = {
                onSetTempHp(it)
                showTempHpDialog = false
            }
        )
    }
}

@Composable
fun TempHpDialog(
    tempHp: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var tempInput by remember { mutableStateOf(if (tempHp > 0) tempHp.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Temporäre HP anpassen", color = PaladinGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Temporäre HP verfallen gemäß 5e-Regeln bei einer Langen Rast. Bei einer Kurzen Rast bleiben sie erhalten. Schaden zieht zuerst Temp HP ab.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = tempInput,
                    onValueChange = { tempInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Temp HP Betrag") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(3, 5, 8, 10).forEach { amount ->
                        FilledTonalButton(
                            onClick = { tempInput = amount.toString() },
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+$amount", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = tempInput.toIntOrNull() ?: 0
                    onConfirm(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
            ) {
                Text("Speichern", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (tempHp > 0) {
                TextButton(
                    onClick = { onConfirm(0) }
                ) {
                    Text("Auf 0 setzen", color = HealthRed)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Abbrechen", color = TextSecondary)
                }
            }
        }
    )
}
