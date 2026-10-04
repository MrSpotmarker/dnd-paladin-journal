package com.paladin.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.*
import kotlin.random.Random

@Composable
fun ShortRestDialog(
    remainingHitDice: Int,
    conMod: Int,
    onDismiss: () -> Unit,
    onConfirm: (diceSpent: Int, hpHealed: Int) -> Unit
) {
    var diceToSpend by remember { mutableIntStateOf(if (remainingHitDice > 0) 1 else 0) }
    var manualHpHealed by remember { mutableStateOf("") }
    var rolledDiceHp by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Kurze Rast (Short Rest)",
                color = PaladinGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Trefferwürfel verfügbar: $remainingHitDice d10",
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                if (remainingHitDice > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Würfel ausgeben: $diceToSpend", color = TextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = { if (diceToSpend > 1) diceToSpend-- },
                                enabled = diceToSpend > 1,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("-") }
                            FilledTonalButton(
                                onClick = { if (diceToSpend < remainingHitDice) diceToSpend++ },
                                enabled = diceToSpend < remainingHitDice,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("+") }
                        }
                    }

                    Button(
                        onClick = {
                            var sum = 0
                            repeat(diceToSpend) {
                                sum += Random.nextInt(1, 11) + conMod
                            }
                            rolledDiceHp = sum.coerceAtLeast(1)
                            manualHpHealed = rolledDiceHp.toString()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🎲 $diceToSpend d10 + ${diceToSpend * conMod} CON würfeln")
                    }

                    OutlinedTextField(
                        value = manualHpHealed,
                        onValueChange = { manualHpHealed = it },
                        label = { Text("Erhaltende Heilung (HP)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Keine Trefferwürfel mehr vorhanden!",
                        color = HealthRed,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    color = SurfaceCardHighlight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✨ 2024 Paladin Regel:\nDu regenerierst automatisch 1 verbrauchte Channel Divinity Nutzung!",
                        color = SmiteBlue,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hp = manualHpHealed.toIntOrNull() ?: rolledDiceHp
                    onConfirm(diceToSpend, hp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen)
            ) {
                Text("Rast beenden")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = TextSecondary)
            }
        }
    )
}

@Composable
fun LongRestDialog(
    level: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val recoveredDice = kotlin.math.max(1, level / 2)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Lange Rast (Long Rest)",
                color = PaladinGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Eine 8-stündige Erholungsphase regeneriert:", fontSize = 14.sp, color = TextPrimary)
                Text(text = "✓ Hit Points vollständig auf Maximum geheilt", fontSize = 13.sp, color = LayOnHandsGreen)
                Text(text = "✓ Alle Zauberplätze (Spell Slots) wiederhergestellt", fontSize = 13.sp, color = SpellSlotPurple)
                Text(text = "✓ Handauflegen-Pool (Lay on Hands) voll aufgefüllt", fontSize = 13.sp, color = LayOnHandsGreen)
                Text(text = "✓ Channel Divinity Nutzungen voll aufgeladen", fontSize = 13.sp, color = SmiteBlue)
                Text(text = "✓ $recoveredDice Trefferwürfel (Hit Dice) regeneriert", fontSize = 13.sp, color = PaladinGold)
                Text(text = "✓ Temporary HP werden auf 0 gesetzt", fontSize = 13.sp, color = TextSecondary)
                Text(text = "✓ Todes-Rettungswürfe zurückgesetzt", fontSize = 13.sp, color = TextSecondary)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
            ) {
                Text("Lange Rast ausführen", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = TextSecondary)
            }
        }
    )
}
