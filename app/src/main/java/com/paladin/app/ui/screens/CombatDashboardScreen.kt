package com.paladin.app.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.data.online.OnlineSearchCategory
import com.paladin.app.model.Ability
import com.paladin.app.model.CalculatedStats
import com.paladin.app.model.ChannelDivinityDetails
import com.paladin.app.model.CharacterSheet
import com.paladin.app.model.DetailItem
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
    var showFeatsOnlineSearch by remember { mutableStateOf(false) }
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
            onOpenFeats = { showFeatsDialog = true },
            onModifyInspiration = { delta -> viewModel.modifyHeroicInspiration(delta) },
            onShowInspirationDetail = {
                viewModel.showDetail(
                    DetailItem.FeatureInfo(
                        title = "Heroische Inspiration",
                        subtitle = "D&D 2024 Regeln (PHB)",
                        badge = "Kernmechanik",
                        icon = "🎲",
                        description = "Wenn du Heroische Inspiration besitzt, kannst du sie ausgeben, um einen beliebigen W20-Wurf (Angriffswurf, Rettungswurf oder Attributswurf) zu wiederholen. Du musst das neue Würfelergebnis verwenden.\n\nDu kannst eine heroische Inspiration auch an einen verbündeten Charakter weitergeben.",
                        keyProperties = listOf(
                            "Aktueller Vorrat" to "${character.heroicInspirations}",
                            "Spezies" to character.species.displayName,
                            "Rast-Regeneration" to "${character.species.defaultInspirationsOnLongRest} (Mensch: Einfallsreich)"
                        ),
                        mechanicalBenefits = listOf(
                            "W20-Neu-Würfeln bei Fehlschlägen oder riskanten Situationen",
                            "Wird bei Rast automatisch auf den Spezies-Standard zurückgesetzt"
                        )
                    )
                )
            },
            onShowSpeciesDetail = {
                viewModel.showDetail(
                    DetailItem.FeatureInfo(
                        title = "Spezies: ${character.species.displayName}",
                        subtitle = "D&D 2024 Spezies",
                        badge = "${character.species.baseSpeedFt} ft Bewegung",
                        icon = "👤",
                        description = character.species.traitsDescription,
                        keyProperties = listOf(
                            "Basis-Bewegung" to "${character.species.baseSpeedFt} ft",
                            "Inspiration bei Rast" to "${character.species.defaultInspirationsOnLongRest}"
                        )
                    )
                )
            }
        )

        // Active Buffs Banner (Shield of Faith, Bless, Sacred Weapon, etc.)
        if (stats.activeBuffs.isNotEmpty()) {
            ActiveBuffsBanner(
                activeBuffs = stats.activeBuffs,
                onDismissBuff = { viewModel.deactivateBuff(it) },
                onShowDetail = { buff ->
                    val spell = allSpells.find { it.id.equals(buff.id, ignoreCase = true) }
                    if (spell != null) {
                        viewModel.showDetail(DetailItem.SpellInfo(spell))
                    } else {
                        when (buff.id) {
                            "sacred_weapon" -> viewModel.showDetail(ChannelDivinityDetails.getSacredWeaponDetail(chaMod))
                            "vow_of_enmity" -> viewModel.showDetail(ChannelDivinityDetails.getVowOfEnmityDetail())
                            "divine_sense" -> viewModel.showDetail(ChannelDivinityDetails.getDivineSenseDetail())
                            else -> {
                                viewModel.showDetail(
                                    DetailItem.FeatureInfo(
                                        title = buff.name,
                                        subtitle = "Aktiver Effekt",
                                        icon = buff.icon,
                                        description = buff.effectSummary,
                                        mechanicalBenefits = listOf(buff.effectSummary)
                                    )
                                )
                            }
                        }
                    }
                }
            )
        }

        // Status-Zustände (Conditions Tracker - D&D 2024)
        ConditionsSection(
            activeConditions = character.activeConditions,
            onToggleCondition = { viewModel.toggleCondition(it) },
            onRemoveCondition = { viewModel.removeCondition(it) },
            onCurePoisonLayOnHands = { viewModel.cureConditionLayOnHands(com.paladin.app.model.Condition.POISONED) },
            remainingLayOnHands = stats.remainingLayOnHands
        )

        // Health Card
        HealthCard(
            currentHp = stats.currentHp,
            maxHp = stats.maxHp,
            tempHp = stats.tempHp,
            onTakeDamage = { viewModel.takeDamage(it) },
            onHeal = { viewModel.heal(it) },
            onSetTempHp = { viewModel.setTempHp(it) }
        )

        // Equipped Weapons & Attacks Card mit Smite-Rechner
        EquippedWeaponsCard(
            attacks = stats.attacks,
            spellSlots = stats.spellSlots,
            onUseSlot = { viewModel.useSpellSlot(it) },
            onShowDetail = { viewModel.showDetail(it) }
        )

        // Unified Spells & Spell Slots Section
        SpellsAndSlotsSection(
            spellSlots = stats.spellSlots,
            preparedSpells = preparedSpells,
            maxPrepared = stats.maxPreparedSpells,
            activeBuffIds = character.activeBuffIds,
            chaMod = chaMod,
            onUseSlot = { viewModel.useSpellSlot(it) },
            onRestoreSlot = { viewModel.restoreSpellSlot(it) },
            onOpenSelectDialog = { showPreparedSpellsDialog = true },
            onToggleBuff = { buffId, isConcentration, consumeSlot ->
                val slotToConsume = if (consumeSlot) 1 else null
                viewModel.toggleBuff(buffId, isConcentration, slotToConsume)
            },
            onCastInstant = { spellId ->
                viewModel.castInstantSpell(spellId, 1)
            },
            onShowDetail = { viewModel.showDetail(it) }
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
                },
                onShowDetail = { viewModel.showDetail(it) }
            )
        }

        // Lay on Hands Card
        LayOnHandsCard(
            remaining = stats.remainingLayOnHands,
            maxPool = stats.maxLayOnHands,
            onUse = { viewModel.useLayOnHands(it) },
            onCureCondition = { viewModel.cureConditionLayOnHands() },
            onShowDetail = {
                viewModel.showDetail(
                    DetailItem.FeatureInfo(
                        title = "Handauflegen (Lay on Hands)",
                        subtitle = "Klassenmerkmal Paladin (Stufe 1)",
                        badge = "Bonus-Aktion",
                        icon = "💚",
                        keyProperties = listOf(
                            "Aktionstyp" to "Bonus-Aktion (D&D 2024)",
                            "Reichweite" to "Berührung",
                            "Pool-Größe" to "${stats.maxLayOnHands} HP (5 x Stufe ${character.level})",
                            "Erholung" to "Lange Rast"
                        ),
                        mechanicalBenefits = listOf(
                            "Heilung: Beliebige Anzahl Trefferpunkte aus deinem Pool (aktuell noch ${stats.remainingLayOnHands} HP) auf eine berührte Kreatur übertragen.",
                            "Vergiftung heilen: 5 Trefferpunkte aus dem Pool aufwenden, um den Zustand 'Vergiftet' (Poisoned) bei einer Kreatur zu heilen."
                        ),
                        description = "Deine gesegnete Berührung lindert Wunden und vertreibt Gifte. In den D&D 2024 Regeln wird Handauflegen als Bonus-Aktion ausgeführt, sodass du im selben Zug noch mit deiner Waffe angreifen oder einen Zauber wirken kannst!"
                    )
                )
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
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "✨ Schutz-Aura (Aura of Protection) aktiv • Reichweite: ${stats.auraRangeFt} ft.",
                        color = PaladinGoldBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+${stats.auraOfProtectionBonus} auf ALLE Rettungswürfe (für dich & Verbündete in ${stats.auraRangeFt} ft.)",
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (stats.hasAuraOfCourage) {
            Surface(
                color = ChaunteaGreen.copy(alpha = 0.20f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChaunteaGreenBright.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "🦁 Aura des Mutes (Aura of Courage) aktiv • ${stats.auraRangeFt} ft.",
                        color = ChaunteaGreenBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Du und Verbündete in ${stats.auraRangeFt} ft. können nicht verängstigt (Frightened) werden!",
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
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
                val score = stats.effectiveAbilities.getScore(ability)
                val mod = stats.modifiers[ability] ?: 0
                val save = stats.savingThrows[ability] ?: 0
                val isProf = ability in stats.savingThrowProficiencies
                val showDetailAction = {
                    viewModel.showDetail(
                        DetailItem.AbilityInfo(
                            ability = ability,
                            score = score,
                            modifier = mod,
                            isSaveProficient = isProf,
                            saveBonus = save
                        )
                    )
                }
                AbilityBox(
                    ability = ability,
                    score = score,
                    mod = mod,
                    save = save,
                    isProficientSave = isProf,
                    hasAura = stats.hasAuraOfProtection,
                    onClick = showDetailAction,
                    onLongClick = showDetailAction,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Expanded Skills & Checks breakdown
        SkillsSection(
            isExpanded = isSkillsExpanded,
            skillModifiers = stats.skillModifiers,
            proficientSkills = character.proficientSkills
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Rest Action Buttons (at bottom of screen)
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
            },
            onShowDetail = { viewModel.showDetail(it) },
            onOpenOnlineSearch = { showFeatsOnlineSearch = true }
        )
    }

    if (showFeatsOnlineSearch) {
        OnlineSearchDialog(
            viewModel = viewModel,
            initialCategory = OnlineSearchCategory.FEATS,
            onDismiss = { showFeatsOnlineSearch = false }
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
            },
            onShowDetail = { viewModel.showDetail(it) }
        )
    }
}
