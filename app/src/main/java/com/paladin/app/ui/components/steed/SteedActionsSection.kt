package com.paladin.app.ui.components.steed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.DetailItem
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.SurfaceCardMuted
import com.paladin.app.ui.theme.TextMuted
import com.paladin.app.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SteedActionsSection(
    creatureType: String,
    typeDisplayName: String,
    damageTypeName: String,
    spellAttackBonus: Int,
    spellSaveDc: Int,
    isSpecialUsed: Boolean,
    onToggleSpecialUsed: () -> Unit,
    onShowDetail: (DetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "⚔️ Aktionen & Taktik",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )

        // Action 1: Otherworldly Slam (Nahkampf-Angriff)
        SteedCombatActionCard(
            icon = "💥",
            title = "Übernatürlicher Hieb (Otherworldly Slam)",
            subtitle = "+$spellAttackBonus zum Treffen • 1d8 + 2 $damageTypeName (Nahkampf, 5 ft)",
            summary = "Nahkampf-Angriff. Verwendet deinen Zauberangriffsbonus (+$spellAttackBonus) und richtet $damageTypeName-Schaden an.",
            onClick = {
                onShowDetail(
                    DetailItem.FeatureInfo(
                        title = "Übernatürlicher Hieb (Otherworldly Slam)",
                        subtitle = "Nahkampf-Angriff des Treuen Reittiers",
                        badge = "Aktion (Reittier)",
                        icon = "💥",
                        description = "Das Treue Reittier attackiert ein Ziel in Nahkampfreichweite mit übernatürlicher Wucht. Die Schadensart richtet sich nach seiner gewählten Wesenheit ($damageTypeName).",
                        keyProperties = listOf(
                            "Angriffsart" to "Nahkampf-Angriff",
                            "Reichweite" to "5 Fuß",
                            "Angriffsbonus" to "+$spellAttackBonus (Dein Paladin-Zauberangriffsbonus: PB + CHA)",
                            "Schaden" to "1d8 + 2 ($damageTypeName)",
                            "Zaubergrad-Skalierung" to "1d8 + Zaubergrad (bei Grad 2: 1d8+2)"
                        ),
                        mechanicalBenefits = listOf(
                            "Greift eigenständig an, wenn du nicht aufgesessen bist oder wenn es unabhängig agiert.",
                            "Profitiert von deinem hohen Zauberangriffsbonus."
                        )
                    )
                )
            }
        )

        // Action 2: Controlled Mount Tactics (Reiten & Bewegungs-Aktionen)
        SteedCombatActionCard(
            icon = "🐎",
            title = "Reittier-Taktik (Controlled Mount)",
            subtitle = "60 ft Bewegung • Spurt | Rückzug | Ausweichen",
            summary = "Als kontrolliertes Reittier teilt es deine Initiative. In deinem Zug führt es Spurt (120 ft), Rückzug (keine OA) oder Ausweichen aus – ohne deine Aktion zu kosten!",
            onClick = {
                onShowDetail(
                    DetailItem.FeatureInfo(
                        title = "Reiten im Kampf (Controlled Mount)",
                        subtitle = "D&D 2024 Reittier-Regeln",
                        badge = "Reittier-Aktion",
                        icon = "🐎",
                        description = "Während du auf dem Treuen Reittier reitest, fungiert es als kontrolliertes Reittier. Es bewegt sich in deinem Zug und hat seine eigene Aktion, die deine eigene Aktion nicht verbraucht!",
                        keyProperties = listOf(
                            "Initiative" to "Teilt deine Initiative (agiert direkt mit dir)",
                            "Basis-Geschwindigkeit" to "60 Fuß (ab Zaubergrad 4+ zusätzlich 60 Fuß Fliegen)",
                            "Erlaubte Aktionen" to "Spurt (Dash), Rückzug (Disengage), Ausweichen (Dodge)"
                        ),
                        mechanicalBenefits = listOf(
                            "🏃 Spurt (Dash): Verdoppelt das Reittier-Tempo für den Zug auf sagenhafte 120 Fuß.",
                            "💨 Rückzug (Disengage): Bewegung provoziert keine Gelegenheitsangriffe gegen dich oder dein Reittier!",
                            "🛡️ Ausweichen (Dodge): Angriffe gegen das Reittier haben Nachteil; Geschicklichkeits-Rettungswürfe haben Vorteil.",
                            "⚔️ Eigene Aktion frei: Du kannst deine volle Aktion (z. B. 2x Extra Attack) und Bonus-Aktion (z. B. Smite) für eigene Angriffe nutzen!"
                        )
                    )
                )
            }
        )

        // Action 3: Type Special Bonus Action (1x per Long Rest)
        val specialTitle = when (creatureType.lowercase()) {
            "fey", "fee" -> "Schritt der Fey (Fey Step)"
            "fiend", "unhold" -> "Finsterer Blick (Fell Glare)"
            else -> "Heilende Berührung (Healing Touch)"
        }
        val specialIcon = when (creatureType.lowercase()) {
            "fey", "fee" -> "🌀"
            "fiend", "unhold" -> "👁️"
            else -> "💚"
        }
        val specialEffectSummary = when (creatureType.lowercase()) {
            "fey", "fee" -> "Teleportiert Reittier & Reiter bis zu 60 ft in ein freies Feld (keine Gelegenheitsangriffe)."
            "fiend", "unhold" -> "Ziel in 60 ft muss WIS-Rettungswurf (SG $spellSaveDc) schaffen oder ist bis zum Ende deines nächsten Zugs Verängstigt (Frightened)."
            else -> "Berühre eine Kreatur in 5 ft, die sofort 2d8 + 2 HP regeneriert."
        }

        Surface(
            color = SurfaceCardHighlight,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (!isSpecialUsed) PaladinGold.copy(alpha = 0.6f) else BorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onShowDetail(
                            DetailItem.FeatureInfo(
                                title = "$specialTitle ($typeDisplayName)",
                                subtitle = "Spezial-Bonusaktion des Reittiers",
                                badge = "Bonus-Aktion (1x / Lange Rast)",
                                icon = specialIcon,
                                description = "Jede Reittier-Form verfügt über eine mächtige Spezialkraft, die es als Bonus-Aktion einsetzen kann. Regeneriert sich vollständig nach einer Langen Rast.",
                                keyProperties = listOf(
                                    "Aktionstyp" to "Bonus-Aktion",
                                    "Erholung" to "1x pro Lange Rast",
                                    "Typ" to typeDisplayName,
                                    "Effekt-Wert" to when (creatureType.lowercase()) {
                                        "fey", "fee" -> "60 Fuß Teleport"
                                        "fiend", "unhold" -> "WIS-Rettungswurf gegen SG $spellSaveDc"
                                        else -> "2d8 + 2 HP Heilung (2d8 + Zaubergrad)"
                                    }
                                ),
                                mechanicalBenefits = listOf(
                                    specialEffectSummary,
                                    "Kann während des Ritts als Bonus-Aktion des Reittiers aktiviert werden."
                                )
                            )
                        )
                    },
                    onLongClick = {
                        onShowDetail(
                            DetailItem.FeatureInfo(
                                title = "$specialTitle ($typeDisplayName)",
                                subtitle = "Spezial-Bonusaktion des Reittiers",
                                badge = "Bonus-Aktion (1x / Lange Rast)",
                                icon = specialIcon,
                                description = specialEffectSummary,
                                keyProperties = listOf(
                                    "Erholung" to "Lange Rast"
                                )
                            )
                        )
                    }
                )
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(specialIcon, fontSize = 14.sp)
                        Text(
                            text = specialTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGoldBright
                        )
                    }

                    Surface(
                        color = if (!isSpecialUsed) PaladinGold.copy(alpha = 0.2f) else SurfaceCardMuted,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (!isSpecialUsed) PaladinGold else BorderDark),
                        modifier = Modifier.clickable { onToggleSpecialUsed() }
                    ) {
                        Text(
                            text = if (!isSpecialUsed) "✨ Bereit (1x)" else "❌ Verbraucht",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isSpecialUsed) PaladinGoldBright else TextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Bonus-Aktion • 1x pro Lange Rast",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PaladinGoldBright
                )

                Text(
                    text = specialEffectSummary,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // Action 4: Life Bond (Lebensband)
        SteedCombatActionCard(
            icon = "🔗",
            title = "Lebensband (Life Bond)",
            subtitle = "Passive Eigenschaft • 5 ft Reichweite",
            summary = "Wenn du durch einen Zauber ab Grad 1 HP heilst (z. B. Cure Wounds, Aura of Vitality), heilt dein Reittier dieselbe Anzahl an HP (wenn in 5 ft)!",
            onClick = {
                onShowDetail(
                    DetailItem.FeatureInfo(
                        title = "Lebensband (Life Bond)",
                        subtitle = "D&D 2024 Reittier-Eigenschaft",
                        badge = "Passive Eigenschaft",
                        icon = "🔗",
                        description = "Zwischen dir und deinem Treuen Reittier besteht eine unzerreißbare magische Lebensbindung.",
                        keyProperties = listOf(
                            "Reichweite" to "5 Fuß",
                            "Auslöser" to "Heilung durch einen Zauber ab Grad 1 (z. B. Cure Wounds, Aura of Vitality)",
                            "Effekt" to "Reittier regeneriert dieselbe Anzahl Trefferpunkte"
                        ),
                        mechanicalBenefits = listOf(
                            "Maximale Heileffizienz: Ein einziger Heilzauber heilt dich und dein treues Ross gleichzeitig!",
                            "Gilt nur für Zauber (nicht für Handauflegen/Lay on Hands)."
                        )
                    )
                )
            }
        )
    }
}
