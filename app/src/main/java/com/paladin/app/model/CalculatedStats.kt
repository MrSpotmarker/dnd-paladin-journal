package com.paladin.app.model

data class ActiveBuffInfo(
    val id: String,
    val name: String,
    val icon: String,
    val effectSummary: String,
    val isConcentration: Boolean = false
)

data class AttackInfo(
    val item: Item,
    val attackBonus: Int,
    val damageString: String,
    val damageType: String,
    val isMasteryActive: Boolean,
    val masteryEffect: WeaponMastery?,
    val activeBuffNotes: List<String> = emptyList(),
    val damageBreakdown: List<String> = emptyList(),
    val attackBreakdown: String = ""
)

data class ArmorClassElement(
    val name: String,
    val value: String,
    val detail: String = "",
    val icon: String = "🛡️"
)

data class CalculatedStats(
    val proficiencyBonus: Int,
    val effectiveAbilities: AbilityScores,
    val modifiers: Map<Ability, Int>,
    val armorClass: Int,
    val armorClassBreakdown: String,
    val armorClassElements: List<ArmorClassElement> = emptyList(),
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
    val auraRangeFt: Int = 10,
    val hasAuraOfCourage: Boolean = false,
    val skillModifiers: Map<Skill, Int>,
    val spellSaveDc: Int,
    val spellAttackBonus: Int,
    val maxPreparedSpells: Int,
    val spellSlots: List<SpellSlotState>,
    val totalWeightLbs: Double,
    val carryCapacityLbs: Double,
    val attacks: List<AttackInfo>,
    val initiative: Int,
    val speedFt: Int = 30,
    val hasDmOverrides: Boolean,
    val activeBuffs: List<ActiveBuffInfo> = emptyList(),
    val activeConditions: Set<Condition> = emptySet(),
    val attacksPerAction: Int = 1,
    val hasRadiantStrikes: Boolean = false,
    val steedHp: Int = 0,
    val steedCurrentHp: Int = 0,
    val steedMaxHp: Int = 0
)
