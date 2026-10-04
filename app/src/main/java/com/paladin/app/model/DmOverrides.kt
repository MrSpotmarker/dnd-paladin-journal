package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
data class DmOverrides(
    val acOverride: Int? = null,
    val acBonus: Int = 0,
    val hpMaxBonus: Int = 0,
    val abilityBonuses: Map<Ability, Int> = emptyMap(),
    val abilityOverrides: Map<Ability, Int> = emptyMap(),
    val savingThrowBonus: Int = 0,
    val spellDcBonus: Int = 0,
    val notes: String = ""
) {
    val isActive: Boolean
        get() = acOverride != null ||
                acBonus != 0 ||
                hpMaxBonus != 0 ||
                abilityBonuses.isNotEmpty() ||
                abilityOverrides.isNotEmpty() ||
                savingThrowBonus != 0 ||
                spellDcBonus != 0 ||
                notes.isNotBlank()
}
