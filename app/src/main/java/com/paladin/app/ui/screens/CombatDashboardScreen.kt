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
import com.paladin.app.model.Skill
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
    val srdSpells by viewModel.srdSpells.collectAsState()

    var showShortRestDialog by remember { mutableStateOf(false) }
    var showLongRestDialog by remember { mutableStateOf(false) }
    var showDmOverrideDialog by remember { mutableStateOf(false) }
    var showFeatsDialog by remember { mutableStateOf(false) }
    var isSkillsExpanded by remember { mutableStateOf(false) }
    var showEditAbilitiesDialog by remember { mutableStateOf(false) }
    var showPreparedSpellsDialog by remember { mutableStateOf(false) }

    val allSpells = remember(srdSpells, character.customSpells) {
        srdSpells + character.customSpells
    }
    val preparedSpells = remember(allSpells, character.preparedSpellIds) {
        allSpells.filter { it.id in character.preparedSpellIds }
    }
    val chaMod = stats.modifiers[Ability.CHARISMA] ?: 0

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
            onOpenOverrides = { showDmOverrideDialog = true },
            onOpenFeats = { showFeatsDialog = true }
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

        // Active Buffs Banner (Shield of Faith, Bless, Sacred Weapon, etc.)
        if (stats.activeBuffs.isNotEmpty()) {
            ActiveBuffsBanner(
                activeBuffs = stats.activeBuffs,
                onDismissBuff = { viewModel.deactivateBuff(it) }
            )
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
            val canRegainSpellSlot = stats.spellSlots.any { it.usedSlots > 0 }
            ChannelDivinityCard(
                remaining = stats.remainingChannelDivinity,
                maxUses = stats.maxChannelDivinity,
                oath = character.oath,
                chaMod = chaMod,
                activeBuffIds = character.activeBuffIds,
                canRegainSpellSlot = canRegainSpellSlot,
                onUse = { viewModel.useChannelDivinity() },
                onRestore = { viewModel.restoreChannelDivinity() },
                onToggleBuff = { buffId, consumesCharge ->
                    viewModel.toggleBuff(
                        buffId = buffId,
                        isConcentration = false,
                        consumesChannelDivinity = consumesCharge
                    )
                },
                onHarnessDivinePower = {
                    viewModel.harnessDivinePower(1)
                }
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

        // Prepared Spells & Quick Cast / Buff Toggle Section
        val hasAvailableSlots = stats.spellSlots.any { it.remainingSlots > 0 }
        PreparedSpellsSection(
            preparedSpells = preparedSpells,
            maxPrepared = stats.maxPreparedSpells,
            hasAvailableSlots = hasAvailableSlots,
            activeBuffIds = character.activeBuffIds,
            chaMod = chaMod,
            onOpenSelectDialog = { showPreparedSpellsDialog = true },
            onToggleBuff = { buffId, isConcentration, consumeSlot ->
                val slotToConsume = if (consumeSlot) 1 else null
                viewModel.toggleBuff(buffId, isConcentration, slotToConsume)
            },
            onCastInstant = { spellId ->
                viewModel.castInstantSpell(spellId, 1)
            }
        )

        // Ability Scores & Saves Header with Edit & Expand buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Attribute & Rettungswürfe",
                style = MaterialTheme.typography.titleSmall,
                color = PaladinGold,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // Edit Base Scores Button
                OutlinedButton(
                    onClick = { showEditAbilitiesDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("✏️ Basiswerte", fontSize = 11.sp)
                }

                // Expand/Collapse Skills Button
                FilledTonalButton(
                    onClick = { isSkillsExpanded = !isSkillsExpanded },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (isSkillsExpanded) "▲ Skills" else "▼ Skills (${character.proficientSkills.size}⭐)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

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

        val hasBless = character.activeBuffIds.any { it.equals("srd_bless", ignoreCase = true) || it.equals("bless", ignoreCase = true) }
        if (hasBless) {
            Surface(
                color = PaladinGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "✨ Segen (Bless) aktiv (+1d4 auf ALLE Rettungswürfe & Angriffe)",
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

        // Expanded Skills & Checks breakdown
        if (isSkillsExpanded) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎲 Fertigkeiten & Skill Checks",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        val passivePerception = 10 + (stats.skillModifiers[Skill.PERCEPTION] ?: 0)
                        Text(
                            text = "Passive Wahrnehmung: $passivePerception",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    HorizontalDivider(color = BorderDark)

                    val allSkills = Skill.entries
                    val firstHalf = allSkills.take(9)
                    val secondHalf = allSkills.drop(9)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left column
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            firstHalf.forEach { skill ->
                                SkillCheckRow(
                                    skill = skill,
                                    modifier = stats.skillModifiers[skill] ?: 0,
                                    isProficient = skill in character.proficientSkills
                                )
                            }
                        }

                        // Right column
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            secondHalf.forEach { skill ->
                                SkillCheckRow(
                                    skill = skill,
                                    modifier = stats.skillModifiers[skill] ?: 0,
                                    isProficient = skill in character.proficientSkills
                                )
                            }
                        }
                    }
                }
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

    if (showFeatsDialog) {
        FeatsDialog(
            characterLevel = character.level,
            currentFeats = character.feats,
            onDismiss = { showFeatsDialog = false },
            onSave = { updatedFeats ->
                viewModel.updateFeats(updatedFeats)
                showFeatsDialog = false
            }
        )
    }

    if (showEditAbilitiesDialog) {
        EditBaseAbilitiesDialog(
            currentScores = character.baseAbilityScores,
            onDismiss = { showEditAbilitiesDialog = false },
            onSave = { updatedScores ->
                viewModel.updateBaseAbilityScores(updatedScores)
                showEditAbilitiesDialog = false
            }
        )
    }

    if (showPreparedSpellsDialog) {
        PreparedSpellsDialog(
            allSpells = allSpells,
            currentPreparedIds = character.preparedSpellIds,
            maxPreparedAllowed = stats.maxPreparedSpells,
            characterLevel = character.level,
            onDismiss = { showPreparedSpellsDialog = false },
            onSave = { updatedPreparedIds ->
                viewModel.setPreparedSpells(updatedPreparedIds)
                showPreparedSpellsDialog = false
            }
        )
    }
}

@Composable
fun CharacterHeader(
    character: CharacterSheet,
    stats: CalculatedStats,
    onOpenOverrides: () -> Unit,
    onOpenFeats: () -> Unit
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
                    val subtitleParts = buildList {
                        add("Stufe ${character.level} Paladin")
                        character.oath?.let { add(it) }
                    }
                    Text(
                        text = subtitleParts.joinToString(" • "),
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

            Spacer(modifier = Modifier.height(10.dp))

            // Subtle Compact Feats Bar
            val featLimit = com.paladin.app.model.FeatCatalog.getStandardFeatLimit(character.level)
            val isOver = character.feats.size > featLimit
            val featsText = if (character.feats.isEmpty()) {
                "Keine Talente aktiv • Tippe zum Auswählen"
            } else {
                character.feats.joinToString(" • ") { it.substringBefore(" (") }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceCardHighlight.copy(alpha = 0.5f))
                    .clickable { onOpenFeats() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("✨", fontSize = 11.sp)
                    Text(
                        text = featsText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PaladinGold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = "${character.feats.size}/$featLimit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOver) HealthRed else ProficiencyGreen
                    )
                    Text("✏️", fontSize = 10.sp)
                }
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
    oath: String?,
    chaMod: Int,
    activeBuffIds: Set<String>,
    canRegainSpellSlot: Boolean,
    onUse: () -> Unit,
    onRestore: () -> Unit,
    onToggleBuff: (buffId: String, consumesCharge: Boolean) -> Unit,
    onHarnessDivinePower: () -> Unit
) {
    val isSacredWeaponActive = activeBuffIds.any { it.equals("sacred_weapon", ignoreCase = true) }
    val isVowOfEnmityActive = activeBuffIds.any { it.equals("vow_of_enmity", ignoreCase = true) }

    val hasDevotion = oath == null || oath.contains("Devotion", ignoreCase = true)
    val hasVengeance = oath == null || oath.contains("Vengeance", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            HorizontalDivider(color = BorderDark)

            // Interactive powers
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sacred Weapon (Devotion)
                if (hasDevotion) {
                    val sacredBonus = maxOf(1, chaMod)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSacredWeaponActive) PaladinGold.copy(alpha = 0.15f) else SurfaceCardHighlight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🌟 Heilige Waffe (Sacred Weapon)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSacredWeaponActive) PaladinGoldBright else TextPrimary
                            )
                            Text(
                                text = "+$sacredBonus auf Waffen-Angriffe für 10 Min.",
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
                                    contentColor = androidx.compose.ui.graphics.Color.White
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎯 Gelübde der Feindschaft (Vow of Enmity)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVowOfEnmityActive) PaladinGoldBright else TextPrimary
                            )
                            Text(
                                text = "Vorteil auf Angriffe gegen Ziel (1 Min.)",
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
                                    contentColor = androidx.compose.ui.graphics.Color.White
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ Göttliche Kraft bündeln",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Regeneriert 1 verbrauchten Zauberslot",
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
            }
        }
    }
}

@Composable
fun SkillCheckRow(
    skill: Skill,
    modifier: Int,
    isProficient: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isProficient) "⭐" else "○",
                fontSize = 10.sp,
                color = if (isProficient) PaladinGold else TextSecondary.copy(alpha = 0.5f)
            )
            Text(
                text = "${skill.displayName} (${skill.ability.abbreviation})",
                fontSize = 11.sp,
                fontWeight = if (isProficient) FontWeight.Bold else FontWeight.Normal,
                color = if (isProficient) TextPrimary else TextSecondary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        val modColor = when {
            modifier > 0 -> if (isProficient) PaladinGold else ProficiencyGreen
            modifier < 0 -> HealthRed
            else -> TextSecondary
        }

        Text(
            text = Ability.formatModifier(modifier),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = modColor,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
