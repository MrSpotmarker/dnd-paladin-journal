package com.paladin.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.ui.theme.*

@Composable
fun EditBaseAbilitiesDialog(
    currentScores: AbilityScores,
    onDismiss: () -> Unit,
    onSave: (AbilityScores) -> Unit
) {
    var str by remember { mutableIntStateOf(currentScores.strength) }
    var dex by remember { mutableIntStateOf(currentScores.dexterity) }
    var con by remember { mutableIntStateOf(currentScores.constitution) }
    var intScore by remember { mutableIntStateOf(currentScores.intelligence) }
    var wis by remember { mutableIntStateOf(currentScores.wisdom) }
    var cha by remember { mutableIntStateOf(currentScores.charisma) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ Basis-Attributswerte anpassen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Ändere die Werte deines Paladins frei ab",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = BorderDark)

                // 6 Ability rows
                val abilitiesList = listOf(
                    Triple(Ability.STRENGTH, str) { v: Int -> str = v },
                    Triple(Ability.DEXTERITY, dex) { v: Int -> dex = v },
                    Triple(Ability.CONSTITUTION, con) { v: Int -> con = v },
                    Triple(Ability.INTELLIGENCE, intScore) { v: Int -> intScore = v },
                    Triple(Ability.WISDOM, wis) { v: Int -> wis = v },
                    Triple(Ability.CHARISMA, cha) { v: Int -> cha = v }
                )

                abilitiesList.forEach { (ability, value, setter) ->
                    val mod = Ability.calculateModifier(value)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "${ability.displayName} (${ability.abbreviation})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Modifikator: ${Ability.formatModifier(mod)}",
                                fontSize = 12.sp,
                                color = if (mod >= 0) PaladinGold else HealthRed
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { if (value > 1) setter(value - 1) },
                                enabled = value > 1,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = "$value",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PaladinGold,
                                modifier = Modifier.width(32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            FilledTonalButton(
                                onClick = { if (value < 30) setter(value + 1) },
                                enabled = value < 30,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderDark)

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Abbrechen")
                    }

                    Button(
                        onClick = {
                            val newScores = AbilityScores(
                                strength = str,
                                dexterity = dex,
                                constitution = con,
                                intelligence = intScore,
                                wisdom = wis,
                                charisma = cha
                            )
                            onSave(newScores)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
