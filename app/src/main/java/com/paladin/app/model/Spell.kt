package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class SpellSchool(val displayName: String) {
    ABJURATION("Bannmagie"),
    CONJURATION("Beschwörung"),
    DIVINATION("Erkenntnismagie"),
    ENCHANTMENT("Verzauberung"),
    EVOCATION("Hervorrufung"),
    ILLUSION("Illusion"),
    NECROMANCY("Nekromantie"),
    TRANSMUTATION("Verwandlung")
}

@Serializable
data class Spell(
    val id: String,
    val name: String,
    val level: Int, // 1..5 for Paladin
    val school: SpellSchool,
    val castingTime: String = "1 Action",
    val range: String = "Self",
    val duration: String = "Instantaneous",
    val isConcentration: Boolean = false,
    val isRitual: Boolean = false,
    val verbal: Boolean = true,
    val somatic: Boolean = true,
    val material: Boolean = false,
    val materialDescription: String = "",
    val description: String,
    val source: String = "SRD 5.2"
)

@Serializable
data class SpellSlotState(
    val level: Int,
    val maxSlots: Int,
    val usedSlots: Int = 0
) {
    val remainingSlots: Int get() = (maxSlots - usedSlots).coerceAtLeast(0)
}
