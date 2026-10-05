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

@Composable
fun ShortRestDialog(
    remainingHitDice: Int,
    conMod: Int,
    onDismiss: () -> Unit,
    onConfirm: (diceSpent: Int) -> Unit
) {
    var diceToSpend by remember { mutableIntStateOf(if (remainingHitDice > 0) 1 else 0) }

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
                    text = "Verfügbare Trefferwürfel: $remainingHitDice d10",
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                if (remainingHitDice > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Würfel verbrauchen: $diceToSpend", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = { if (diceToSpend > 0) diceToSpend-- },
                                enabled = diceToSpend > 0,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("-") }
                            FilledTonalButton(
                                onClick = { if (diceToSpend < remainingHitDice) diceToSpend++ },
                                enabled = diceToSpend < remainingHitDice,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) { Text("+") }
                        }
                    }

                    // 🛡️ CON Bonus Info
                    val conSign = if (conMod >= 0) "+$conMod" else "$conMod"
                    val totalCon = diceToSpend * conMod
                    val totalConSign = if (totalCon >= 0) "+$totalCon" else "$totalCon"

                    Surface(
                        color = SurfaceCardHighlight.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🛡️", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "Konstitutions-Bonus: $conSign HP pro Würfel",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaladinGold
                                )
                                if (diceToSpend > 0) {
                                    Text(
                                        text = "Formel am Tisch: $diceToSpend d10 $totalConSign HP",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Keine Trefferwürfel mehr vorhanden (0 d10).",
                        color = TextSecondary,
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
                    onConfirm(diceToSpend)
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
                Text(text = "✓ Heroische Inspiration regeneriert (Mensch: 1)", fontSize = 13.sp, color = PaladinGoldBright)
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
