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

    companion object {
        const val POINT_BUY_BUDGET = 27

        val STANDARD_ARRAY = mapOf(
            Ability.STRENGTH to 15,
            Ability.DEXTERITY to 10,
            Ability.CONSTITUTION to 14,
            Ability.INTELLIGENCE to 8,
            Ability.WISDOM to 12,
            Ability.CHARISMA to 13
        )

        fun calculatePointCost(score: Int): Int = when {
            score <= 8 -> 0
            score == 9 -> 1
            score == 10 -> 2
            score == 11 -> 3
            score == 12 -> 4
            score == 13 -> 5
            score == 14 -> 7
            score == 15 -> 9
            score == 16 -> 11
            score == 17 -> 13
            score >= 18 -> 15 + (score - 18) * 2
            else -> 0
        }

        fun calculateTotalPointsSpent(scores: Map<Ability, Int>): Int =
            scores.values.sumOf { calculatePointCost(it) }

        fun calculatePointsRemaining(scores: Map<Ability, Int>, budget: Int = POINT_BUY_BUDGET): Int =
            budget - calculateTotalPointsSpent(scores)
    }
}
