package com.paladin.app.engine

import com.paladin.app.model.CharacterSheet
import com.paladin.app.model.DeathSavesState
import kotlin.math.max
import kotlin.math.min

object RestService {

    /**
     * Führt eine Kurze Rast (Short Rest) durch:
     * - Heilt um den erwürfelten Betrag der ausgegebenen Trefferwürfel.
     * - Zieht die ausgegebenen Trefferwürfel ab.
     * - 2024 Regel: Ein Paladin ab Stufe 3 regeneriert 1 verbrauchte Channel Divinity-Nutzung!
     */
    fun performShortRest(
        character: CharacterSheet,
        hitDiceToSpend: Int,
        totalHpHealed: Int,
        maxHp: Int
    ): CharacterSheet {
        val newHp = min(maxHp, character.currentHp + totalHpHealed)
        val newHitDiceUsed = min(character.level, character.hitDiceUsed + hitDiceToSpend)

        // 2024 Paladin Rule: Regain 1 expended use of Channel Divinity on Short Rest
        val newChannelDivinityUsed = if (character.level >= 3 && character.channelDivinityUsed > 0) {
            character.channelDivinityUsed - 1
        } else {
            character.channelDivinityUsed
        }

        return character.copy(
            currentHp = newHp,
            hitDiceUsed = newHitDiceUsed,
            channelDivinityUsed = newChannelDivinityUsed,
            activeBuffIds = emptySet()
        )
    }

    /**
     * Führt eine Lange Rast (Long Rest) durch:
     * - HP werden komplett geheilt (= maxHp).
     * - Temp HP verfallen auf 0.
     * - Alle Zauberplätze werden vollständig regeneriert.
     * - Lay on Hands-Pool wird vollständig aufgefüllt (layOnHandsUsed = 0).
     * - Channel Divinity wird vollständig aufgefüllt (channelDivinityUsed = 0).
     * - Die Hälfte der Trefferwürfel (mindestens 1) wird regeneriert.
     * - Todes-Rettungswürfe werden zurückgesetzt.
     * - Alle temporären Zauber und Buffs enden.
     */
    fun performLongRest(character: CharacterSheet, maxHp: Int): CharacterSheet {
        val recoveredHitDice = max(1, character.level / 2)
        val newHitDiceUsed = max(0, character.hitDiceUsed - recoveredHitDice)

        return character.copy(
            currentHp = maxHp,
            tempHp = 0,
            hitDiceUsed = newHitDiceUsed,
            layOnHandsUsed = 0,
            channelDivinityUsed = 0,
            spellSlotUsages = emptyMap(),
            activeBuffIds = emptySet(),
            deathSaves = DeathSavesState(successes = 0, failures = 0)
        )
    }

    /**
     * Wendet Schaden an: Zieht zuerst von Temp HP ab, dann von Current HP.
     */
    fun applyDamage(character: CharacterSheet, damage: Int): CharacterSheet {
        if (damage <= 0) return character

        var remainingDamage = damage
        var newTempHp = character.tempHp

        if (newTempHp > 0) {
            if (remainingDamage <= newTempHp) {
                newTempHp -= remainingDamage
                remainingDamage = 0
            } else {
                remainingDamage -= newTempHp
                newTempHp = 0
            }
        }

        val newHp = max(0, character.currentHp - remainingDamage)
        return character.copy(
            currentHp = newHp,
            tempHp = newTempHp
        )
    }

    /**
     * Wendet Heilung an (kann nicht über Max HP heilen).
     */
    fun applyHealing(character: CharacterSheet, healing: Int, maxHp: Int): CharacterSheet {
        if (healing <= 0) return character
        val newHp = min(maxHp, character.currentHp + healing)
        return character.copy(currentHp = newHp)
    }
}
