package com.paladin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.AttackInfo
import com.paladin.app.model.DetailItem
import com.paladin.app.model.SmiteConfig
import com.paladin.app.model.SmiteRegistry
import com.paladin.app.model.Spell
import com.paladin.app.model.SpellSlotState
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.LayOnHandsGreen
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SmiteBlue
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun EquippedWeaponsCard(
    attacks: List<AttackInfo>,
    modifier: Modifier = Modifier,
    spellSlots: List<SpellSlotState> = emptyList(),
    preparedSpells: List<Spell> = emptyList(),
    attacksPerAction: Int = 1,
    onUseSlot: (Int) -> Unit = {},
    onTriggerSmite: (SmiteConfig, Int) -> Unit = { _, slot -> onUseSlot(slot) },
    onShowDetail: (DetailItem) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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

                if (attacksPerAction >= 2) {
                    Surface(
                        color = PaladinGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "⚔️ 2 Angriffe (Extra Attack)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGoldBright,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
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
                            spellSlots = spellSlots,
                            preparedSpells = preparedSpells,
                            onUseSlot = onUseSlot,
                            onTriggerSmite = onTriggerSmite,
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
    spellSlots: List<com.paladin.app.model.SpellSlotState> = emptyList(),
    preparedSpells: List<Spell> = emptyList(),
    onUseSlot: (Int) -> Unit = {},
    onTriggerSmite: (SmiteConfig, Int) -> Unit = { _, slot -> onUseSlot(slot) },
    onShowDetail: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSmitePanel by remember { mutableStateOf(false) }
    var selectedSmiteSlotLevel by remember { mutableIntStateOf(1) }
    var smiteExecutedMessage by remember { mutableStateOf<String?>(null) }

    val availableSmites = remember(preparedSpells) {
        SmiteRegistry.resolveAvailableSmites(preparedSpells)
    }
    var selectedSmiteId by remember(availableSmites) {
        mutableStateOf(availableSmites.firstOrNull()?.id ?: SmiteRegistry.PALADINS_SMITE.id)
    }
    val selectedSmite = availableSmites.find { it.id == selectedSmiteId } ?: availableSmites.firstOrNull() ?: SmiteRegistry.PALADINS_SMITE

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

                    // ── ⚡ Schnell-Smite Rechner (D&D 2024 Regel) ──
                    val availableSlots = spellSlots.filter { it.remainingSlots > 0 }
                    Surface(
                        color = SmiteBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SmiteBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(selectedSmite.icon, fontSize = 13.sp)
                                    Text(
                                        if (availableSmites.size > 1) "Schnell-Smite (${selectedSmite.name})" else selectedSmite.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaladinGoldBright
                                    )
                                }
                                TextButton(
                                    onClick = { showSmitePanel = !showSmitePanel },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text(if (showSmitePanel) "Schließen ▲" else "Smite berechnen ▼", fontSize = 11.sp, color = PaladinGold)
                                }
                            }

                            if (showSmitePanel) {
                                // 1. Smite-Auswahl (wenn mehr als 1 Smite verfügbar ist)
                                if (availableSmites.size > 1) {
                                    Text(
                                        text = "Wähle vorbereiteten Smite:",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.horizontalScroll(rememberScrollState())
                                    ) {
                                        availableSmites.forEach { smite ->
                                            FilterChip(
                                                selected = smite.id == selectedSmite.id,
                                                onClick = {
                                                    selectedSmiteId = smite.id
                                                    smiteExecutedMessage = null
                                                },
                                                label = { Text("${smite.icon} ${smite.name}", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = PaladinGold,
                                                    selectedLabelColor = DarkNavyBackground
                                                )
                                            )
                                        }
                                    }
                                }

                                val eligibleSlots = spellSlots.filter { it.remainingSlots > 0 && it.level >= selectedSmite.baseLevel }
                                if (eligibleSlots.isEmpty()) {
                                    val anySlots = spellSlots.any { it.remainingSlots > 0 }
                                    Text(
                                        text = if (anySlots) {
                                            "Kein Zauberplatz ab Grad ${selectedSmite.baseLevel} für ${selectedSmite.name} verfügbar!"
                                        } else {
                                            "Keine Zauberplätze mehr verfügbar!"
                                        },
                                        fontSize = 11.sp,
                                        color = HealthRed
                                    )
                                } else {
                                    val currentSlot = eligibleSlots.find { it.level == selectedSmiteSlotLevel } ?: eligibleSlots.first()
                                    val smiteBonus = selectedSmite.damageBonusString(currentSlot.level)
                                    val totalDamageFormula = "${attack.damageString} + $smiteBonus"

                                    Text(
                                        text = "Wähle Zauberplatz-Grad:",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        eligibleSlots.forEach { slot ->
                                            FilterChip(
                                                selected = slot.level == currentSlot.level,
                                                onClick = { selectedSmiteSlotLevel = slot.level },
                                                label = { Text("Grad ${slot.level} (${slot.remainingSlots}x)", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = PaladinGold,
                                                    selectedLabelColor = DarkNavyBackground
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        color = SurfaceCard,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                            Text(
                                                text = "🔥 Gesamtschaden: $totalDamageFormula",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PaladinGoldBright
                                            )
                                            Text(
                                                text = "Bonus-Aktion bei Treffer • Grad ${currentSlot.level} Slot" +
                                                    if (selectedSmite.isConcentration) " • Konzentration" else "",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                            if (selectedSmite.riderEffect.isNotBlank()) {
                                                Text(
                                                    text = "• ${selectedSmite.riderEffect}",
                                                    fontSize = 10.sp,
                                                    color = PaladinGold.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onTriggerSmite(selectedSmite, currentSlot.level)
                                            smiteExecutedMessage = "Grad ${currentSlot.level} ${selectedSmite.name} gezündet (+$smiteBonus)!"
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PaladinGold,
                                            contentColor = DarkNavyBackground
                                        ),
                                        modifier = Modifier.fillMaxWidth().height(32.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("⚡ Slot verbrauchen & ${selectedSmite.name} zünden", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (smiteExecutedMessage != null) {
                                        Text(
                                            text = smiteExecutedMessage!!,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LayOnHandsGreen
                                        )
                                    }
                                }
                            }
                        }
                    }

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
