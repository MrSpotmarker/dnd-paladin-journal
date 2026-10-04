package com.paladin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.CalculatedStats
import com.paladin.app.model.CharacterSheet
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.*
import com.paladin.app.ui.theme.*

@Composable
fun CombatDashboardScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()

    var showShortRestDialog by remember { mutableStateOf(false) }
    var showLongRestDialog by remember { mutableStateOf(false) }
    var showDmOverrideDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        CharacterHeader(
            character = character,
            stats = stats,
            onOpenOverrides = { showDmOverrideDialog = true }
        )

        // Rest Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showShortRestDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = TextPrimary),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("☕ Kurze Rast", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = { showLongRestDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🏕️ Lange Rast", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Health Card
        HealthCard(
            currentHp = stats.currentHp,
            maxHp = stats.maxHp,
            tempHp = stats.tempHp,
            onTakeDamage = { viewModel.takeDamage(it) },
            onHeal = { viewModel.heal(it) },
            onSetTempHp = { viewModel.setTempHp(it) }
        )

        // Lay on Hands Card
        LayOnHandsCard(
            remaining = stats.remainingLayOnHands,
            maxPool = stats.maxLayOnHands,
            onUse = { viewModel.useLayOnHands(it) },
            onCureCondition = { viewModel.cureConditionLayOnHands() }
        )

        // Channel Divinity (Level 3+)
        if (stats.maxChannelDivinity > 0) {
            ChannelDivinityCard(
                remaining = stats.remainingChannelDivinity,
                maxUses = stats.maxChannelDivinity,
                onUse = { viewModel.useChannelDivinity() },
                onRestore = { viewModel.restoreChannelDivinity() }
            )
        }

        // Spell Slots Tracker
        if (stats.spellSlots.isNotEmpty()) {
            SpellSlotsTracker(
                slots = stats.spellSlots,
                onUseSlot = { viewModel.useSpellSlot(it) },
                onRestoreSlot = { viewModel.restoreSpellSlot(it) }
            )
        }

        // Ability Scores & Saves
        Text(
            text = "Attribute & Rettungswürfe",
            style = MaterialTheme.typography.titleSmall,
            color = PaladinGold,
            fontWeight = FontWeight.Bold
        )
        if (stats.hasAuraOfProtection) {
            Surface(
                color = PaladinGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "✨ Aura of Protection aktiv (+${stats.auraOfProtectionBonus} auf ALLE Rettungswürfe)",
                    color = PaladinGoldBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Ability.entries.forEach { ability ->
                AbilityBox(
                    ability = ability,
                    score = stats.effectiveAbilities.getScore(ability),
                    mod = stats.modifiers[ability] ?: 0,
                    save = stats.savingThrows[ability] ?: 0,
                    isProficientSave = ability in stats.savingThrowProficiencies,
                    hasAura = stats.hasAuraOfProtection,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showShortRestDialog) {
        val conMod = stats.modifiers[Ability.CONSTITUTION] ?: 0
        ShortRestDialog(
            remainingHitDice = stats.remainingHitDice,
            conMod = conMod,
            onDismiss = { showShortRestDialog = false },
            onConfirm = { diceSpent, hpHealed ->
                viewModel.performShortRest(diceSpent, hpHealed)
                showShortRestDialog = false
            }
        )
    }

    if (showLongRestDialog) {
        LongRestDialog(
            level = character.level,
            onDismiss = { showLongRestDialog = false },
            onConfirm = {
                viewModel.performLongRest()
                showLongRestDialog = false
            }
        )
    }

    if (showDmOverrideDialog) {
        DmOverrideDialog(
            currentOverrides = character.dmOverrides,
            onDismiss = { showDmOverrideDialog = false },
            onSave = { overrides ->
                viewModel.updateDmOverrides(overrides)
                showDmOverrideDialog = false
            }
        )
    }
}

@Composable
fun CharacterHeader(
    character: CharacterSheet,
    stats: CalculatedStats,
    onOpenOverrides: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = character.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Stufe ${character.level} Paladin ${character.oath?.let { "• $it" } ?: "(Kein Eid)"}",
                        fontSize = 13.sp,
                        color = PaladinGold
                    )
                }

                IconButton(
                    onClick = onOpenOverrides,
                    modifier = Modifier
                        .background(
                            if (stats.hasDmOverrides) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "DM Overrides",
                        tint = if (stats.hasDmOverrides) PaladinGold else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats Row: AC, PB, Hit Dice
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeaderStatItem(label = "Rüstungsklasse (AC)", value = "${stats.armorClass}")
                HeaderStatItem(label = "Übungsbonus (PB)", value = "+${stats.proficiencyBonus}")
                HeaderStatItem(label = "Trefferwürfel", value = "${stats.remainingHitDice}/${stats.maxHitDice} d10")
            }

            if (stats.hasDmOverrides) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚡ Aktive DM Overrides/Hausregeln",
                    fontSize = 11.sp,
                    color = PaladinGoldBright,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HeaderStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = PaladinGold)
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun ChannelDivinityCard(
    remaining: Int,
    maxUses: Int,
    onUse: () -> Unit,
    onRestore: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Göttliche Macht (Channel Divinity)",
                    fontWeight = FontWeight.Bold,
                    color = SmiteBlue,
                    fontSize = 14.sp
                )
                Text(
                    text = "Regeneriert 1 Ladung bei Kurzer Rast!",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
        }
    }
}
