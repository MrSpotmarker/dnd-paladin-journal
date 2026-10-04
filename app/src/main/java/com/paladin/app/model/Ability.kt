package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class Ability(val abbreviation: String, val displayName: String) {
    STRENGTH("STR", "Stärke"),
    DEXTERITY("DEX", "Geschicklichkeit"),
    CONSTITUTION("CON", "Konstitution"),
    INTELLIGENCE("INT", "Intelligenz"),
    WISDOM("WIS", "Weisheit"),
    CHARISMA("CHA", "Charisma");

    companion object {
        fun calculateModifier(score: Int): Int = Math.floorDiv(score - 10, 2)
        fun formatModifier(mod: Int): String = if (mod >= 0) "+$mod" else "$mod"
    }
}

@Serializable
data class AbilityScores(
    val strength: Int = 16,
    val dexterity: Int = 10,
    val constitution: Int = 14,
    val intelligence: Int = 8,
    val wisdom: Int = 10,
    val charisma: Int = 16
) {
    fun getScore(ability: Ability): Int = when (ability) {
        Ability.STRENGTH -> strength
        Ability.DEXTERITY -> dexterity
        Ability.CONSTITUTION -> constitution
        Ability.INTELLIGENCE -> intelligence
        Ability.WISDOM -> wisdom
        Ability.CHARISMA -> charisma
    }

    fun withScore(ability: Ability, newScore: Int): AbilityScores = when (ability) {
        Ability.STRENGTH -> copy(strength = newScore)
        Ability.DEXTERITY -> copy(dexterity = newScore)
        Ability.CONSTITUTION -> copy(constitution = newScore)
        Ability.INTELLIGENCE -> copy(intelligence = newScore)
        Ability.WISDOM -> copy(wisdom = newScore)
        Ability.CHARISMA -> copy(charisma = newScore)
    }
}
