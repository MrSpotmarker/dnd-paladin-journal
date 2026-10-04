package com.paladin.app

import com.paladin.app.engine.CharacterStatsEngine
import com.paladin.app.engine.RestService
import com.paladin.app.model.*
import org.junit.Assert.*
import org.junit.Test

class CharacterStatsEngineTest {

    @Test
    fun testLevel1Paladin2024Rules() {
        val character = CharacterSheet(
            name = "Test Paladin",
            level = 1,
            baseAbilityScores = AbilityScores(
                strength = 16, // +3
                dexterity = 10, // +0
                constitution = 14, // +2
                intelligence = 8,  // -1
                wisdom = 10,       // +0
                charisma = 16      // +3
            )
        )

        val stats = CharacterStatsEngine.calculate(character)

        assertEquals("Proficiency Bonus at Lvl 1 should be 2", 2, stats.proficiencyBonus)
        assertEquals("Max HP at Lvl 1 should be 10 + CON (12)", 12, stats.maxHp)
        assertEquals("Lay on Hands at Lvl 1 should be 5", 5, stats.maxLayOnHands)
        assertEquals("Channel Divinity at Lvl 1 should be 0", 0, stats.maxChannelDivinity)
        assertEquals("2024 Paladin has 2 spell slots at Level 1", 1, stats.spellSlots.size)
        assertEquals("Level 1 spell slots count is 2", 2, stats.spellSlots[0].maxSlots)
        assertEquals("Max prepared spells at Level 1 is 4", 4, stats.maxPreparedSpells)
        assertEquals("Spell Save DC is 8 + 2 + 3 = 13", 13, stats.spellSaveDc)
        assertEquals("Spell Attack Bonus is 2 + 3 = 5", 5, stats.spellAttackBonus)
    }

    @Test
    fun testLevel6AuraOfProtection() {
        val character = CharacterSheet(
            level = 6,
            baseAbilityScores = AbilityScores(
                strength = 16,
                dexterity = 10,
                constitution = 14,
                intelligence = 8,
                wisdom = 10,
                charisma = 16 // CHA mod = +3
            )
        )

        val stats = CharacterStatsEngine.calculate(character)

        assertTrue("Aura of Protection should be active at Level 6", stats.hasAuraOfProtection)
        assertEquals("Aura bonus should equal CHA mod (+3)", 3, stats.auraOfProtectionBonus)

        // STR save = +3 (mod) + 0 (not prof) + 3 (aura) = +6
        assertEquals(6, stats.savingThrows[Ability.STRENGTH])
        // WIS save = +0 (mod) + 3 (PB) + 3 (aura) = +6
        assertEquals(6, stats.savingThrows[Ability.WISDOM])
        // CHA save = +3 (mod) + 3 (PB) + 3 (aura) = +9
        assertEquals(9, stats.savingThrows[Ability.CHARISMA])
    }

    @Test
    fun testArmorAndShieldCalculation() {
        val plateArmor = Item(
            id = "plate",
            name = "Plattenrüstung",
            type = ItemType.ARMOR,
            armorType = ArmorType.HEAVY,
            baseAc = 18,
            isEquipped = true
        )
        val shield = Item(
            id = "shield",
            name = "Schild",
            type = ItemType.SHIELD,
            armorType = ArmorType.SHIELD,
            baseAc = 2,
            isEquipped = true
        )
        val ringOfProtection = Item(
            id = "ring",
            name = "Ring des Schutzes",
            type = ItemType.MAGIC_ITEM,
            isEquipped = true,
            effects = listOf(ItemEffect.AcBonus(1))
        )

        val character = CharacterSheet(
            inventory = listOf(plateArmor, shield, ringOfProtection)
        )

        val stats = CharacterStatsEngine.calculate(character)
        // 18 (Plate) + 2 (Shield) + 1 (Ring) = 21 AC
        assertEquals(21, stats.armorClass)
    }

    @Test
    fun testStatOverrideItem() {
        val gauntlets = Item(
            id = "gauntlets",
            name = "Gauntlets of Ogre Power",
            type = ItemType.MAGIC_ITEM,
            isEquipped = true,
            effects = listOf(ItemEffect.AbilityOverride(Ability.STRENGTH, 19))
        )

        val character = CharacterSheet(
            baseAbilityScores = AbilityScores(strength = 14),
            inventory = listOf(gauntlets)
        )

        val stats = CharacterStatsEngine.calculate(character)
        assertEquals("STR should be overridden to 19", 19, stats.effectiveAbilities.strength)
        assertEquals("STR modifier should be +4", 4, stats.modifiers[Ability.STRENGTH])
    }

    @Test
    fun testDmOverrides() {
        val character = CharacterSheet(
            dmOverrides = DmOverrides(
                acBonus = 2,
                hpMaxBonus = 5,
                savingThrowBonus = 1,
                notes = "DM Blessing"
            )
        )

        val stats = CharacterStatsEngine.calculate(character)
        assertTrue(stats.hasDmOverrides)
        // Base AC without armor is 10 + DEX (0) + 2 (DM bonus) = 12
        assertEquals(12, stats.armorClass)
        // Max HP = 12 + 5 (DM bonus) = 17
        assertEquals(17, stats.maxHp)
    }

    @Test
    fun testWeaponMasteryCalculation() {
        val longsword = Item(
            id = "longsword",
            name = "Longsword",
            type = ItemType.WEAPON,
            damageDice = "1d8",
            damageType = "Slashing",
            mastery = WeaponMastery.SAP,
            isEquipped = true
        )

        val character = CharacterSheet(
            level = 1,
            baseAbilityScores = AbilityScores(strength = 16), // +3
            masteredWeaponNames = listOf("Longsword"),
            inventory = listOf(longsword)
        )

        val stats = CharacterStatsEngine.calculate(character)
        assertEquals(1, stats.attacks.size)
        val attack = stats.attacks[0]
        assertEquals("Attack bonus should be 2 (PB) + 3 (STR) = 5", 5, attack.attackBonus)
        assertTrue(attack.isMasteryActive)
        assertEquals(WeaponMastery.SAP, attack.masteryEffect)
    }
}
