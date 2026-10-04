package com.paladin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.SpellSlotState
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, TempHpCyan)
                    ) {
                        Text(
                            text = "+$tempHp Temp HP",
                            color = TempHpCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HP Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
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

            // Health bar
            val hpFraction = if (maxHp > 0) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { hpFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (hpFraction < 0.33f) HealthRed else PaladinGold,
                trackColor = SurfaceCardHighlight
            )

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
}

@Composable
fun LayOnHandsCard(
    remaining: Int,
    maxPool: Int,
    onUse: (Int) -> Unit,
    onCureCondition: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Handauflegen (Lay on Hands)",
                    fontWeight = FontWeight.Bold,
                    color = LayOnHandsGreen,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "$remaining / $maxPool HP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            Text(
                text = "2024 Regel: Kostet nur eine Bonus-Aktion!",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { if (remaining >= 1) onUse(1) },
                    enabled = remaining >= 1,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("1 HP", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { if (remaining >= 5) onUse(5) },
                    enabled = remaining >= 5,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("5 HP", fontSize = 12.sp)
                }
                Button(
                    onClick = onCureCondition,
                    enabled = remaining >= 5,
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen),
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Zustand heilen (5 HP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SpellSlotsTracker(
    slots: List<SpellSlotState>,
    onUseSlot: (Int) -> Unit,
    onRestoreSlot: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Zauberplätze (Spell Slots)",
                fontWeight = FontWeight.Bold,
                color = SpellSlotPurple,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(10.dp))

            slots.forEach { slot ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grad ${slot.level}:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (0 until slot.maxSlots).forEach { index ->
                            val isUsed = index < slot.usedSlots
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isUsed) SurfaceCardHighlight else SpellSlotPurple)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isUsed) BorderDark else SpellSlotPurple,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (isUsed) onRestoreSlot(slot.level) else onUseSlot(slot.level)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isUsed) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
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

@Composable
fun AbilityBox(
    ability: Ability,
    score: Int,
    mod: Int,
    save: Int,
    isProficientSave: Boolean,
    hasAura: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ability.abbreviation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = Ability.formatModifier(mod),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "$score",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = if (isProficientSave) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Save ${Ability.formatModifier(save)}",
                    fontSize = 10.sp,
                    fontWeight = if (isProficientSave) FontWeight.Bold else FontWeight.Normal,
                    color = if (isProficientSave) PaladinGold else TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
