package com.paladin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.ChannelDivinityDetails
import com.paladin.app.model.DetailItem
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SmiteBlue
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelDivinityCard(
    remaining: Int,
    maxUses: Int,
    oath: String?,
    chaMod: Int,
    activeBuffIds: Set<String>,
    canRegainSpellSlot: Boolean,
    onUse: () -> Unit,
    onRestore: () -> Unit,
    onToggleBuff: (buffId: String, consumesCharge: Boolean) -> Unit,
    onHarnessDivinePower: () -> Unit,
    modifier: Modifier = Modifier,
    onShowDetail: (DetailItem) -> Unit = {}
) {
    val isSacredWeaponActive = activeBuffIds.any { it.equals("sacred_weapon", ignoreCase = true) }
    val isVowOfEnmityActive = activeBuffIds.any { it.equals("vow_of_enmity", ignoreCase = true) }

    val hasDevotion = oath == null || oath.contains("Devotion", ignoreCase = true)
    val hasVengeance = oath == null || oath.contains("Vengeance", ignoreCase = true)

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .combinedClickable(
                            onClick = { isExpanded = !isExpanded },
                            onLongClick = {
                                onShowDetail(ChannelDivinityDetails.getGeneralChannelDivinityDetail(oath, maxUses, chaMod))
                            }
                        )
                ) {
                    Text("⚡", fontSize = 16.sp)
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Göttliche Macht (Channel Divinity)",
                                fontWeight = FontWeight.Bold,
                                color = SmiteBlue,
                                fontSize = 14.sp
                            )
                            IconButton(
                                onClick = {
                                    onShowDetail(ChannelDivinityDetails.getGeneralChannelDivinityDetail(oath, maxUses, chaMod))
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Text("ℹ️", fontSize = 11.sp)
                            }
                        }
                        Text(
                            text = "Regeneriert 1 Ladung bei Kurzer Rast • Details lange drücken",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        (0 until maxUses).forEach { index ->
                            val isUsed = index >= remaining
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isUsed) SurfaceCardHighlight else SmiteBlue)
                                    .border(1.dp, if (isUsed) BorderDark else SmiteBlue, CircleShape)
                                    .clickable {
                                        if (isUsed) onRestore() else onUse()
                                    }
                            )
                        }
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Göttliche Macht einklappen" else "Göttliche Macht ausklappen",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Interactive powers (Expandable)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = BorderDark)

                    // Sacred Weapon (Devotion)
                    if (hasDevotion) {
                        val sacredBonus = max(1, chaMod)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSacredWeaponActive) PaladinGold.copy(alpha = 0.15f) else SurfaceCardHighlight)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .combinedClickable(
                                        onClick = { onShowDetail(ChannelDivinityDetails.getSacredWeaponDetail(chaMod)) },
                                        onLongClick = { onShowDetail(ChannelDivinityDetails.getSacredWeaponDetail(chaMod)) }
                                    )
                                    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "🌟 Heilige Waffe (Sacred Weapon)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSacredWeaponActive) PaladinGoldBright else TextPrimary
                                    )
                                    Text("ℹ️", fontSize = 10.sp)
                                }
                                Text(
                                    text = "+$sacredBonus auf Waffen-Angriffe für 10 Min. (Info antippen)",
                                    fontSize = 10.sp,
                                    color = if (isSacredWeaponActive) PaladinGold else TextSecondary
                                )
                            }

                            if (isSacredWeaponActive) {
                                Button(
                                    onClick = { onToggleBuff("sacred_weapon", false) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PaladinGold,
                                        contentColor = DarkNavyBackground
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Aktiv ✕", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onToggleBuff("sacred_weapon", true) },
                                    enabled = remaining > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SmiteBlue,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Aktivieren", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Vow of Enmity (Vengeance)
                    if (hasVengeance) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVowOfEnmityActive) PaladinGold.copy(alpha = 0.15f) else SurfaceCardHighlight)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .combinedClickable(
                                        onClick = { onShowDetail(ChannelDivinityDetails.getVowOfEnmityDetail()) },
                                        onLongClick = { onShowDetail(ChannelDivinityDetails.getVowOfEnmityDetail()) }
                                    )
                                    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "🎯 Gelübde der Feindschaft (Vow of Enmity)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isVowOfEnmityActive) PaladinGoldBright else TextPrimary
                                    )
                                    Text("ℹ️", fontSize = 10.sp)
                                }
                                Text(
                                    text = "Vorteil auf Angriffe gegen Ziel (1 Min. • Info antippen)",
                                    fontSize = 10.sp,
                                    color = if (isVowOfEnmityActive) PaladinGold else TextSecondary
                                )
                            }

                            if (isVowOfEnmityActive) {
                                Button(
                                    onClick = { onToggleBuff("vow_of_enmity", false) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PaladinGold,
                                        contentColor = DarkNavyBackground
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Aktiv ✕", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onToggleBuff("vow_of_enmity", true) },
                                    enabled = remaining > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SmiteBlue,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Aktivieren", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Turn the Unholy (Devotion)
                    if (hasDevotion) {
                        val turnDc = 8 + 2 + chaMod
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceCardHighlight)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .combinedClickable(
                                        onClick = { onShowDetail(ChannelDivinityDetails.getTurnTheUnholyDetail(turnDc)) },
                                        onLongClick = { onShowDetail(ChannelDivinityDetails.getTurnTheUnholyDetail(turnDc)) }
                                    )
                                    .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "☀️ Untote vertreiben (Turn the Unholy)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text("ℹ️", fontSize = 10.sp)
                                }
                                Text(
                                    text = "Aktion • Untote/Unholde fliehen 1 Min. (DC $turnDc)",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            FilledTonalButton(
                                onClick = onUse,
                                enabled = remaining > 0,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Wirken", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Harness Divine Power (2024 optional/standard rule)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardHighlight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .combinedClickable(
                                    onClick = { onShowDetail(ChannelDivinityDetails.getHarnessDivinePowerDetail()) },
                                    onLongClick = { onShowDetail(ChannelDivinityDetails.getHarnessDivinePowerDetail()) }
                                )
                                .padding(end = 8.dp, top = 2.dp, bottom = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "⚡ Göttliche Kraft bündeln",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("ℹ️", fontSize = 10.sp)
                            }
                            Text(
                                text = "Regeneriert 1 verbrauchten Zauberslot (Info antippen)",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        FilledTonalButton(
                            onClick = onHarnessDivinePower,
                            enabled = remaining > 0 && canRegainSpellSlot,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("+1 Slot", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Full Details link row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onShowDetail(ChannelDivinityDetails.getGeneralChannelDivinityDetail(oath, maxUses, chaMod))
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 Vollständige Channel Divinity Regeln & Details ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SmiteBlue
                        )
                    }
                }
            }
        }
    }
}
