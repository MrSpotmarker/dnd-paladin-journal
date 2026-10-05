package com.paladin.app

import com.paladin.app.engine.CharacterStatsEngine
import com.paladin.app.model.AbilityScores
import com.paladin.app.model.CharacterSheet
import com.paladin.app.model.SmiteRegistry
import com.paladin.app.model.Spell
import com.paladin.app.model.SpellSchool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmiteFeatureTest {

    @Test
    fun testPaladinsSmiteScaling() {
        val smite = SmiteRegistry.PALADINS_SMITE

        // Base slot level 1 -> 2d8 Radiant
        assertEquals(2, smite.calculateDiceCount(1))
        assertEquals("2d8 Radiant", smite.damageBonusString(1))

        // Level 2 slot -> 3d8 Radiant
        assertEquals(3, smite.calculateDiceCount(2))
        assertEquals("3d8 Radiant", smite.damageBonusString(2))

        // Level 3 slot -> 4d8 Radiant
        assertEquals(4, smite.calculateDiceCount(3))
        assertEquals("4d8 Radiant", smite.damageBonusString(3))
    }

    @Test
    fun testSearingSmiteScaling() {
        val searing = SmiteRegistry.KNOWN_SMITES["srd_searing_smite"]
        assertNotNull(searing)
        val smite = searing!!

        assertTrue("Searing Smite should require concentration", smite.isConcentration)

        // Level 1 slot -> 1d6 Feuer
        assertEquals(1, smite.calculateDiceCount(1))
        assertEquals("1d6 Feuer", smite.damageBonusString(1))

        // Level 2 slot -> 2d6 Feuer
        assertEquals(2, smite.calculateDiceCount(2))
        assertEquals("2d6 Feuer", smite.damageBonusString(2))

        // Level 3 slot -> 3d6 Feuer
        assertEquals(3, smite.calculateDiceCount(3))
        assertEquals("3d6 Feuer", smite.damageBonusString(3))
    }

    @Test
    fun testThunderousSmiteScaling() {
        val thunderous = SmiteRegistry.KNOWN_SMITES["srd_thunderous_smite"]
        assertNotNull(thunderous)
        val smite = thunderous!!

        assertTrue("Thunderous Smite should require concentration", smite.isConcentration)

        // Level 1 slot -> 2d6 Donner
        assertEquals(2, smite.calculateDiceCount(1))
        assertEquals("2d6 Donner", smite.damageBonusString(1))

        // Level 2 slot -> 3d6 Donner
        assertEquals(3, smite.calculateDiceCount(2))
        assertEquals("3d6 Donner", smite.damageBonusString(2))
    }

    @Test
    fun testResolveAvailableSmites() {
        val preparedSpells = listOf(
            Spell(
                id = "srd_shield_of_faith",
                name = "Shield of Faith",
                level = 1,
                school = SpellSchool.ABJURATION,
                castingTime = "1 Bonus Action",
                range = "60 ft",
                duration = "Concentration, bis zu 10 Minuten",
                isConcentration = true,
                description = "AC +2"
            ),
            Spell(
                id = "srd_searing_smite",
                name = "Searing Smite",
                level = 1,
                school = SpellSchool.EVOCATION,
                castingTime = "1 Bonus Action",
                range = "Selbst",
                duration = "Concentration, bis zu 1 Minute",
                isConcentration = true,
                description = "+1d6 Feuerschaden"
            )
        )

        val resolved = SmiteRegistry.resolveAvailableSmites(preparedSpells)

        // Must always have Paladin's Smite + Searing Smite
        assertEquals(2, resolved.size)
        assertEquals("srd_paladins_smite", resolved[0].id)
        assertEquals("srd_searing_smite", resolved[1].id)
    }

    @Test
    fun testActiveBuffsIncludeThunderousAndSearingSmite() {
        val character = CharacterSheet(
            name = "Smite Knight",
            level = 3,
            baseAbilityScores = AbilityScores(),
            activeBuffIds = setOf("srd_thunderous_smite")
        )

        val stats = CharacterStatsEngine.calculate(character)
        val thunderousBuff = stats.activeBuffs.find { it.id == "srd_thunderous_smite" }

        assertNotNull("Thunderous Smite buff should be active", thunderousBuff)
        assertTrue(thunderousBuff!!.isConcentration)
        assertEquals("Thunderous Smite", thunderousBuff.name)
    }
}
