package com.paladin.app.model

import kotlinx.serialization.Serializable

/**
 * Represents a core class feature of the Paladin (D&D 2024).
 */
@Serializable
data class PaladinFeature(
    val level: Int,
    val name: String,
    val summary: String,
    val isSubclassChoice: Boolean = false,
    val isAsiOrFeat: Boolean = false
)

/**
 * Central catalog of Paladin progression rules (D&D 2024).
 */
object PaladinProgression {

    val classFeatures: List<PaladinFeature> = listOf(
        PaladinFeature(1, "Lay on Hands (Handauflegen)", "Heilpool = 5 × Stufe, heilt oder kuriert Gift (5 HP)"),
        PaladinFeature(1, "Spellcasting (Zauber)", "Vorbereitete Zauber & Zauberplätze ab Stufe 1"),
        PaladinFeature(1, "Weapon Mastery (Waffenmeisterschaft)", "Meisterschaften für 2 Waffen freigeschaltet"),
        PaladinFeature(2, "Fighting Style (Kampfstil)", "Kampfstil-Talent wählbar"),
        PaladinFeature(2, "Paladin's Smite", "Divine Smite immer vorbereitet, 1x/Tag ohne Zauberplatz"),
        PaladinFeature(3, "Sacred Oath (Heiliger Eid)", "Wahl der Unterklasse & Channel Divinity", isSubclassChoice = true),
        PaladinFeature(3, "Divine Health", "Immunität gegen Krankheiten"),
        PaladinFeature(4, "Ability Score Improvement / Feat", "2 Attributspunkte oder 1 allgemeines Talent", isAsiOrFeat = true),
        PaladinFeature(5, "Extra Attack (Zusätzlicher Angriff)", "2 Angriffe pro Angriffs-Aktion"),
        PaladinFeature(5, "Faithful Steed (Treues Reittier)", "Find Steed immer vorbereitet, 1x/Lange Rast kostenlos"),
        PaladinFeature(6, "Aura of Protection", "+CHA auf alle Rettungswürfe in 10 ft. Reichweite"),
        PaladinFeature(7, "Subclass Aura (Eid-Aura)", "Eid-spezifische Aura (z. B. Aura of Devotion / Warding / Alacrity)"),
        PaladinFeature(8, "Ability Score Improvement / Feat", "2 Attributspunkte oder 1 allgemeines Talent", isAsiOrFeat = true),
        PaladinFeature(9, "Abjure Foes", "Channel Divinity: Mehrere Feinde in 60 ft. verängstigen & verlangsamen"),
        PaladinFeature(10, "Aura of Courage", "Immunität gegen den Zustand 'Verängstigt' (Frightened) in 10 ft."),
        PaladinFeature(11, "Radiant Strikes", "+1d8 Radiant-Schaden auf alle Nahkampfwaffenangriffe"),
        PaladinFeature(12, "Ability Score Improvement / Feat", "2 Attributspunkte oder 1 allgemeines Talent", isAsiOrFeat = true),
        PaladinFeature(14, "Restoring Touch", "Lay on Hands (5 HP) kuriert Blinded, Charmed, Deafened, Frightened, Paralyzed, Stunned"),
        PaladinFeature(15, "Subclass Feature", "Eid-spezifisches Stufe-15-Merkmal"),
        PaladinFeature(16, "Ability Score Improvement / Feat", "2 Attributspunkte oder 1 allgemeines Talent", isAsiOrFeat = true),
        PaladinFeature(18, "Aura Expansion", "Reichweite aller Auren vergrößert sich von 10 ft. auf 30 ft."),
        PaladinFeature(19, "Epic Boon Feat", "Episches Stufe-19-Talent oder ASI", isAsiOrFeat = true),
        PaladinFeature(20, "Sacred Oath Avatar", "Göttliche 1/Tag Verwandlungs-Form deiner Unterklasse")
    )

    fun getFeaturesForLevel(level: Int): List<PaladinFeature> {
        return classFeatures.filter { it.level == level }
    }

    fun getAllFeaturesUpToLevel(level: Int): List<PaladinFeature> {
        return classFeatures.filter { it.level <= level }
    }

    fun getAuraRadiusFt(level: Int): Int {
        return if (level >= 18) 30 else 10
    }

    fun hasExtraAttack(level: Int): Boolean = level >= 5

    fun hasAuraOfProtection(level: Int): Boolean = level >= 6

    fun hasAuraOfCourage(level: Int): Boolean = level >= 10

    fun hasRadiantStrikes(level: Int): Boolean = level >= 11

    fun hasRestoringTouch(level: Int): Boolean = level >= 14
}
