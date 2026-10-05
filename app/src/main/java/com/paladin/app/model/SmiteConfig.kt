package com.paladin.app.model

data class SmiteConfig(
    val id: String,
    val name: String,
    val icon: String,
    val baseLevel: Int = 1,
    val baseDiceCount: Int,
    val diceType: String,
    val damageType: String,
    val scalingPerLevel: Int = 1,
    val riderEffect: String,
    val isConcentration: Boolean = false
) {
    fun calculateDiceCount(slotLevel: Int): Int {
        val extra = (slotLevel - baseLevel).coerceAtLeast(0) * scalingPerLevel
        return baseDiceCount + extra
    }

    fun damageBonusString(slotLevel: Int): String {
        return "${calculateDiceCount(slotLevel)}$diceType $damageType"
    }
}

object SmiteRegistry {
    val PALADINS_SMITE = SmiteConfig(
        id = "srd_paladins_smite",
        name = "Paladin's Smite",
        icon = "⚡",
        baseLevel = 1,
        baseDiceCount = 2,
        diceType = "d8",
        damageType = "Radiant",
        scalingPerLevel = 1,
        riderEffect = "Bonus-Aktion unmittelbar nach Treffer (D&D 2024)",
        isConcentration = false
    )

    val KNOWN_SMITES = mapOf(
        "srd_paladins_smite" to PALADINS_SMITE,
        "srd_searing_smite" to SmiteConfig(
            id = "srd_searing_smite",
            name = "Searing Smite",
            icon = "🔥",
            baseLevel = 1,
            baseDiceCount = 1,
            diceType = "d6",
            damageType = "Feuer",
            scalingPerLevel = 1,
            riderEffect = "Brand: Ziel erleidet 1d6 Feuerschaden zu Beginn jedes seiner Züge (CON-Save)",
            isConcentration = true
        ),
        "srd_thunderous_smite" to SmiteConfig(
            id = "srd_thunderous_smite",
            name = "Thunderous Smite",
            icon = "🌩️",
            baseLevel = 1,
            baseDiceCount = 2,
            diceType = "d6",
            damageType = "Donner",
            scalingPerLevel = 1,
            riderEffect = "Donnerschlag: ST-Save oder 10 ft weggestoßen und zu Boden geworfen (Prone)",
            isConcentration = true
        ),
        "srd_wrathful_smite" to SmiteConfig(
            id = "srd_wrathful_smite",
            name = "Wrathful Smite",
            icon = "👻",
            baseLevel = 1,
            baseDiceCount = 1,
            diceType = "d6",
            damageType = "Psychisch",
            scalingPerLevel = 1,
            riderEffect = "Schrecken: WIS-Save oder bis zum Ende des Zaubers verängstigt (Frightened)",
            isConcentration = true
        ),
        "srd_shining_smite" to SmiteConfig(
            id = "srd_shining_smite",
            name = "Shining Smite",
            icon = "✨",
            baseLevel = 2,
            baseDiceCount = 2,
            diceType = "d6",
            damageType = "Radiant",
            scalingPerLevel = 1,
            riderEffect = "Helles Licht (5ft), keine Unsichtbarkeit, CON-Save oder geblendet (Blinded)",
            isConcentration = true
        ),
        "srd_blinding_smite" to SmiteConfig(
            id = "srd_blinding_smite",
            name = "Blinding Smite",
            icon = "☀️",
            baseLevel = 3,
            baseDiceCount = 3,
            diceType = "d8",
            damageType = "Radiant",
            scalingPerLevel = 1,
            riderEffect = "Blendend: CON-Save oder geblendet bis Zauberende",
            isConcentration = true
        ),
        "srd_staggering_smite" to SmiteConfig(
            id = "srd_staggering_smite",
            name = "Staggering Smite",
            icon = "💫",
            baseLevel = 4,
            baseDiceCount = 4,
            diceType = "d6",
            damageType = "Psychisch",
            scalingPerLevel = 1,
            riderEffect = "Erschütternd: WIS-Save oder Nachteil auf Angriffe/Checks & keine Reaktionen",
            isConcentration = true
        ),
        "srd_banishing_smite" to SmiteConfig(
            id = "srd_banishing_smite",
            name = "Banishing Smite",
            icon = "🌀",
            baseLevel = 5,
            baseDiceCount = 5,
            diceType = "d10",
            damageType = "Wucht (Force)",
            scalingPerLevel = 1,
            riderEffect = "Verbannung: Fällt Ziel auf 50 HP oder weniger, wird es verbannt",
            isConcentration = true
        )
    )

    fun resolveAvailableSmites(preparedSpells: List<Spell>): List<SmiteConfig> {
        val result = mutableListOf<SmiteConfig>()
        // Paladin's Smite is always available for Paladins
        result.add(PALADINS_SMITE)

        for (spell in preparedSpells) {
            val key = spell.id.lowercase()
            val known = KNOWN_SMITES[key] ?: KNOWN_SMITES.entries.find {
                key.contains(it.key.removePrefix("srd_"))
            }?.value

            if (known != null) {
                if (known.id != PALADINS_SMITE.id && result.none { it.id == known.id }) {
                    result.add(known)
                }
            } else if (spell.name.contains("smite", ignoreCase = true) || spell.id.contains("smite", ignoreCase = true)) {
                if (result.none { it.id == spell.id }) {
                    result.add(
                        SmiteConfig(
                            id = spell.id,
                            name = spell.name,
                            icon = "⚡",
                            baseLevel = spell.level.coerceAtLeast(1),
                            baseDiceCount = spell.level.coerceAtLeast(1),
                            diceType = "d8",
                            damageType = "Magisch",
                            scalingPerLevel = 1,
                            riderEffect = spell.description.take(100),
                            isConcentration = spell.isConcentration
                        )
                    )
                }
            }
        }
        return result
    }
}
