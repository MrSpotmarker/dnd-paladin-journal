package com.paladin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.ActiveBuffInfo
import com.paladin.app.model.Spell
import com.paladin.app.ui.theme.*

@Composable
fun ActiveBuffsBanner(
    activeBuffs: List<ActiveBuffInfo>,
    onDismissBuff: (String) -> Unit
) {
    if (activeBuffs.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚡", fontSize = 14.sp)
                    Text(
                        text = "Aktive Zauber & Effekte",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGoldBright
                    )
                }

                val hasConcentration = activeBuffs.any { it.isConcentration }
                if (hasConcentration) {
                    Surface(
                        color = SmiteBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SmiteBlue)
                    ) {
                        Text(
                            text = "🔮 Konzentration aktiv",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmiteBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderDark)

            // Chips for each active buff
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                activeBuffs.forEach { buff ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardHighlight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(buff.icon, fontSize = 16.sp)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = buff.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (buff.isConcentration) {
                                        Text(
                                            text = "(K)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SmiteBlue
                                        )
                                    }
                                }
                                Text(
                                    text = buff.effectSummary,
                                    fontSize = 11.sp,
                                    color = PaladinGold
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDismissBuff(buff.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effekt beenden",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreparedSpellsSection(
    preparedSpells: List<Spell>,
    maxPrepared: Int,
    hasAvailableSlots: Boolean,
    activeBuffIds: Set<String>,
    chaMod: Int,
    onOpenSelectDialog: () -> Unit,
    onToggleBuff: (buffId: String, isConcentration: Boolean, consumeSlot: Boolean) -> Unit,
    onCastInstant: (spellId: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vorbereitete Zauber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text(
                        text = "${preparedSpells.size} / $maxPrepared vorbereitet",
                        fontSize = 11.sp,
                        color = if (preparedSpells.size > maxPrepared) HealthRed else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                FilledTonalButton(
                    onClick = onOpenSelectDialog,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("✏️ Zauber wählen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = BorderDark)

            if (preparedSpells.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine Zauber vorbereitet.\nTippe auf 'Zauber wählen', um deine Paladin-Zauber auszurüsten.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    preparedSpells.forEach { spell ->
                        PreparedSpellCombatRow(
                            spell = spell,
                            isActiveBuff = spell.id in activeBuffIds,
                            hasAvailableSlots = hasAvailableSlots,
                            chaMod = chaMod,
                            onToggleBuff = { consumeSlot ->
                                onToggleBuff(spell.id, spell.isConcentration, consumeSlot)
                            },
                            onCastInstant = { onCastInstant(spell.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreparedSpellCombatRow(
    spell: Spell,
    isActiveBuff: Boolean,
    hasAvailableSlots: Boolean,
    chaMod: Int,
    onToggleBuff: (consumeSlot: Boolean) -> Unit,
    onCastInstant: () -> Unit
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
                // Left: Spell Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded }
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

            AnimatedVisibility(visible = isExpanded) {
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
                }
            }
        }
    }
}
