package com.paladin.app.ui.screens.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.ui.theme.*

@Composable
fun AttributesStepCard(
    standardScores: Map<Ability, Int>,
    plus2Ability: Ability,
    plus1Ability: Ability,
    onUpdateScore: (Ability, Int) -> Unit,
    onUpdatePlus2: (Ability) -> Unit,
    onUpdatePlus1: (Ability) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPointsSpent = AbilityScores.calculateTotalPointsSpent(standardScores)
    val pointsRemaining = AbilityScores.calculatePointsRemaining(standardScores)
    val isAttributeOverrun = pointsRemaining < 0

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Attribute festlegen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )

            // Budget Indicator Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isAttributeOverrun) HealthRed.copy(alpha = 0.15f)
                        else PaladinGold.copy(alpha = 0.12f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isAttributeOverrun) HealthRed else PaladinGold.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAttributeOverrun) "⚠️ Punktebudget überschritten" else "✨ Point-Buy Punkte:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAttributeOverrun) HealthRed else PaladinGold
                        )
                        Text(
                            text = if (isAttributeOverrun)
                                "$totalPointsSpent / 27 Pkt (+${-pointsRemaining})"
                            else
                                "$pointsRemaining / 27 verbleibend",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAttributeOverrun) HealthRed else ProficiencyGreen
                        )
                    }
                    Text(
                        text = if (isAttributeOverrun)
                            "Du hast mehr als 27 Punkte verteilt. Das Weitergehen ist trotzdem erlaubt (z.B. für gewürfelte Werte oder Hausregeln)."
                        else
                            "Standard Point-Buy Budget sind 27 Punkte (Startwert 8 = 0 Pkt).",
                        fontSize = 11.sp,
                        color = if (isAttributeOverrun) HealthRed.copy(alpha = 0.9f) else TextSecondary
                    )
                }
            }

            // Ability Score adjustment rows
            Ability.entries.forEach { ability ->
                val base = standardScores[ability] ?: 10
                val bonus = (if (plus2Ability == ability) 2 else 0) + (if (plus1Ability == ability) 1 else 0)
                val finalScore = base + bonus
                val mod = Ability.calculateModifier(finalScore)
                val cost = AbilityScores.calculatePointCost(base)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = "${ability.displayName} (${ability.abbreviation})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Basis $base ($cost Pkt) ${if (bonus > 0) "+$bonus Bonus" else ""}",
                            fontSize = 11.sp,
                            color = if (bonus > 0) PaladinGold else TextSecondary
                        )
                    }

                    Row(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { if (base > 8) onUpdateScore(ability, base - 1) },
                            enabled = base > 8,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("-") }

                        Text(
                            text = "$finalScore (${Ability.formatModifier(mod)})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        FilledTonalButton(
                            onClick = { if (base < 18) onUpdateScore(ability, base + 1) },
                            enabled = base < 18,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) { Text("+") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "2024 Hintergrund-Boni zuweisen:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )

            // +2 Picker
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("+2 Bonus auf:", fontSize = 12.sp, color = TextPrimary)
                var expanded2 by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expanded2 = true }) {
                        Text(plus2Ability.displayName)
                    }
                    DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                        Ability.entries.forEach { ab ->
                            DropdownMenuItem(
                                text = { Text(ab.displayName) },
                                onClick = {
                                    onUpdatePlus2(ab)
                                    expanded2 = false
                                }
                            )
                        }
                    }
                }
            }

            // +1 Picker
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("+1 Bonus auf:", fontSize = 12.sp, color = TextPrimary)
                var expanded1 by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expanded1 = true }) {
                        Text(plus1Ability.displayName)
                    }
                    DropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                        Ability.entries.filter { it != plus2Ability }.forEach { ab ->
                            DropdownMenuItem(
                                text = { Text(ab.displayName) },
                                onClick = {
                                    onUpdatePlus1(ab)
                                    expanded1 = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
