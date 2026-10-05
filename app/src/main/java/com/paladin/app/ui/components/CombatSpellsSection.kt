package com.paladin.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Spell
import com.paladin.app.model.SpellSlotState
import com.paladin.app.ui.theme.*

@Composable
fun SpellsAndSlotsSection(
    spellSlots: List<SpellSlotState>,
    preparedSpells: List<Spell>,
    maxPrepared: Int,
    activeBuffIds: Set<String>,
    chaMod: Int,
    onUseSlot: (Int) -> Unit,
    onRestoreSlot: (Int) -> Unit,
    onOpenSelectDialog: () -> Unit,
    onToggleBuff: (buffId: String, isConcentration: Boolean, consumeSlot: Boolean) -> Unit,
    onCastInstant: (spellId: String) -> Unit,
    onShowDetail: (DetailItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hasAvailableSlots = spellSlots.any { it.remainingSlots > 0 }
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
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
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Text("✨", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "Zauber & Zauberplätze",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        val totalSlots = spellSlots.sumOf { it.maxSlots }
                        val remainingSlots = spellSlots.sumOf { it.remainingSlots }
                        Text(
                            text = "${preparedSpells.size}/$maxPrepared vorbereitet • $remainingSlots/$totalSlots Slots frei",
                            fontSize = 11.sp,
                            color = if (preparedSpells.size > maxPrepared) HealthRed else TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(
                        onClick = onOpenSelectDialog,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("✏️ Zauber", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Zauber einklappen" else "Zauber ausklappen",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Compact Spell Slot Tracker
            if (spellSlots.isNotEmpty()) {
                Surface(
                    color = SurfaceCardHighlight,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        spellSlots.forEach { slot ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Grad ${slot.level} Slots:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SpellSlotPurple
                                    )
                                    Text(
                                        text = "(${slot.remainingSlots}/${slot.maxSlots} frei)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    (0 until slot.maxSlots).forEach { index ->
                                        val isUsed = index < slot.usedSlots
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(if (isUsed) SurfaceCard else SpellSlotPurple)
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
                                                        .size(9.dp)
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

            // Expandable List of Prepared Spells
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = BorderDark)

                    if (preparedSpells.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Keine Zauber vorbereitet.\nTippe auf '✏️ Zauber', um Paladin-Zauber auszurüsten.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        preparedSpells.forEach { spell ->
                            PreparedSpellCombatRow(
                                spell = spell,
                                isActiveBuff = spell.id in activeBuffIds,
                                hasAvailableSlots = hasAvailableSlots,
                                chaMod = chaMod,
                                onToggleBuff = { consumeSlot ->
                                    onToggleBuff(spell.id, spell.isConcentration, consumeSlot)
                                },
                                onCastInstant = { onCastInstant(spell.id) },
                                onShowDetail = { onShowDetail(DetailItem.SpellInfo(spell)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreparedSpellCombatRow(
    spell: Spell,
    isActiveBuff: Boolean,
    hasAvailableSlots: Boolean,
    chaMod: Int,
    onToggleBuff: (consumeSlot: Boolean) -> Unit,
    onCastInstant: () -> Unit,
    onShowDetail: () -> Unit = {}
) {
    val isBuffOrConcentration = spell.isConcentration || spell.id in setOf(
        "srd_shield_of_faith", "srd_bless", "srd_divine_favor",
        "srd_heroism", "srd_compelled_duel", "srd_searing_smite",
        "srd_wrathful_smite", "srd_thunderous_smite", "srd_detect_magic",
        "srd_protection_from_evil_and_good"
    )

    var isExpanded by remember { mutableStateOf(false) }

    val effectQuickText = when (spell.id) {
        "srd_shield_of_faith" -> "+2 Rüstungsklasse (AC)"
        "srd_bless" -> "+1d4 auf Angriffe & Rettungswürfe"
        "srd_divine_favor" -> "+1d4 Gleißender Schaden auf Waffen"
        "srd_heroism" -> "Immunität Furcht, +${maxOf(1, chaMod)} Temp-HP/Runde"
        "srd_compelled_duel" -> "Duell: Ziel an dich gebunden"
        "srd_cure_wounds" -> "Heilt 2d8 + $chaMod Trefferpunkte"
        "srd_paladins_smite" -> "+2d8 Gleißender Schaden bei Treffer"
        "srd_searing_smite" -> "+1d6 Feuerschaden & Brand"
        "srd_wrathful_smite" -> "+1d6 Psychisch & Verängstigt"
        "srd_command" -> "1-Wort-Befehl (WIS-Save DC)"
        "srd_protection_from_evil_and_good" -> "Schutz: Unholde/Untote haben Nachteil"
        "srd_detect_magic" -> "Spürt magische Auren (30ft)"
        else -> spell.description.take(45) + "..."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActiveBuff) PaladinGold.copy(alpha = 0.12f) else SurfaceCardHighlight
        ),
        border = if (isActiveBuff) {
            androidx.compose.foundation.BorderStroke(1.dp, PaladinGold)
        } else null,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Spell Details (Click = Expand/Collapse, Long Click = Full Details)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .combinedClickable(
                            onClick = { isExpanded = !isExpanded },
                            onLongClick = onShowDetail
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = spell.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActiveBuff) PaladinGoldBright else TextPrimary
                        )

                        if (spell.isConcentration) {
                            Surface(
                                color = SmiteBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "K",
                                    fontSize = 9.sp,
                                    color = SmiteBlue,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = effectQuickText,
                        fontSize = 11.sp,
                        color = if (isActiveBuff) PaladinGold else TextSecondary,
                        fontWeight = if (isActiveBuff) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                // Right: Action Button
                if (isBuffOrConcentration) {
                    if (isActiveBuff) {
                        Button(
                            onClick = { onToggleBuff(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PaladinGold,
                                contentColor = DarkNavyBackground
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aktiv ✕", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { onToggleBuff(hasAvailableSlots) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SurfaceCard,
                                contentColor = PaladinGold
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aktivieren", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Button(
                        onClick = onCastInstant,
                        enabled = hasAvailableSlots,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SmiteBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Wirken", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = BorderDark)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${spell.level}. Grad • ${spell.school.displayName} • ${spell.castingTime} • Reichweite ${spell.range}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = spell.description,
                        fontSize = 11.sp,
                        color = TextPrimary.copy(alpha = 0.9f)
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
                            text = "📖 Details & Regeln (oder lange drücken) ➔",
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
