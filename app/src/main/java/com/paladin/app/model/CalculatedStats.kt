package com.paladin.app.model

data class AttackInfo(
    val item: Item,
    val attackBonus: Int,
    val damageString: String,
    val damageType: String,
    val isMasteryActive: Boolean,
    val masteryEffect: WeaponMastery?
)

data class CalculatedStats(
    val proficiencyBonus: Int,
    val effectiveAbilities: AbilityScores,
    val modifiers: Map<Ability, Int>,
    val armorClass: Int,
    val armorClassBreakdown: String,
    val maxHp: Int,
    val currentHp: Int,
    val tempHp: Int,
    val remainingHitDice: Int,
    val maxHitDice: Int,
    val maxLayOnHands: Int,
    val remainingLayOnHands: Int,
    val maxChannelDivinity: Int,
    val remainingChannelDivinity: Int,
    val savingThrows: Map<Ability, Int>,
    val savingThrowProficiencies: Set<Ability>,
    val hasAuraOfProtection: Boolean,
    val auraOfProtectionBonus: Int,
    val skillModifiers: Map<Skill, Int>,
    val spellSaveDc: Int,
    val spellAttackBonus: Int,
    val maxPreparedSpells: Int,
    val spellSlots: List<SpellSlotState>,
    val totalWeightLbs: Double,
    val carryCapacityLbs: Double,
    val attacks: List<AttackInfo>,
    val hasDmOverrides: Boolean
)
