package com.paladin.app.model

import kotlin.math.max

object ChannelDivinityDetails {

    fun getGeneralChannelDivinityDetail(
        oath: String?,
        maxUses: Int,
        chaMod: Int,
        spellDc: Int = 13
    ): DetailItem.FeatureInfo {
        val oathName = oath ?: "Heiliger Eid (Sacred Oath)"
        return DetailItem.FeatureInfo(
            title = "Göttliche Macht (Channel Divinity)",
            subtitle = "Klassenmerkmal Paladin (Stufe 3) • $oathName",
            badge = "$maxUses Ladung${if (maxUses > 1) "en" else ""}",
            icon = "⚡",
            keyProperties = listOf(
                "Ladungen" to "$maxUses Nutzungen (D&D 2024 Regeln)",
                "Kurze Rast" to "Regeneriert 1 verbrauchte Ladung",
                "Lange Rast" to "Regeneriert alle Ladungen vollständig",
                "Rückgewinnung" to "Harness Divine Power (Bonus-Aktion, 1 Slot zurück)"
            ),
            mechanicalBenefits = listOf(
                "Sacred Weapon (Hingabe): Aktion. Deine Waffe wird 10 Minuten lang magisch, strahlt Licht (20ft) ab und addiert deinen Charisma-Modifikator (+${max(1, chaMod)}) auf alle Angriffswürfe.",
                "Turn the Unholy (Hingabe): Aktion. Alle Untoten und Unholde im Umkreis von 30 Fuß müssen einen Weisheits-Rettungswurf (DC $spellDc) bestehen oder fliehen.",
                "Vow of Enmity (Rache): Bonus-Aktion. Du wählst eine Kreatur innerhalb von 30 Fuß. Du hast 1 Minute lang VORTEIL auf alle Angriffswürfe gegen dieses Ziel.",
                "Harness Divine Power: Bonus-Aktion. Wandelt 1 Channel Divinity Ladung in einen verbrauchten Zauberslot 1. Grades um."
            ),
            description = "Als Paladin kanalisierst du göttliche Energie direkt von deiner Gottheit oder deinem heiligen Eid. Gemäß den D&D 2024 Regeln regenerierst du nun bereits bei jeder Kurzen Rast 1 verbrauchte Ladung zurück!"
        )
    }

    fun getSacredWeaponDetail(chaMod: Int): DetailItem.FeatureInfo {
        val sacredBonus = max(1, chaMod)
        return DetailItem.FeatureInfo(
            title = "Heilige Waffe (Sacred Weapon)",
            subtitle = "Eid der Hingabe (Oath of Devotion) • Channel Divinity",
            badge = "Aktion • 10 Min.",
            icon = "🌟",
            keyProperties = listOf(
                "Aktionstyp" to "Aktion (oder Bonus-Aktion)",
                "Dauer" to "10 Minuten (oder bis weggesteckt/bewusstlos)",
                "Angriffsbonus" to "+$sacredBonus auf alle Angriffswürfe",
                "Licht" to "Helles Licht (20 ft), Dämmriges Licht (weitere 20 ft)",
                "Magisch" to "Waffe gilt als magisch für Überwindung von Resistenzen"
            ),
            mechanicalBenefits = listOf(
                "Du erfüllst eine Waffe, die du führst, mit positiver göttlicher Energie.",
                "10 Minuten lang addierst du deinen Charisma-Modifikator (+$sacredBonus) zu deinen Angriffswürfen mit dieser Waffe.",
                "Die Waffe strahlt helles Licht im Radius von 20 Fuß und dämmriges Licht für weitere 20 Fuß aus.",
                "Falls die Waffe nicht bereits magisch ist, gilt sie für die Wirkungsdauer als magisch.",
                "Der Effekt endet vorzeitig, wenn du die Waffe fallen lässt, wegsteckst oder bewusstlos wirst."
            ),
            description = "Deine Waffe erstrahlt in gleißendem heiligem Glanz. Die Reinheit deines Eids leitet jeden deiner Schläge mit übernatürlicher Präzision."
        )
    }

