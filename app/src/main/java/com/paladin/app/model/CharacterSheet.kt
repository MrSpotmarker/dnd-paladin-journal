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
    PERSUASION("Überzeugen", Ability.CHARISMA);

    companion object {
        val paladinClassSkills = setOf(
            ATHLETICS,
            INSIGHT,
            INTIMIDATION,
            MEDICINE,
            PERSUASION,
            RELIGION
        )
    }
}

@Serializable
data class DeathSavesState(
    val successes: Int = 0,
    val failures: Int = 0
)

@Serializable
data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val dateText: String = "",
    val content: String = "",
    val imagePaths: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = title.ifBlank { dateText.ifBlank { "Unbenannter Eintrag" } }
}

@Serializable
data class CharacterSheet(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Sir Galahad",
    val level: Int = 1,
    val hasCompletedCreation: Boolean = true,
    val oath: String? = null, // z. B. "Oath of Devotion", "Oath of Vengeance"
    val species: Species = Species.HUMAN,
    val heroicInspirations: Int = 1,
    val baseAbilityScores: AbilityScores = AbilityScores(),
    val currentHp: Int = 12,
    val maxHpManualAdjustment: Int = 0, // falls gewürfelt statt Durchschnitt
    val tempHp: Int = 0,
    val hitDiceUsed: Int = 0,
    val layOnHandsUsed: Int = 0,
    val channelDivinityUsed: Int = 0,
    val proficientSkills: Set<Skill> = setOf(Skill.ATHLETICS, Skill.RELIGION),
    val masteredWeaponNames: List<String> = listOf("Longsword", "Halberd"),
    val fightingStyle: String? = null, // z. B. "Defense", "Dueling"
    val feats: List<String> = emptyList(), // z. B. ["Alert", "Savage Attacker"]
    val preparedSpellIds: Set<String> = emptySet(),
    val activeBuffIds: Set<String> = emptySet(), // z. B. ["srd_shield_of_faith", "srd_bless", "sacred_weapon"]
    val spellSlotUsages: Map<Int, Int> = emptyMap(), // Level -> Used slots
    val inventory: List<Item> = emptyList(),
    val customSpells: List<Spell> = emptyList(),
    val dmOverrides: DmOverrides = DmOverrides(),
    val goldPieces: Double = 15.0,
    val silverPieces: Int = 0,
    val copperPieces: Int = 0,
    val deathSaves: DeathSavesState = DeathSavesState(),
    val notes: String = "",
    val journalEntries: List<JournalEntry> = emptyList(),
    val customProfileImagePath: String? = null,
    val customFullImagePath: String? = null,
    val activeConditions: Set<Condition> = emptySet(),
    val freeFindSteedUsed: Boolean = false,
    val isSteedSummoned: Boolean = false,
    val steedCurrentHp: Int? = null,
    val steedMaxHpOverride: Int? = null,
    val steedCreatureType: String = "Celestial",
    val steedSpecialUsed: Boolean = false
) {
    val totalGoldEquivalent: Double
        get() = goldPieces + (silverPieces / 10.0) + (copperPieces / 100.0)

    val subclass: PaladinSubclass?
        get() = PaladinSubclass.fromOathString(oath)
}
