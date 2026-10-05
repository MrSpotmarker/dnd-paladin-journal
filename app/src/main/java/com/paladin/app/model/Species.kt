package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class Species(
    val displayName: String,
    val baseSpeedFt: Int = 30,
    val traitsDescription: String,
    val defaultInspirationsOnLongRest: Int = 0
) {
    HUMAN(
        displayName = "Mensch",
        baseSpeedFt = 30,
        traitsDescription = "Einfallsreich (Heroische Inspiration bei jeder Langen Rast), Geschickt (1 extra Fertigkeit), Vielseitig (1 extra Herkunftstalent)",
        defaultInspirationsOnLongRest = 1
    ),
    ELF(
        displayName = "Elf",
        baseSpeedFt = 30,
        traitsDescription = "Dunkelsicht (60 ft), Elfen-Abstammung, Schärfte Sinne, Feenblut, Trance",
        defaultInspirationsOnLongRest = 0
    ),
    DWARF(
        displayName = "Zwerg",
        baseSpeedFt = 30,
        traitsDescription = "Dunkelsicht (120 ft), Zwergen-Widerstandskraft, Zwergen-Zähigkeit (+1 HP/Stufe), Steingespür",
        defaultInspirationsOnLongRest = 0
    ),
    HALFLING(
        displayName = "Halbling",
        baseSpeedFt = 30,
        traitsDescription = "Mutig (Vorteil gegen Furcht), Halbling-Gewandtheit, Glücklich (1er neu würfeln), Natürlich verstohlen",
        defaultInspirationsOnLongRest = 0
    ),
    GNOME(
        displayName = "Gnom",
        baseSpeedFt = 30,
        traitsDescription = "Dunkelsicht (60 ft), Gnomische Schläue (Vorteil auf INT/WIS/CHA Rettungswürfe gegen Magie)",
        defaultInspirationsOnLongRest = 0
    ),
    DRAGONBORN(
        displayName = "Drachenblütiger",
        baseSpeedFt = 30,
        traitsDescription = "Drakonische Abstammung, Odemwaffe, Schadensresistenz, Dunkelsicht (60 ft), Flug (ab Stufe 5)",
        defaultInspirationsOnLongRest = 0
    ),
    TIEFLING(
        displayName = "Tiefling",
        baseSpeedFt = 30,
        traitsDescription = "Dunkelsicht (60 ft), Teuflisches Erbe (Thaumaturgy & Zauber), Feuer-/Giftresistenz",
        defaultInspirationsOnLongRest = 0
    ),
    ORC(
        displayName = "Ork",
        baseSpeedFt = 30,
        traitsDescription = "Adrenalinschub (Bonusaktion Sprinten + Temp HP), Dunkelsicht (120 ft), Unerbittliche Ausdauer",
        defaultInspirationsOnLongRest = 0
    ),
    GOLIATH(
        displayName = "Goliath",
        baseSpeedFt = 35,
        traitsDescription = "Riesenerbe (z. B. Wolke, Frost, Feuer), Riesige Gestalt (ab Stufe 5), Mächtiger Körperbau (Tempo 35 ft)",
        defaultInspirationsOnLongRest = 0
    ),
    AASIMAR(
        displayName = "Aasimar",
        baseSpeedFt = 30,
        traitsDescription = "Himmlische Enthüllung (ab Stufe 3), Himmlischer Widerstand (Nekrotisch/Gleißend), Dunkelsicht (60 ft), Heilende Hände",
        defaultInspirationsOnLongRest = 0
    ),
    CUSTOM(
        displayName = "Eigene Spezies",
        baseSpeedFt = 30,
        traitsDescription = "Benutzerdefinierte Spezies / Hausregel",
        defaultInspirationsOnLongRest = 0
    );
}
