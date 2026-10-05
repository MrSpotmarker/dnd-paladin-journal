package com.paladin.app.model

import kotlinx.serialization.Serializable

@Serializable
enum class Condition(
    val displayName: String,
    val icon: String,
    val shortSummary: String,
    val description: String
) {
    BLINDED(
        displayName = "Blind",
        icon = "👁️‍🗨️",
        shortSummary = "Keine Sicht, Nachteil auf Angriffe, Angriffe gegen dich haben Vorteil.",
        description = "Du kannst nicht sehen und verfehlst automatisch jeden Wurf, der Sicht erfordert. Angriffswürfe gegen dich haben Vorteil. Deine eigenen Angriffswürfe haben Nachteil."
    ),
    CHARMED(
        displayName = "Bezaubert",
        icon = "💖",
        shortSummary = "Kann Bezauberer nicht angreifen, Bezauberer hat Vorteil bei sozialer Interaktion.",
        description = "Du kannst den Bezauberer nicht angreifen oder mit schädlichen Fähigkeiten ins Ziel nehmen. Der Bezauberer hat Vorteil bei allen Attributswürfen zur sozialen Interaktion mit dir."
    ),
    DEAFENED(
        displayName = "Taub",
        icon = "🔇",
        shortSummary = "Kann nicht hören, automatische Fehlschläge bei Gehör-Würfen.",
        description = "Du kannst nichts hören und verfehlst automatisch jeden Attributswurf, der Gehör erfordert."
    ),
    FRIGHTENED(
        displayName = "Verängstigt",
        icon = "😱",
        shortSummary = "Nachteil bei Würfen solange Quelle sichtbar, kann sich Quelle nicht nähern.",
        description = "Du hast Nachteil auf Attributswürfe und Angriffswürfe, solange die Quelle deiner Furcht in Sichtlinie ist. Du kannst dich der Quelle deiner Furcht nicht freiwillig nähern."
    ),
    GRAPPLED(
        displayName = "Gepackt",
        icon = "🤼",
        shortSummary = "Bewegungsreichweite ist 0, keine Boni auf Bewegung.",
        description = "Deine Bewegungsreichweite ist 0 und du kannst von keinem Bonus auf deine Reichweite profitieren. Der Zustand endet, wenn der Ringende dich loslässt oder du aus der Reichweite gestoßen wirst."
    ),
    INCAPACITATED(
        displayName = "Handlungsunfähig",
        icon = "💫",
        shortSummary = "Keine Aktionen, Reaktionen oder Konzentration möglich.",
        description = "Du kannst weder Aktionen noch Reaktionen ausführen. Deine Konzentration auf Zauber bricht sofort ab."
    ),
    INVISIBLE(
        displayName = "Unsichtbar",
        icon = "👻",
        shortSummary = "Gilt als schwer verschleiert, Vorteil auf eigene Angriffe, Angriffe gegen dich haben Nachteil.",
        description = "Du kannst ohne magische Hilfe nicht gesehen werden. Du hast Vorteil auf deine Angriffswürfe. Angriffe gegen dich haben Nachteil."
    ),
    PARALYZED(
        displayName = "Gelähmt",
        icon = "⚡",
        shortSummary = "Handlungsunfähig, kann sich nicht bewegen, automatische Fehlschläge bei STR/DEX Saves, kritische Treffer aus 5ft.",
        description = "Du bist handlungsunfähig und kannst dich weder bewegen noch sprechen. Du verfehlst automatisch Stärke- und Geschicklichkeits-Rettungswürfe. Angriffe gegen dich haben Vorteil. Jeder Nahkampftreffer innerhalb von 5 ft. gegen dich ist automatisch ein kritischer Treffer!"
    ),
    PETRIFIED(
        displayName = "Versteinert",
        icon = "🗿",
        shortSummary = "In Stein verwandelt, gewicht verzehnfacht, handlungsunfähig, Resistenzen.",
        description = "Du bist in eine feste leblose Substanz (meist Stein) verwandelt. Dein Gewicht verzehnfacht sich. Du bist handlungsunfähig und immun gegen Gift/Krankheit."
    ),
    POISONED(
        displayName = "Vergiftet",
        icon = "🤢",
        shortSummary = "Nachteil auf Angriffswürfe und Attributswürfe.",
        description = "Das Gift in deinen Adern schwächt dich: Du hast Nachteil auf alle Angriffswürfe und Attributswürfe. (Tipp: Mit 'Handauflegen' für 5 HP heilbar!)"
    ),
    PRONE(
        displayName = "Liegend",
        icon = "🧎",
        shortSummary = "Nur Krabbeln möglich, eigene Angriffe mit Nachteil, Angreifer aus 5ft haben Vorteil.",
        description = "Deine einzige Bewegungsoption ist Krabbeln, es sei denn, du stehst auf (kostet die Hälfte deiner Bewegungsrate). Deine eigenen Angriffswürfe haben Nachteil. Nahkampfangriffe innerhalb 5 ft. gegen dich haben Vorteil; Fernkampfangriffe haben Nachteil."
    ),
    RESTRAINED(
        displayName = "Festgesetzt",
        icon = "🕸️",
        shortSummary = "Bewegung 0, eigene Angriffe mit Nachteil, Angriffe gegen dich mit Vorteil, Nachteil auf DEX-Saves.",
        description = "Deine Bewegungsrate wird zu 0. Deine Angriffe haben Nachteil. Angriffe gegen dich haben Vorteil. Du hast Nachteil auf Geschicklichkeits-Rettungswürfe."
    ),
    STUNNED(
        displayName = "Betäubt",
        icon = "🧠",
        shortSummary = "Handlungsunfähig, kann sich nicht bewegen, verfehlt STR/DEX Saves automatisch, Angriffe gegen dich haben Vorteil.",
        description = "Du bist handlungsunfähig, kannst dich nicht bewegen und sprichst nur stotternd. Du verfehlst automatisch Stärke- und Geschicklichkeits-Rettungswürfe. Angriffe gegen dich haben Vorteil."
    ),
    UNCONSCIOUS(
        displayName = "Bewusstlos",
        icon = "💤",
        shortSummary = "Handlungsunfähig, lässt alles fallen, fällt liegend um, kritische Treffer aus 5ft.",
        description = "Du bist handlungsunfähig, lässt Gegenstände fallen und fällst liegend um. Du nimmst deine Umgebung nicht wahr. Angriffe gegen dich haben Vorteil. Jeder Treffer innerhalb von 5 ft. ist ein kritischer Treffer."
    ),
    EXHAUSTED(
        displayName = "Erschöpft",
        icon = "🥵",
        shortSummary = "D&D 2024: -2 pro Stufe auf d20-Würfe, -5 ft. Bewegungsrate pro Stufe.",
        description = "Gemäß D&D 2024 Regeln: Jede Erschöpfungsstufe zieht -2 von all deinen d20-Würfen ab und reduziert dein Bewegungstempo um 5 ft. Bei Stufe 6 stirbt der Charakter."
    )
}
