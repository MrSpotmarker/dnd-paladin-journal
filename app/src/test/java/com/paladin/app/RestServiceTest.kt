package com.paladin.app

import com.paladin.app.engine.RestService
import com.paladin.app.model.CharacterSheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RestServiceTest {

    @Test
    fun testDamageAbsorbsTempHpFirst() {
        val character = CharacterSheet(
            currentHp = 20,
            tempHp = 10
        )

        // Damage 5 -> Temp HP decreases to 5, Current HP untouched
        val after5Damage = RestService.applyDamage(character, 5)
        assertEquals(5, after5Damage.tempHp)
        assertEquals(20, after5Damage.currentHp)

        // Damage 15 -> Temp HP goes to 0, remaining 5 goes to Current HP (15)
        val after15Damage = RestService.applyDamage(character, 15)
        assertEquals(0, after15Damage.tempHp)
        assertEquals(15, after15Damage.currentHp)
    }

    @Test
    fun testHealingCappedAtMaxHp() {
        val character = CharacterSheet(currentHp = 15)
        val healed = RestService.applyHealing(character, 10, maxHp = 20)
        assertEquals(20, healed.currentHp)
    }

    @Test
    fun testShortRestRegainsChannelDivinity2024Rule() {
        val character = CharacterSheet(
            level = 3,
            currentHp = 10,
            hitDiceUsed = 0,
            channelDivinityUsed = 2, // used both uses
            activeBuffIds = setOf("srd_shield_of_faith", "sacred_weapon")
        )

        // Short Rest spends 1 hit die, rolls 8 HP
        val rested = RestService.performShortRest(
            character = character,
            hitDiceToSpend = 1,
            totalHpHealed = 8,
            maxHp = 25
        )

        assertEquals(18, rested.currentHp)
        assertEquals(1, rested.hitDiceUsed)
        // In 2024 rules: Regains 1 expended Channel Divinity use! So channelDivinityUsed drops to 1
        assertEquals(1, rested.channelDivinityUsed)
        assertTrue("Active buffs should expire after short rest", rested.activeBuffIds.isEmpty())
    }

    @Test
    fun testLongRestResetsAllCoreResources() {
        val character = CharacterSheet(
            level = 4,
            currentHp = 5,
            tempHp = 8,
            hitDiceUsed = 3,
            layOnHandsUsed = 15,
            channelDivinityUsed = 2,
            spellSlotUsages = mapOf(1 to 3),
            activeBuffIds = setOf("srd_bless")
        )

        val rested = RestService.performLongRest(character, maxHp = 32)

        assertEquals(32, rested.currentHp)
        assertEquals(0, rested.tempHp)
        assertEquals(0, rested.layOnHandsUsed)
        assertEquals(0, rested.channelDivinityUsed)
        assertTrue(rested.spellSlotUsages.isEmpty())
        assertTrue("Active buffs should expire after long rest", rested.activeBuffIds.isEmpty())
        // Hit dice recovery: half of level (4 / 2 = 2 recovered). hitDiceUsed was 3 -> now 1
        assertEquals(1, rested.hitDiceUsed)
    }
}