    fun getVowOfEnmityDetail(): DetailItem.FeatureInfo {
        return DetailItem.FeatureInfo(
            title = "Gelübde der Feindschaft (Vow of Enmity)",
            subtitle = "Eid der Rache (Oath of Vengeance) • Channel Divinity",
            badge = "Bonus-Aktion • 1 Min.",
            icon = "🎯",
            keyProperties = listOf(
                "Aktionstyp" to "Bonus-Aktion",
                "Reichweite" to "30 Fuß (10 Meter)",
                "Dauer" to "1 Minute (oder bis Ziel 0 HP hat)",
                "Effekt" to "Vorteil auf alle Angriffswürfe gegen das Ziel"
            ),
            mechanicalBenefits = listOf(
                "Als Bonus-Aktion sprichst du ein Gelübde der Feindschaft gegen eine Kreatur aus, die du innerhalb von 30 Fuß sehen kannst.",
                "Du erhältst 1 Minute lang VORTEIL auf alle Angriffswürfe gegen diese Kreatur.",
                "In den D&D 2024 Regeln: Wenn die markierte Kreatur stirbt oder auf 0 TP fällt, kannst du das Gelübde als Bonus-Aktion auf ein neues Ziel übertragen!"
            ),
            description = "Du fokussierst deinen unerbittlichen Zorn auf einen einzelnen Feind. Jeder deiner Hiebe sucht zielsicher seine Schwachstellen."
        )
    }

    fun getHarnessDivinePowerDetail(): DetailItem.FeatureInfo {
        return DetailItem.FeatureInfo(
            title = "Göttliche Kraft bündeln (Harness Divine Power)",
            subtitle = "Klassenmerkmal Paladin • Channel Divinity",
            badge = "Bonus-Aktion",
            icon = "⚡",
            keyProperties = listOf(
                "Aktionstyp" to "Bonus-Aktion",
                "Verbrauch" to "1 Channel Divinity Ladung",
                "Effekt" to "Regeneriert 1 verbrauchten Zauberslot",
                "Regel" to "D&D 2024 / Tasha's Cauldron of Everything"
            ),
            mechanicalBenefits = listOf(
                "Du berührst dein heiliges Symbol, sprichst ein kurzes Gebet und wendest eine Nutzung deiner Göttlichen Macht (Channel Divinity) auf.",
                "Du stellst sofort einen verbrauchten Zauberslot 1. Grades (oder höher gemäß Stufe) wieder her."
            ),
            description = "Du wandelst die rohe göttliche Gunst deines Glaubens in pure magische Energie um, um deine Zauberkraft im Gefecht aufzufrischen."
        )
    }

    fun getTurnTheUnholyDetail(dc: Int): DetailItem.FeatureInfo {
        return DetailItem.FeatureInfo(
            title = "Untote vertreiben (Turn the Unholy)",
            subtitle = "Eid der Hingabe (Oath of Devotion) • Channel Divinity",
            badge = "Aktion • DC $dc",
            icon = "☀️",
            keyProperties = listOf(
                "Aktionstyp" to "Aktion",
                "Reichweite" to "30 Fuß (alle Untoten & Unholde)",
                "Rettungswurf" to "Weisheit DC $dc",
                "Dauer" to "1 Minute (oder bis Ziel Schaden nimmt)"
            ),
            mechanicalBenefits = listOf(
                "Du streckst dein heiliges Symbol empor und sprichst ein heiliges Gebet gegen das Böse.",
                "Jeder Unhold oder Untote innerhalb von 30 Fuß, der dich sehen oder hören kann, muss einen Weisheits-Rettungswurf (DC $dc) bestehen.",
                "Bei Misslingen ist die Kreatur 1 Minute lang vertrieben (Turned) und muss ihre Aktionen nutzen, um so weit wie möglich von dir wegzulaufen."
            ),
            description = "Göttliche Ehrfurcht strahlt aus dir heraus. Kreaturen der Finsternis können deine heilige Präsenz nicht ertragen und fliehen in Panik."
        )
    }

    fun getDivineSenseDetail(): DetailItem.FeatureInfo {
        return DetailItem.FeatureInfo(
            title = "Göttliches Gespür (Divine Sense)",
            subtitle = "Klassenmerkmal Paladin (Stufe 1)",
            badge = "Bonus-Aktion",
            icon = "👁️",
            keyProperties = listOf(
                "Aktionstyp" to "Bonus-Aktion (D&D 2024)",
                "Reichweite" to "60 Fuß",
                "Dauer" to "Bis zum Ende deines nächsten Zuges"
            ),
            mechanicalBenefits = listOf(
                "Du nimmst den genauen Standort von Himmlischen (Celestials), Unholden (Fiends) und Untoten (Undead) innerhalb von 60 Fuß wahr, die nicht hinter voller Deckung sind.",
                "Du spürst die Präsenz von geweihtem oder entweihtem Boden (wie durch den Zauber Hallow)."
            ),
            description = "Die Gegenwart starken Bösen registriert sich in deinen Sinnen wie ein übler Geruch, und mächtiges Gutes klingt wie himmlische Musik in deinen Ohren."
        )
    }
}
