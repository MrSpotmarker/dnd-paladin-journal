package com.paladin.app.model

import kotlinx.serialization.Serializable

/**
 * Definition of a specific feature provided by a Paladin Subclass (Sacred Oath).
 */
@Serializable
data class SubclassFeature(
    val level: Int,
    val name: String,
    val description: String,
    val mechanicalSummary: String
)

/**
 * The official Paladin Subclasses (Sacred Oaths) in D&D 2024.
 */
@Serializable
enum class PaladinSubclass(
    val id: String,
    val displayName: String,
    val germanTitle: String,
    val summary: String,
    val channelDivinityOptions: List<String>,
    val auraName: String,
    val auraDescription: String,
    val features: List<SubclassFeature>
) {
    DEVOTION(
        id = "devotion",
        displayName = "Oath of Devotion",
        germanTitle = "Eid der Hingabe",
        summary = "Göttliche Gerechtigkeit, Schutz der Schwachen und unerschütterliche Tugend.",
        channelDivinityOptions = listOf("Sacred Weapon", "Turn the Unholy"),
        auraName = "Aura of Devotion (Stufe 7)",
        auraDescription = "Du und Verbündete in deiner Aura können nicht bezaubert (Charmed) werden.",
        features = listOf(
            SubclassFeature(
                level = 3,
                name = "Sacred Weapon (Channel Divinity)",
                description = "Als Bonus-Aktion hüllst du deine Waffe für 10 Minuten in gleißendes Licht. Du addierst deinen CHA-Modifikator zu Angriffswürfen und die Waffe leuchtet.",
                mechanicalSummary = "+CHA zu Angriffswürfen mit der gewählten Waffe (10 Min)"
            ),
            SubclassFeature(
                level = 3,
                name = "Turn the Unholy (Channel Divinity)",
                description = "Als Aktion zwingst du Untote und Unholde in 30 ft. zu einem WIS-Rettungswurf. Bei Fehlschlag sind sie für 1 Minute vertrieben.",
                mechanicalSummary = "Untote & Unholde in 30 ft. vertreiben (WIS-Save)"
            ),
            SubclassFeature(
                level = 7,
                name = "Aura of Devotion",
                description = "Du und Verbündete innerhalb deiner Aura (10 ft., ab Stufe 18: 30 ft.) können nicht verzaubert/befriedet werden (Charmed-Immunität).",
                mechanicalSummary = "Immunität gegen Bezauberung (Charmed) in der Aura"
            ),
            SubclassFeature(
                level = 15,
                name = "Smite of Protection",
                description = "Wirkst du einen Smite-Zauber, erhalten du und Verbündete in 30 ft. halbe Deckung (+2 AC und DEX-Saves) bis zum Beginn deines nächsten Zuges.",
                mechanicalSummary = "+2 AC & DEX-Saves für Verbündete nach Smite"
            ),
            SubclassFeature(
                level = 20,
                name = "Holy Nimbus",
                description = "Als Bonus-Aktion für 10 Minuten: Sonnenlicht (30 ft.), 10 Radiant-Schaden für Feinde, die ihren Zug dort beginnen, und Vorteil auf Rettungswürfe gegen Zauber von Untoten/Unholden.",
                mechanicalSummary = "10 Min: 10 Radiant Dmg Aura & Spell Save Vorteil"
            )
        )
    ),

    VENGEANCE(
        id = "vengeance",
        displayName = "Oath of Vengeance",
        germanTitle = "Eid der Rache",
        summary = "Unnachgiebige Bestrafung des Bösen und gnadenlose Jagd auf Todfeinde.",
        channelDivinityOptions = listOf("Vow of Enmity", "Abjure Enemy"),
        auraName = "Relentless Avenger (Stufe 7)",
        auraDescription = "Triffst du ein Ziel mit einem Gelegenheitsangriff, kannst du dich um bis zur Hälfte deines Tempos bewegen, ohne Gelegenheitsangriffe zu provozieren.",
        features = listOf(
            SubclassFeature(
                level = 3,
                name = "Vow of Enmity (Channel Divinity)",
                description = "Als Bonus-Aktion wählst du eine Kreatur in 30 ft. Du hast Vorteil auf alle Angriffswürfe gegen diese Kreatur für 1 Minute.",
                mechanicalSummary = "Vorteil auf alle Angriffe gegen dein gewähltes Ziel (1 Min)"
            ),
            SubclassFeature(
                level = 7,
                name = "Relentless Avenger",
                description = "Wenn du ein Ziel mit einem Gelegenheitsangriff triffst, kannst du dich sofort bis zur Hälfte deiner Bewegungsrate bewegen, ohne Gelegenheitsangriffe zu provozieren.",
                mechanicalSummary = "Reaktions-Bewegung nach Gelegenheitsangriff"
            ),
            SubclassFeature(
                level = 15,
                name = "Soul of Vengeance",
                description = "Macht eine Kreatur unter deinem Vow of Enmity einen Angriff, kannst du deine Reaktion nutzen, um einen Nahkampfwaffenangriff gegen sie auszuführen.",
                mechanicalSummary = "Reaktionsangriff wenn markierter Feind angreift"
            ),
            SubclassFeature(
                level = 20,
                name = "Avenging Angel",
                description = "Für 10 Minuten wachsen dir Flügel: Flugtempo 60 ft. und eine Schreckens-Aura (Feinde in 30 ft. müssen WIS-Save ablegen oder sind verängstigt).",
                mechanicalSummary = "10 Min: 60 ft. Fliegen + Schreckens-Aura"
            )
        )
    ),

    ANCIENTS(
        id = "ancients",
        displayName = "Oath of the Ancients",
        germanTitle = "Eid der Uralten",
        summary = "Bewahrung des Lebens, der Freude, des Lichts und der Schönheit der Welt.",
        channelDivinityOptions = listOf("Nature's Wrath", "Turn the Faithless"),
        auraName = "Aura of Warding (Stufe 7)",
        auraDescription = "Du und Verbündete in deiner Aura haben Resistenz gegen Schaden durch Zauber.",
        features = listOf(
            SubclassFeature(
                level = 3,
                name = "Nature's Wrath (Channel Divinity)",
                description = "Ranken schießen hervor und halten einen Gegner in 30 ft. fest (Restrained bei misslungenem STR- oder DEX-Save).",
                mechanicalSummary = "Gegner in 30 ft. festsetzen (Restrained)"
            ),
            SubclassFeature(
                level = 7,
                name = "Aura of Warding",
                description = "Uralte Magie schützt dich: Du und deine Verbündeten in der Aura erhalten Resistenz gegen nekrotischen, psychischen und strahlenden Schaden.",
                mechanicalSummary = "Resistenz gegen magische Schadensarten in der Aura"
            ),
            SubclassFeature(
                level = 15,
                name = "Undying Sentinel",
                description = "Fällst du auf 0 HP, fällst du stattdessen 1x pro Langer Rast auf 1 HP. Zudem kannst du nicht magisch altern.",
                mechanicalSummary = "1x/Tag Überleben bei 0 HP (auf 1 HP gesetzt)"
            ),
            SubclassFeature(
                level = 20,
                name = "Elder Champion",
                description = "Für 1 Minute verwandelst du dich in eine Naturgewalt: 10 HP Regeneration pro Runde, Zauber als Bonus-Aktion wirken und Feinde haben Nachteil auf Saves gegen deine Paladin-Zauber.",
                mechanicalSummary = "1 Min: 10 HP/Runde Regeneration & Bonus-Action Spells"
            )
        )
    ),

    GLORY(
        id = "glory",
        displayName = "Oath of Glory",
        germanTitle = "Eid des Ruhms",
        summary = "Heldentum, athletische Höchstleistungen und die Inspiration von Gefährten.",
        channelDivinityOptions = listOf("Peerless Athlete", "Inspiring Smite"),
        auraName = "Aura of Alacrity (Stufe 7)",
        auraDescription = "Deine Schrittgeschwindigkeit und die deiner Verbündeten in der Aura erhöht sich um +10 Fuß.",
        features = listOf(
            SubclassFeature(
                level = 3,
                name = "Peerless Athlete (Channel Divinity)",
                description = "Für 1 Stunde hast du Vorteil auf Athletik & Akrobatik, kannst mehr tragen und dein Weitsprung/Hochsprung erhöht sich um 10 ft.",
                mechanicalSummary = "Vorteil auf Athletik/Akrobatik & Sprungweite +10 ft"
            ),
            SubclassFeature(
                level = 3,
                name = "Inspiring Smite (Channel Divinity)",
                description = "Direkt nach einem Divine Smite verteilst du 2d8 + Paladinstufe temporäre Trefferpunkte auf dich und Verbündete in 30 ft.",
                mechanicalSummary = "Verteilt 2d8 + Stufe Temp HP nach Smite"
            ),
            SubclassFeature(
                level = 7,
                name = "Aura of Alacrity",
                description = "Deine Bewegungsrate erhöht sich um +10 ft. Verbündete, die ihren Zug in deiner Aura beginnen, erhalten bis zu ihrem Zugende ebenfalls +10 ft.",
                mechanicalSummary = "+10 ft. Bewegungsrate für dich & Verbündete"
            ),
            SubclassFeature(
                level = 15,
                name = "Mind Over Mettle",
                description = "Verfehlt dich oder einen Verbündeten ein Angriff, kannst du deine Reaktion nutzen, um die RK für diesen Angriff um deinen CHA-Modifikator zu erhöhen.",
                mechanicalSummary = "Reaktions-Schutzwurf gegen knappe Treffer"
            ),
            SubclassFeature(
                level = 20,
                name = "Living Legend",
                description = "Für 10 Minuten: Vorteil auf alle Charisma-Würfe, 1x pro Runde misslungenen Angriff in Treffer umwandeln, und 1x pro Rast als Reaktion einen Rettungswurf wiederholen.",
                mechanicalSummary = "10 Min: CHA-Vorteil, garantierte Treffer & Rerolls"
            )
        )
    );

    companion object {
        fun fromOathString(oathStr: String?): PaladinSubclass? {
            if (oathStr.isNullOrBlank()) return null
            val clean = oathStr.trim()
            return entries.find {
                it.displayName.equals(clean, ignoreCase = true) ||
                it.name.equals(clean, ignoreCase = true) ||
                it.id.equals(clean, ignoreCase = true) ||
                clean.contains(it.id, ignoreCase = true) ||
                clean.contains(it.displayName.replace("Oath of ", "").replace("the ", ""), ignoreCase = true) ||
                clean.contains(it.germanTitle, ignoreCase = true)
            }
        }
    }
}
