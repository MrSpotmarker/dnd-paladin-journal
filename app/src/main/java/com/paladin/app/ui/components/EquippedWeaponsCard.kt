package com.paladin.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.AttackInfo
import com.paladin.app.model.DetailItem
import com.paladin.app.ui.theme.*

@Composable
fun EquippedWeaponsCard(
    attacks: List<AttackInfo>,
    onShowDetail: (DetailItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("⚔️", fontSize = 16.sp)
                    Text(
                        text = "Waffen & Angriffe",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                }
                Text(
                    text = "${attacks.size} ausgerüstet",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            HorizontalDivider(color = BorderDark)

            if (attacks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine Waffe angelegt.\nGehe ins Inventar, um eine Waffe auszurüsten!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attacks.forEach { attack ->
                        EquippedWeaponRow(
                            attack = attack,
                            onShowDetail = { onShowDetail(DetailItem.ItemInfo(attack.item)) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EquippedWeaponRow(
    attack: AttackInfo,
    onShowDetail: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onShowDetail
            ),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Main Top Summary: Name, Attack Bonus, Damage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = attack.item.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (attack.isMasteryActive && attack.masteryEffect != null) {
                            Surface(
                                color = PaladinGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = attack.masteryEffect.propertyName,
                                    color = PaladinGoldBright,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${attack.damageString} (${attack.damageType})",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${attack.attackBonus}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = PaladinGold
                        )
                        Text(
                            text = "ATK",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Details einklappen" else "Details ausklappen",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Animated Expandable Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = BorderDark.copy(alpha = 0.6f))

                    if (attack.isMasteryActive && attack.masteryEffect != null) {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Meisterschaft (${attack.masteryEffect.propertyName}): ${attack.masteryEffect.description}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Damage breakdown (e.g. +3 Stärke, +2 Duellieren)
                    if (attack.damageBreakdown.isNotEmpty()) {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "📊 Schadens-Zusammensetzung (${attack.damageString}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaladinGoldBright
                                )
                                attack.damageBreakdown.forEach { part ->
                                    Text(
                                        text = "• $part",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Attack bonus breakdown
                    if (attack.attackBreakdown.isNotBlank()) {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎯 Angriffs-Bonus (+${attack.attackBonus}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaladinGoldBright
                                )
                                Text(
                                    text = attack.attackBreakdown,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    if (attack.activeBuffNotes.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            attack.activeBuffNotes.forEach { note ->
                                Surface(
                                    color = PaladinGold.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = note,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PaladinGoldBright,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Basis-Würfel: ${attack.item.damageDice} • Gewicht: ${attack.item.weightLbs} lbs",
                        fontSize = 10.sp,
                        color = TextSecondary.copy(alpha = 0.8f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clickable { onShowDetail() },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚔️ Waffendetails & Meisterschaft (oder lange drücken) ➔",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PaladinGold
                        )
                    }
                }
            }
        }
    }
}
