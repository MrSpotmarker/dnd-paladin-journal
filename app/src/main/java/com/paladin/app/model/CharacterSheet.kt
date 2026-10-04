package com.paladin.app.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class Skill(val displayName: String, val ability: Ability) {
    ATHLETICS("Athletik", Ability.STRENGTH),
    ACROBATICS("Akrobatik", Ability.DEXTERITY),
    SLEIGHT_OF_HAND("Fingerfertigkeit", Ability.DEXTERITY),
    STEALTH("Heimlichkeit", Ability.DEXTERITY),
    ARCANA("Arkane Kunde", Ability.INTELLIGENCE),
    HISTORY("Geschichte", Ability.INTELLIGENCE),
    INVESTIGATION("Nachforschungen", Ability.INTELLIGENCE),
    NATURE("Naturkunde", Ability.INTELLIGENCE),
    RELIGION("Religion", Ability.INTELLIGENCE),
    ANIMAL_HANDLING("Mit Tieren umgehen", Ability.WISDOM),
    INSIGHT("Motiv erkennen", Ability.WISDOM),
    MEDICINE("Heilkunde", Ability.WISDOM),
    PERCEPTION("Wahrnehmung", Ability.WISDOM),
    SURVIVAL("Überlebenskunst", Ability.WISDOM),
    DECEPTION("Täuschen", Ability.CHARISMA),
    INTIMIDATION("Einschüchtern", Ability.CHARISMA),
    PERFORMANCE("Auftreten", Ability.CHARISMA),
    PERSUASION("Überzeugen", Ability.CHARISMA)
}

@Serializable
data class DeathSavesState(
    val successes: Int = 0,
    val failures: Int = 0
)

@Serializable
data class CharacterSheet(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Sir Galahad",
    val level: Int = 1,
    val oath: String? = null, // z. B. "Oath of Devotion", "Oath of Vengeance"
    val baseAbilityScores: AbilityScores = AbilityScores(),
    val currentHp: Int = 12,
    val maxHpManualAdjustment: Int = 0, // falls gewürfelt statt Durchschnitt
    val tempHp: Int = 0,
    val hitDiceUsed: Int = 0,
    val layOnHandsUsed: Int = 0,
    val channelDivinityUsed: Int = 0,
    val proficientSkills: Set<Skill> = setOf(Skill.ATHLETICS, Skill.RELIGION),
    val masteredWeaponNames: List<String> = listOf("Longsword", "Halberd"),
    val preparedSpellIds: Set<String> = emptySet(),
    val spellSlotUsages: Map<Int, Int> = emptyMap(), // Level -> Used slots
    val inventory: List<Item> = emptyList(),
    val customSpells: List<Spell> = emptyList(),
    val dmOverrides: DmOverrides = DmOverrides(),
    val goldPieces: Double = 15.0,
    val deathSaves: DeathSavesState = DeathSavesState(),
    val notes: String = ""
)
