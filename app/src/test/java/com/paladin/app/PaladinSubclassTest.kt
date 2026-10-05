package com.paladin.app

import com.paladin.app.model.CharacterSheet
import com.paladin.app.model.PaladinProgression
import com.paladin.app.model.PaladinSubclass
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaladinSubclassTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testFromOathStringResolvesStandardSubclasses() {
        assertEquals(PaladinSubclass.DEVOTION, PaladinSubclass.fromOathString("Oath of Devotion"))
        assertEquals(PaladinSubclass.VENGEANCE, PaladinSubclass.fromOathString("Oath of Vengeance"))
        assertEquals(PaladinSubclass.ANCIENTS, PaladinSubclass.fromOathString("Oath of the Ancients"))
        assertEquals(PaladinSubclass.GLORY, PaladinSubclass.fromOathString("Oath of Glory"))
    }

    @Test
    fun testFromOathStringCaseInsensitiveAndPartialMatch() {
        assertEquals(PaladinSubclass.DEVOTION, PaladinSubclass.fromOathString("oath of devotion"))
        assertEquals(PaladinSubclass.DEVOTION, PaladinSubclass.fromOathString("Devotion"))
        assertEquals(PaladinSubclass.DEVOTION, PaladinSubclass.fromOathString("Eid der Hingabe"))
        assertEquals(PaladinSubclass.VENGEANCE, PaladinSubclass.fromOathString("Vengeance"))
        assertEquals(PaladinSubclass.ANCIENTS, PaladinSubclass.fromOathString("Ancients"))
        assertEquals(PaladinSubclass.GLORY, PaladinSubclass.fromOathString("Glory"))
    }

    @Test
    fun testFromOathStringNullOrUnknownReturnsNull() {
        assertNull(PaladinSubclass.fromOathString(null))
        assertNull(PaladinSubclass.fromOathString(""))
        assertNull(PaladinSubclass.fromOathString("   "))
        assertNull(PaladinSubclass.fromOathString("Oath of the Crown"))
    }

    @Test
    fun testCharacterSheetSubclassBridge() {
        val sheetDevotion = CharacterSheet(oath = "Oath of Devotion")
        assertEquals(PaladinSubclass.DEVOTION, sheetDevotion.subclass)

        val sheetCustom = CharacterSheet(oath = "Homebrew Oath")
        assertNull(sheetCustom.subclass)
        assertEquals("Homebrew Oath", sheetCustom.oath)

        val sheetNone = CharacterSheet(oath = null)
        assertNull(sheetNone.subclass)
    }

    @Test
    fun testSubclassFeaturesExistForProgression() {
        for (subclass in PaladinSubclass.entries) {
            val levels = subclass.features.map { it.level }
            assertTrue("Subclass ${subclass.name} must have level 3 feature", levels.contains(3))
            assertTrue("Subclass ${subclass.name} must have level 7 feature", levels.contains(7))
            assertTrue("Subclass ${subclass.name} must have level 15 feature", levels.contains(15))
            assertTrue("Subclass ${subclass.name} must have level 20 feature", levels.contains(20))
            assertTrue("Subclass ${subclass.name} must define channel divinity options", subclass.channelDivinityOptions.isNotEmpty())
            assertFalse("Subclass ${subclass.name} must have aura name", subclass.auraName.isBlank())
        }
    }

    @Test
    fun testPaladinProgressionLevelRules() {
        assertEquals(10, PaladinProgression.getAuraRadiusFt(6))
        assertEquals(10, PaladinProgression.getAuraRadiusFt(17))
        assertEquals(30, PaladinProgression.getAuraRadiusFt(18))
        assertEquals(30, PaladinProgression.getAuraRadiusFt(20))

        assertFalse(PaladinProgression.hasExtraAttack(4))
        assertTrue(PaladinProgression.hasExtraAttack(5))

        assertFalse(PaladinProgression.hasAuraOfProtection(5))
        assertTrue(PaladinProgression.hasAuraOfProtection(6))

        assertFalse(PaladinProgression.hasAuraOfCourage(9))
        assertTrue(PaladinProgression.hasAuraOfCourage(10))

        assertFalse(PaladinProgression.hasRadiantStrikes(10))
        assertTrue(PaladinProgression.hasRadiantStrikes(11))
    }

    @Test
    fun testJsonSerializationPreservesOathString() {
        val original = CharacterSheet(name = "Valerius", level = 5, oath = "Oath of Devotion")
        val jsonStr = json.encodeToString(CharacterSheet.serializer(), original)
        assertTrue(jsonStr.contains("\"oath\":\"Oath of Devotion\""))

        val restored = json.decodeFromString(CharacterSheet.serializer(), jsonStr)
        assertEquals("Oath of Devotion", restored.oath)
        assertEquals(PaladinSubclass.DEVOTION, restored.subclass)
    }
}
