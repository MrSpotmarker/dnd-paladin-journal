package com.paladin.app.ui.components.steed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.DetailItem
import com.paladin.app.model.SpellSlotState
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SmiteBlue
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FaithfulSteedCard(
    isSummoned: Boolean,
    freeUsageUsed: Boolean,
    currentHp: Int,
    maxHp: Int,
    spellAttackBonus: Int,
    spellSaveDc: Int,
    creatureType: String,
    isSpecialUsed: Boolean,
    spellSlots: List<SpellSlotState>,
    onSummonFree: () -> Unit,
    onSummonWithSlot: (Int) -> Unit,
    onDismissSteed: () -> Unit,
    onUpdateHp: (current: Int, maxOverride: Int?) -> Unit,
    onSelectCreatureType: (String) -> Unit,
    onToggleSpecialUsed: () -> Unit,
    onShowDetail: (DetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showEditHpDialog by remember { mutableStateOf(false) }

    val level2Slot = spellSlots.firstOrNull { it.level == 2 }
    val hasLevel2Slot = level2Slot != null && level2Slot.remainingSlots > 0

    val typeDisplayName = when (creatureType.lowercase()) {
        "fey", "fee" -> "Fee (Fey)"
        "fiend", "unhold" -> "Unhold (Fiend)"
        else -> "Himmlisch (Celestial)"
    }

    val damageTypeName = when (creatureType.lowercase()) {
        "fey", "fee" -> "Psychisch"
        "fiend", "unhold" -> "Nekrotisch"
        else -> "Gleißend"
    }

    val typeIcon = when (creatureType.lowercase()) {
        "fey", "fee" -> "🦋"
        "fiend", "unhold" -> "🔥"
        else -> "🌟"
    }

    val hpFraction = if (maxHp > 0) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 1f
    val hpBarColor = when {
        hpFraction > 0.5f -> ProficiencyGreen
        hpFraction > 0.2f -> PaladinGoldBright
        else -> HealthRed
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header (Clickable to collapse/expand)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Text("🐴", fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Treues Reittier (Faithful Steed)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGold
                            )
                        }
                        Text(
                            text = "Stufe 5 Klassenmerkmal • Find Steed",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        color = if (isSummoned) ProficiencyGreen.copy(alpha = 0.2f) else SurfaceCardHighlight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isSummoned) "✓ Gerufen" else "Nicht aktiv",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSummoned) ProficiencyGreen else TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(28.dp)) {
                        Text(
                            text = if (isExpanded) "▲" else "▼",
                            color = PaladinGoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Collapsed Compact Summary Row
            if (!isExpanded && isSummoned) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHighlight.copy(alpha = 0.6f))
                        .clickable { isExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️ $currentHp / $maxHp HP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = hpBarColor
                    )
                    Text("🛡️ 13 RK", fontSize = 11.sp, color = TextPrimary)
                    Text("💨 60 ft", fontSize = 11.sp, color = TextPrimary)
                    Text("$typeIcon $typeDisplayName", fontSize = 11.sp, color = PaladinGoldBright)
                    Text("⏳ Dauerhaft", fontSize = 11.sp, color = TextSecondary)
                }
            }

            // Expanded Full Content
            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 1. Creature Type Selector Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Wesenheit des Reittiers (Typ):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("Celestial", "🌟 Himmlisch", "Gleißend & Heilende Berührung"),
                                Triple("Fey", "🦋 Fee", "Psychisch & Schritt der Fey"),
                                Triple("Fiend", "🔥 Unhold", "Nekrotisch & Finsterer Blick")
                            ).forEach { (typeKey, label) ->
                                val isSelected = creatureType.equals(typeKey, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .combinedClickable(
                                            onClick = { onSelectCreatureType(typeKey) },
                                            onLongClick = {
                                                onShowDetail(
                                                    DetailItem.FeatureInfo(
                                                        title = "$label (Otherworldly Steed)",
                                                        subtitle = "D&D 2024 Find Steed Typ",
                                                        badge = typeKey,
                                                        icon = label.take(2).trim(),
                                                        description = "Beim Rufen des Treuen Reittiers bestimmst du seine kosmische Natur: Himmlisch, Fee oder Unhold. Dies bestimmt die Schadensart des Nahkampf-Hiebs sowie die spezielle Bonus-Aktion des Reittiers.",
                                                        keyProperties = listOf(
                                                            "Typ" to typeKey,
                                                            "Bonus-Aktion" to when (typeKey) {
                                                                "Fey" -> "Schritt der Fey (60 ft Teleport)"
                                                                "Fiend" -> "Finsterer Blick (WIS-Save oder Verängstigt)"
                                                                else -> "Heilende Berührung (2d8+2 HP Heilung)"
                                                            },
                                                            "Hieb-Schadensart" to when (typeKey) {
                                                                "Fey" -> "Psychisch"
                                                                "Fiend" -> "Nekrotisch"
                                                                else -> "Gleißend (Radiant)"
                                                            }
                                                        )
                                                    )
                                                )
                                            }
                                        ),
                                    color = if (isSelected) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                                    border = BorderStroke(1.dp, if (isSelected) PaladinGold else BorderDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PaladinGoldBright else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. HP & Stat-Block Preview
                    Surface(
                        color = SurfaceCardHighlight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // HP Header & Edit Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Trefferpunkte (HP):", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "$currentHp / $maxHp HP",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = hpBarColor
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showEditHpDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("HP anpassen ✏️", fontSize = 10.sp)
                                }
                            }

                            // HP Progress Bar
                            LinearProgressIndicator(
                                progress = { hpFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = hpBarColor,
                                trackColor = DarkNavyBackground
                            )

                            // Quick Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                HeaderStatItem(label = "Rüstung (RK)", value = "13", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Tempo", value = "60 ft", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Zauber-SG", value = "$spellSaveDc", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Hieb-Treffer", value = "+$spellAttackBonus", modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Steed Duration & Rules Banner
                    SteedDurationBanner()

                    // 3. Combat Actions & Mechanics Section
                    SteedActionsSection(
                        creatureType = creatureType,
                        typeDisplayName = typeDisplayName,
                        damageTypeName = damageTypeName,
                        spellAttackBonus = spellAttackBonus,
                        spellSaveDc = spellSaveDc,
                        isSpecialUsed = isSpecialUsed,
                        onToggleSpecialUsed = onToggleSpecialUsed,
                        onShowDetail = onShowDetail
                    )

                    // 4. Action Buttons (Summon / Dismiss)
                    if (!isSummoned) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSummonFree,
                                enabled = !freeUsageUsed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PaladinGold,
                                    contentColor = DarkNavyBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (!freeUsageUsed) "✨ Gratis rufen (1/Rast)" else "Gratis verbraucht",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onSummonWithSlot(2) },
                                enabled = hasLevel2Slot,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SmiteBlue),
                                border = BorderStroke(1.dp, SmiteBlue.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Slot Stufe 2 rufen",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismissSteed,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthRed),
                            border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reittier entlassen / zurücksenden", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showEditHpDialog) {
        EditSteedHpDialog(
            currentHp = currentHp,
            maxHp = maxHp,
            onDismiss = { showEditHpDialog = false },
            onSave = { newCurrent, newMaxOverride ->
                onUpdateHp(newCurrent, newMaxOverride)
                showEditHpDialog = false
            }
        )
    }
}
