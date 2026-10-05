package com.paladin.app.ui.screens.levelup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun AsiDistributionCard(
    baseScores: AbilityScores,
    modifiedAbilities: AbilityScores,
    selectedLevel: Int,
    onScoresChanged: (AbilityScores) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedLevel < 4) return

    val pointsAdded = Ability.entries.sumOf {
        (modifiedAbilities.getScore(it) - baseScores.getScore(it)).coerceAtLeast(0)
    }
    val targetAsiBudget = when {
        selectedLevel >= 16 -> 8
        selectedLevel >= 12 -> 6
        selectedLevel >= 8 -> 4
        selectedLevel >= 4 -> 2
        else -> 0
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalDivider(color = BorderDark)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💪 Attributs-Erhöhung (ASI):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Surface(
                color = PaladinGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "$pointsAdded Punkte vergeben (Richtwert: $targetAsiBudget)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGoldBright,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Text(
            text = "Verteile Punkte frei auf deine Basisattribute (maximal 20). Pro 4 Stufen stehen dir nach Standardregeln 2 Punkte zu.",
            fontSize = 11.sp,
            color = TextSecondary
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Ability.entries.chunked(2).forEach { rowAbilities ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowAbilities.forEach { ability ->
                        val currentVal = modifiedAbilities.getScore(ability)
                        val originalVal = baseScores.getScore(ability)
                        Surface(
                            color = SurfaceCardHighlight.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ability.displayName, fontSize = 11.sp, color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "$currentVal",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentVal > originalVal) ProficiencyGreen else PaladinGold
                                        )
                                        if (currentVal > originalVal) {
                                            Text(" (+${currentVal - originalVal})", fontSize = 11.sp, color = ProficiencyGreen)
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilledTonalButton(
                                        onClick = {
                                            if (currentVal > originalVal) {
                                                onScoresChanged(modifiedAbilities.withScore(ability, currentVal - 1))
                                            }
                                        },
                                        enabled = currentVal > originalVal,
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", fontSize = 13.sp)
                                    }
                                    FilledTonalButton(
                                        onClick = {
                                            if (currentVal < 20) {
                                                onScoresChanged(modifiedAbilities.withScore(ability, currentVal + 1))
                                            }
                                        },
                                        enabled = currentVal < 20,
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
