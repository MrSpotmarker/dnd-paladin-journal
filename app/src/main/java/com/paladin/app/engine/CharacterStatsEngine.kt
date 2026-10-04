package com.paladin.app.engine

import com.paladin.app.model.*
import kotlin.math.max
import kotlin.math.min

object CharacterStatsEngine {

    fun calculate(character: CharacterSheet): CalculatedStats {
        val level = character.level.coerceIn(1, 20)
        val pb = calculateProficiencyBonus(level)

        // 1. Attributswerte berechnen (Basis -> Items -> DM Overrides)
        val effectiveAbilities = calculateEffectiveAbilities(character)
        val modifiers = Ability.entries.associateWith { ability ->
            Ability.calculateModifier(effectiveAbilities.getScore(ability))
        }

        val strMod = modifiers[Ability.STRENGTH] ?: 0
        val dexMod = modifiers[Ability.DEXTERITY] ?: 0
        val conMod = modifiers[Ability.CONSTITUTION] ?: 0
        val intMod = modifiers[Ability.INTELLIGENCE] ?: 0
        val wisMod = modifiers[Ability.WISDOM] ?: 0
        val chaMod = modifiers[Ability.CHARISMA] ?: 0

        // 2. Max HP
        val baseLvl1Hp = 10 + conMod
        val higherLvlHp = if (level > 1) (level - 1) * (6 + conMod) else 0
        val toughBonus = if (character.feats.any { it.contains("Tough", ignoreCase = true) }) level * 2 else 0
        val maxHp = max(1, baseLvl1Hp + higherLvlHp + toughBonus + character.maxHpManualAdjustment + character.dmOverrides.hpMaxBonus)

        // 3. Rüstungsklasse (AC)
        val (ac, acBreakdown) = calculateArmorClass(character, dexMod)

        // 4. Rettungswürfe & Aura of Protection (Level 6+)
        val savingThrowProficiencies = setOf(Ability.WISDOM, Ability.CHARISMA)
        val hasAura = level >= 6
        val auraBonus = if (hasAura) max(1, chaMod) else 0
        val savingThrows = Ability.entries.associateWith { ability ->
            val mod = modifiers[ability] ?: 0
            val profBonus = if (ability in savingThrowProficiencies) pb else 0
            mod + profBonus + auraBonus + character.dmOverrides.savingThrowBonus
        }

        // 5. Fertigkeiten (Skills)
        val skillModifiers = Skill.entries.associateWith { skill ->
            val mod = modifiers[skill.ability] ?: 0
            val profBonus = if (skill in character.proficientSkills) pb else 0
            mod + profBonus
        }

        // 6. Zaubersprüche & Slots (2024 Regeln)
        val spellSaveDc = 8 + pb + chaMod + character.dmOverrides.spellDcBonus
        val spellAttackBonus = pb + chaMod
        val maxPreparedSpells = getPreparedSpellsForLevel(level)
        val spellSlots = getSpellSlotsForLevel(level, character.spellSlotUsages)

        // 7. Ressourcen: Lay on Hands & Channel Divinity
        val maxLayOnHands = level * 5
        val remainingLayOnHands = max(0, maxLayOnHands - character.layOnHandsUsed)

        val maxChannelDivinity = when {
            level >= 11 -> 3
            level >= 3 -> 2
            else -> 0
        }
        val remainingChannelDivinity = max(0, maxChannelDivinity - character.channelDivinityUsed)

        // 8. Hit Dice
        val maxHitDice = level
        val remainingHitDice = max(0, maxHitDice - character.hitDiceUsed)

        // 9. Inventar, Gewicht & Angriffe
        val totalWeight = character.inventory.sumOf { it.weightLbs * it.quantity }
        val carryCapacity = (effectiveAbilities.strength * 15).toDouble()

        val attacks = character.inventory
            .filter { it.type == ItemType.WEAPON && it.isEquipped }
            .map { weapon ->
                calculateAttack(
                    weapon = weapon,
                    pb = pb,
                    strMod = strMod,
                    dexMod = dexMod,
                    chaMod = chaMod,
                    masteredWeapons = character.masteredWeaponNames,
                    fightingStyle = character.fightingStyle,
                    feats = character.feats,
                    activeBuffIds = character.activeBuffIds
                )
            }

        val hasAlert = character.feats.any { it.contains("Alert", ignoreCase = true) }
        val alertBonus = if (hasAlert) pb else 0
        val initiative = dexMod + alertBonus + character.dmOverrides.initiativeBonus

        val activeBuffList = mutableListOf<ActiveBuffInfo>()
        if (character.activeBuffIds.any { it.equals("srd_shield_of_faith", ignoreCase = true) || it.equals("shield_of_faith", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_shield_of_faith", "Shield of Faith (Glaubensschild)", "🛡️", "+2 Rüstungsklasse (AC)", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_bless", ignoreCase = true) || it.equals("bless", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_bless", "Bless (Segen)", "✨", "+1d4 auf Angriffe & Rettungswürfe", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_divine_favor", ignoreCase = true) || it.equals("divine_favor", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_divine_favor", "Divine Favor (Göttliche Gunst)", "⚔️", "+1d4 Gleißender Schaden", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_heroism", ignoreCase = true) || it.equals("heroism", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_heroism", "Heroism (Heldenmut)", "🦁", "Immunität Furcht, +${max(1, chaMod)} Temp-HP/Runde", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_compelled_duel", ignoreCase = true) || it.equals("compelled_duel", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_compelled_duel", "Compelled Duel (Erzwungenes Duell)", "🤺", "Ziel an dich gebunden", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_protection_from_evil_and_good", ignoreCase = true) || it.equals("protection_from_evil_and_good", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_protection_from_evil_and_good", "Protection from Evil & Good", "🛡️", "Unholde/Untote haben Nachteil", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_searing_smite", ignoreCase = true) || it.equals("searing_smite", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_searing_smite", "Searing Smite", "🔥", "+1d6 Feuerschaden & Brand", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_wrathful_smite", ignoreCase = true) || it.equals("wrathful_smite", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_wrathful_smite", "Wrathful Smite", "👻", "+1d6 Psychisch & Furcht", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("srd_detect_magic", ignoreCase = true) || it.equals("detect_magic", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("srd_detect_magic", "Detect Magic (Magie entdecken)", "🔮", "Auren innerhalb 30ft spürbar", isConcentration = true))
        }
        if (character.activeBuffIds.any { it.equals("sacred_weapon", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("sacred_weapon", "Heilige Waffe (Sacred Weapon)", "🌟", "+${max(1, chaMod)} Waffen-Angriffsbonus, 20ft Licht", isConcentration = false))
        }
        if (character.activeBuffIds.any { it.equals("vow_of_enmity", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("vow_of_enmity", "Gelübde der Feindschaft (Vow of Enmity)", "🎯", "Vorteil auf Angriffe", isConcentration = false))
        }
        if (character.activeBuffIds.any { it.equals("divine_sense", ignoreCase = true) }) {
            activeBuffList.add(ActiveBuffInfo("divine_sense", "Göttliches Gespür (Divine Sense)", "👁️", "Spürt Himmlische, Unholde & Untote (60ft)", isConcentration = false))
        }

        return CalculatedStats(
            proficiencyBonus = pb,
            effectiveAbilities = effectiveAbilities,
            modifiers = modifiers,
            armorClass = ac,
            armorClassBreakdown = acBreakdown,
            maxHp = maxHp,
            currentHp = character.currentHp,
            tempHp = character.tempHp,
            remainingHitDice = remainingHitDice,
            maxHitDice = maxHitDice,
            maxLayOnHands = maxLayOnHands,
            remainingLayOnHands = remainingLayOnHands,
            maxChannelDivinity = maxChannelDivinity,
            remainingChannelDivinity = remainingChannelDivinity,
            savingThrows = savingThrows,
            savingThrowProficiencies = savingThrowProficiencies,
            hasAuraOfProtection = hasAura,
            auraOfProtectionBonus = auraBonus,
            skillModifiers = skillModifiers,
            spellSaveDc = spellSaveDc,
            spellAttackBonus = spellAttackBonus,
            maxPreparedSpells = maxPreparedSpells,
            spellSlots = spellSlots,
            totalWeightLbs = totalWeight,
            carryCapacityLbs = carryCapacity,
            attacks = attacks,
            initiative = initiative,
            speedFt = 30,
            hasDmOverrides = character.dmOverrides.isActive,
            activeBuffs = activeBuffList
        )
    }

    fun calculateProficiencyBonus(level: Int): Int = (level - 1) / 4 + 2

    private fun calculateEffectiveAbilities(character: CharacterSheet): AbilityScores {
        var scores = character.baseAbilityScores

        // Item-Effekte (Overrides zuerst, dann Boni)
        character.inventory.filter { it.isEquipped && (!it.requiresAttunement || it.isAttuned) }
            .forEach { item ->
                item.effects.forEach { effect ->
                    when (effect) {
                        is ItemEffect.AbilityOverride -> {
                            val current = scores.getScore(effect.ability)
                            if (effect.score > current) {
                                scores = scores.withScore(effect.ability, effect.score)
                            }
                        }
                        is ItemEffect.AbilityBonus -> {
                            val current = scores.getScore(effect.ability)
                            scores = scores.withScore(effect.ability, current + effect.bonus)
                        }
                        else -> Unit
                    }
                }
            }

        // DM Overrides anwenden
        character.dmOverrides.abilityOverrides.forEach { (ability, overrideVal) ->
            scores = scores.withScore(ability, overrideVal)
        }
        character.dmOverrides.abilityBonuses.forEach { (ability, bonus) ->
            val current = scores.getScore(ability)
            scores = scores.withScore(ability, current + bonus)
        }

        return scores
    }

    private fun calculateArmorClass(character: CharacterSheet, dexMod: Int): Pair<Int, String> {
        if (character.dmOverrides.acOverride != null) {
            return character.dmOverrides.acOverride to "DM Override (${character.dmOverrides.acOverride})"
        }

        val equippedArmor = character.inventory.firstOrNull { it.type == ItemType.ARMOR && it.isEquipped }
        val equippedShield = character.inventory.firstOrNull { it.type == ItemType.SHIELD && it.isEquipped }

        var baseAc: Int
        val breakdownParts = mutableListOf<String>()

        if (equippedArmor != null) {
            when (equippedArmor.armorType) {
                ArmorType.HEAVY -> {
                    baseAc = equippedArmor.baseAc
                    breakdownParts.add("${equippedArmor.name} ($baseAc)")
                }
                ArmorType.MEDIUM -> {
                    val cappedDex = min(2, dexMod)
                    baseAc = equippedArmor.baseAc + cappedDex
                    breakdownParts.add("${equippedArmor.name} (${equippedArmor.baseAc}) + DEX max 2 ($cappedDex)")
                }
                ArmorType.LIGHT -> {
                    baseAc = equippedArmor.baseAc + dexMod
                    breakdownParts.add("${equippedArmor.name} (${equippedArmor.baseAc}) + DEX ($dexMod)")
                }
                else -> {
                    baseAc = 10 + dexMod
                    breakdownParts.add("Ohne Rüstung (10 + DEX $dexMod)")
                }
            }
        } else {
            baseAc = 10 + dexMod
            breakdownParts.add("Ohne Rüstung (10 + DEX $dexMod)")
        }

        if (equippedShield != null) {
            val shieldBonus = if (equippedShield.baseAc > 0) equippedShield.baseAc else 2
            baseAc += shieldBonus
            breakdownParts.add("${equippedShield.name} (+$shieldBonus)")
        }

        // Item-Boni (+1 Rings, Cloaks, Shields etc.)
        character.inventory.filter { it.isEquipped && (!it.requiresAttunement || it.isAttuned) }
            .forEach { item ->
                item.effects.forEach { effect ->
                    if (effect is ItemEffect.AcBonus) {
                        baseAc += effect.bonus
                        breakdownParts.add("${item.name} (+${effect.bonus})")
                    }
                }
            }

        if (character.dmOverrides.acBonus != 0) {
            baseAc += character.dmOverrides.acBonus
            breakdownParts.add("DM Bonus (+${character.dmOverrides.acBonus})")
        }

        // Defense Fighting Style (+1 AC wenn Rüstung getragen wird)
        val hasDefense = character.fightingStyle?.equals("Defense", ignoreCase = true) == true ||
                character.feats.any { it.contains("Defense", ignoreCase = true) }
        if (hasDefense && equippedArmor != null) {
            baseAc += 1
            breakdownParts.add("Defense (+1)")
        }

        // Aktiver Zauber-Buff: Shield of Faith (+2 AC)
        val hasShieldOfFaith = character.activeBuffIds.any {
            it.equals("srd_shield_of_faith", ignoreCase = true) || it.equals("shield_of_faith", ignoreCase = true)
        }
        if (hasShieldOfFaith) {
            baseAc += 2
            breakdownParts.add("Shield of Faith (+2)")
        }

        return baseAc to breakdownParts.joinToString(" + ")
    }

    private fun calculateAttack(
        weapon: Item,
        pb: Int,
        strMod: Int,
        dexMod: Int,
        chaMod: Int = 0,
        masteredWeapons: List<String>,
        fightingStyle: String? = null,
        feats: List<String> = emptyList(),
        activeBuffIds: Set<String> = emptySet()
    ): AttackInfo {
        val useDex = weapon.isFinesse && dexMod > strMod
        val abilityMod = if (useDex) dexMod else strMod
        val itemAttackBonus = weapon.effects.filterIsInstance<ItemEffect.AttackBonus>().sumOf { it.bonus }
        val itemDamageBonus = weapon.effects.filterIsInstance<ItemEffect.DamageBonus>().sumOf { it.bonus }

        val hasDueling = (fightingStyle?.equals("Dueling", ignoreCase = true) == true ||
                feats.any { it.contains("Dueling", ignoreCase = true) }) && !weapon.isTwoHanded
        val duelingBonus = if (hasDueling) 2 else 0

        // Channel Divinity: Sacred Weapon (+CHA to attack rolls)
        val isSacredWeapon = activeBuffIds.any { it.equals("sacred_weapon", ignoreCase = true) }
        val sacredWeaponBonus = if (isSacredWeapon) max(1, chaMod) else 0

        val totalAttackBonus = pb + abilityMod + itemAttackBonus + sacredWeaponBonus
        val totalDamageMod = abilityMod + itemDamageBonus + duelingBonus
        val damageSign = if (totalDamageMod >= 0) "+ $totalDamageMod" else "- ${-totalDamageMod}"
        val duelingSuffix = if (hasDueling) " (inkl. +2 Duellieren)" else ""
        
        var damageStr = "${weapon.damageDice} $damageSign$duelingSuffix"

        val buffNotes = mutableListOf<String>()
        if (isSacredWeapon) {
            buffNotes.add("🌟 Heilige Waffe aktiv (+${sacredWeaponBonus} ATK, 20ft Licht)")
        }
        if (activeBuffIds.any { it.equals("srd_bless", ignoreCase = true) || it.equals("bless", ignoreCase = true) }) {
            buffNotes.add("✨ Segen aktiv (+1d4 auf Angriffswürfe)")
        }
        if (activeBuffIds.any { it.equals("vow_of_enmity", ignoreCase = true) }) {
            buffNotes.add("🎯 Gelübde der Feindschaft aktiv (Vorteil auf Angriffswürfe)")
        }
        if (activeBuffIds.any { it.equals("srd_divine_favor", ignoreCase = true) || it.equals("divine_favor", ignoreCase = true) }) {
            damageStr += " + 1d4 Radiant"
            buffNotes.add("⚔️ Göttliche Gunst aktiv (+1d4 Gleißender Schaden)")
        }
        if (activeBuffIds.any { it.equals("srd_searing_smite", ignoreCase = true) || it.equals("searing_smite", ignoreCase = true) }) {
            buffNotes.add("🔥 Sengender Smite aktiv (+1d6 Feuerschaden & Brand)")
        }
        if (activeBuffIds.any { it.equals("srd_wrathful_smite", ignoreCase = true) || it.equals("wrathful_smite", ignoreCase = true) }) {
            buffNotes.add("👻 Zorniger Smite aktiv (+1d6 Psychisch & Verängstigt)")
        }

        val isMastered = masteredWeapons.any { it.equals(weapon.name, ignoreCase = true) }

        return AttackInfo(
            item = weapon,
            attackBonus = totalAttackBonus,
            damageString = damageStr,
            damageType = weapon.damageType,
            isMasteryActive = isMastered && weapon.mastery != null,
            masteryEffect = if (isMastered) weapon.mastery else null,
            activeBuffNotes = buffNotes
        )
    }

    fun getPreparedSpellsForLevel(level: Int): Int = when (level) {
        1 -> 4
        2 -> 5
        3 -> 6
        4 -> 7
        5 -> 9
        6 -> 10
        7 -> 11
        8 -> 12
        9 -> 14
        10 -> 15
        11 -> 16
        12 -> 16
        13 -> 18
        14 -> 18
        15 -> 19
        16 -> 21
        17 -> 22
        18 -> 23
        19 -> 24
        else -> 25
    }

    fun getSpellSlotsForLevel(level: Int, usages: Map<Int, Int>): List<SpellSlotState> {
        val slotTable = mapOf(
            1 to listOf(2, 0, 0, 0, 0),
            2 to listOf(2, 0, 0, 0, 0),
            3 to listOf(3, 0, 0, 0, 0),
            4 to listOf(3, 0, 0, 0, 0),
            5 to listOf(4, 2, 0, 0, 0),
            6 to listOf(4, 2, 0, 0, 0),
            7 to listOf(4, 3, 0, 0, 0),
            8 to listOf(4, 3, 0, 0, 0),
            9 to listOf(4, 3, 2, 0, 0),
            10 to listOf(4, 3, 2, 0, 0),
            11 to listOf(4, 3, 3, 0, 0),
            12 to listOf(4, 3, 3, 0, 0),
            13 to listOf(4, 3, 3, 1, 0),
            14 to listOf(4, 3, 3, 1, 0),
            15 to listOf(4, 3, 3, 2, 0),
            16 to listOf(4, 3, 3, 2, 0),
            17 to listOf(4, 3, 3, 3, 1),
            18 to listOf(4, 3, 3, 3, 1),
            19 to listOf(4, 3, 3, 3, 2),
            20 to listOf(4, 3, 3, 3, 2)
        )

        val slots = slotTable[level.coerceIn(1, 20)] ?: listOf(2, 0, 0, 0, 0)
        return (1..5).mapNotNull { slotLevel ->
            val maxSlots = slots.getOrElse(slotLevel - 1) { 0 }
            if (maxSlots > 0) {
                SpellSlotState(
                    level = slotLevel,
                    maxSlots = maxSlots,
                    usedSlots = (usages[slotLevel] ?: 0).coerceIn(0, maxSlots)
                )
            } else null
        }
    }
}
