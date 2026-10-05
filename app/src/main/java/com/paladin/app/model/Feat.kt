package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class FeatCategory(val displayName: String) {
    ORIGIN("Herkunftstalente (Stufe 1)"),
    FIGHTING_STYLE("Kampfstil-Talente (Stufe 2+)"),
    GENERAL("Allgemeine Talente (Stufe 4+)"),
    CUSTOM("Eigene Fähigkeiten & Hausregeln")
}

data class FeatDefinition(
    val id: String,
    val name: String,
    val category: FeatCategory,
    val description: String,
    val mechanicalBenefit: String
)

object FeatCatalog {
    val allFeats = listOf(
        // Herkunftstalente (Origin Feats)
        FeatDefinition(
            id = "alert",
            name = "Alert (Wachsam)",
            category = FeatCategory.ORIGIN,
            description = "Stets auf der Hut vor Gefahren.",
            mechanicalBenefit = "+Übungsbonus (+PB) auf Initiative; Initiative mit bereitwilligem Verbündeten tauschen."
        ),
        FeatDefinition(
            id = "savage_attacker",
            name = "Savage Attacker (Brutaler Angreifer)",
            category = FeatCategory.ORIGIN,
            description = "Entfessle vernichtende Waffenschläge.",
            mechanicalBenefit = "1x pro Zug beim Waffentreffer Schadenswürfel 2x werfen und das höhere Ergebnis wählen."
        ),
        FeatDefinition(
            id = "tough",
            name = "Tough (Zäh)",
            category = FeatCategory.ORIGIN,
            description = "Außergewöhnliche Zähigkeit und Lebenskraft.",
            mechanicalBenefit = "+2 maximale Trefferpunkte pro Stufe (+2 HP pro Level)."
        ),
        FeatDefinition(
            id = "magic_initiate",
            name = "Magic Initiate (Magie-Initiat)",
            category = FeatCategory.ORIGIN,
            description = "Grundlegende Magie erlernt.",
            mechanicalBenefit = "2 Zaubertricks (Cantrips) + 1x Zauber 1. Grades frei pro Tag."
        ),
        FeatDefinition(
            id = "lucky",
            name = "Lucky (Glückspilz)",
            category = FeatCategory.ORIGIN,
            description = "Unerklärliches Glück in brenzligen Situationen.",
            mechanicalBenefit = "Glückspunkte in Höhe von PB für Vorteil/Nachteil auf Würfe."
        ),

        // Kampfstil-Talente (Fighting Style Feats - Stufe 2+)
        FeatDefinition(
            id = "defense",
            name = "Defense (Verteidigung)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Meisterhafte Rüstungsabwehr.",
            mechanicalBenefit = "+1 Rüstungsklasse (AC), solange eine Rüstung getragen wird."
        ),
        FeatDefinition(
            id = "dueling",
            name = "Dueling (Duellieren)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Gesteigerter Schaden mit Einhandwaffen.",
            mechanicalBenefit = "+2 Schaden bei Angriffen mit einer einhändigen Nahkampfwaffe (keine Zweitwaffe)."
        ),
        FeatDefinition(
            id = "great_weapon_fighting",
            name = "Great Weapon Fighting",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Wuchtige Schwünge mit zweihändigen Waffen.",
            mechanicalBenefit = "1er und 2er auf Schadenswürfeln zweihändiger Waffen zählen als 3."
        ),
        FeatDefinition(
            id = "protection",
            name = "Protection (Beschützer)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Schütze deine Gefährten mit deinem Schild.",
            mechanicalBenefit = "Reaktion: Feind erhält Nachteil auf Angriff gegen nahen Verbündeten (Schild nötig)."
        ),
        FeatDefinition(
            id = "interception",
            name = "Interception (Abfangen)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Schläge für deine Verbündeten abfangen.",
            mechanicalBenefit = "Reaktion: Schaden an nahem Verbündeten um 1d10 + PB verringern (Schild oder Waffe nötig)."
        ),
        FeatDefinition(
            id = "blind_fighting",
            name = "Blind Fighting (Blinder Kampf)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Kämpfen ohne Augenlicht.",
            mechanicalBenefit = "Blindsicht (Blindsight) im Umkreis von 10 Fuß."
        ),
        FeatDefinition(
            id = "blessed_warrior",
            name = "Blessed Warrior (Gesegneter Krieger)",
            category = FeatCategory.FIGHTING_STYLE,
            description = "Mit göttlichen Wundern gesegnet.",
            mechanicalBenefit = "2 Zaubertricks (Cantrips) der Klerikerliste (z. B. Guidance, Sacred Flame)."
        ),

        // Allgemeine Talente (General Feats - Stufe 4+)
        FeatDefinition(
            id = "war_caster",
            name = "War Caster (Kriegsmagier)",
            category = FeatCategory.GENERAL,
            description = "Unerschütterliche Konzentration und Zauberkraft mitten im Getümmel.",
            mechanicalBenefit = "Vorteil auf Konzentrationswürfe; Zaubern mit Waffe & Schild in den Händen; Zauber als Gelegenheitsangriff wirken."
        ),
        FeatDefinition(
            id = "sentinel",
            name = "Sentinel (Wachsamer Wächter)",
            category = FeatCategory.GENERAL,
            description = "Hält Feinde unerbittlich fest und schützt Verbündete.",
            mechanicalBenefit = "Gelegenheitsangriff setzt gegnerisches Tempo auf 0 ft; Gelegenheitsangriff selbst bei 'Rückzug'; Reaktion gegen Angreifer naher Verbündeter."
        ),
        FeatDefinition(
            id = "polearm_master",
            name = "Polearm Master (Stangenwaffenmeister)",
            category = FeatCategory.GENERAL,
            description = "Tödliche Reichweite und schnelle Schläge mit Hellebarde, Pike oder Speer.",
            mechanicalBenefit = "Bonus-Aktion: Schlag mit dem Waffenende (1d4 Wucht); Gelegenheitsangriff, wenn eine Kreatur in deine Reichweite tritt."
        ),
        FeatDefinition(
            id = "heavy_armor_master",
            name = "Heavy Armor Master",
            category = FeatCategory.GENERAL,
            description = "Gepanzert wie eine lebende Festung.",
            mechanicalBenefit = "Reduziert erlittenen Hieb-, Stich- und Wuchtschaden (sowohl magisch als auch nicht-magisch) um deinen Übungsbonus (+PB)!"
        ),
        FeatDefinition(
            id = "great_weapon_master",
            name = "Great Weapon Master",
            category = FeatCategory.GENERAL,
            description = "Verheerende Wucht mit Zweihandwaffen.",
            mechanicalBenefit = "Bei Kritischem Treffer oder Kill: 1 Bonus-Angriff; Angriffe mit Zweihandwaffen erhalten +PB zusätzlichen Waffenschaden."
        ),
        FeatDefinition(
            id = "shield_master",
            name = "Shield Master (Schildmeister)",
            category = FeatCategory.GENERAL,
            description = "Verwendet den Schild sowohl als Waffe als auch als rettende Deckung.",
            mechanicalBenefit = "Bonus-Aktion nach Angriff: Ziel zu Boden werfen (Prone); Schild-Bonus auf DEX-Rettungswürfe; Reaktion: 0 Schaden statt halber Schaden."
        ),
        FeatDefinition(
            id = "inspiring_leader",
            name = "Inspiring Leader",
            category = FeatCategory.GENERAL,
            description = "Inspirierende Worte vor der Schlacht stärken die Moral.",
            mechanicalBenefit = "Nach Rast: Bis zu 6 Gefährten erhalten temporäre HP in Höhe von Charakterstufe + CHA-Modifikator."
        ),
        FeatDefinition(
            id = "mage_slayer",
            name = "Mage Slayer (Magiertöter)",
            category = FeatCategory.GENERAL,
            description = "Gezieltes Vorgehen gegen feindliche Zauberwirker.",
            mechanicalBenefit = "Vorteil auf Saves gegen Zauber im Umkreis von 5 ft; Treffer zwingt Zauberer zu Nachteil auf Konzentrationswürfe; 1x/Tag Save automatisch bestehen."
        )
    )

    fun getStandardFeatLimit(level: Int): Int {
        // PHB 2024 & Setup:
        // Stufe 1: 2 (Herkunftstalent + Menschen-Bonus)
        // Stufe 2: +1 (Kampfstil-Talent) = 3
        // Stufe 3: +1 (Unterklassen-Eid / Spezialfähigkeit) = 4
        // Stufe 4, 8, 12, 16, 19: je +1 Feat / ASI
        var limit = 2
        if (level >= 2) limit += 1
        if (level >= 3) limit += 1
        if (level >= 4) limit += 1
        if (level >= 8) limit += 1
        if (level >= 12) limit += 1
        if (level >= 16) limit += 1
        if (level >= 19) limit += 1
        return limit
    }
}
