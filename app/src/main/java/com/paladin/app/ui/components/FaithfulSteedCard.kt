package com.paladin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paladin.app.model.DetailItem
import com.paladin.app.model.SpellSlotState
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SmiteBlue
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.SurfaceCardMuted
import com.paladin.app.ui.theme.TextMuted
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FaithfulSteedCard(
    isSummoned: Boolean,
    freeUsageUsed: Boolean,
    currentHp: Int,
    maxHp: Int,
    spellAttackBonus: Int,
    spellSaveDc: Int,
    creatureType: String,
    isSpecialUsed: Boolean,
    spellSlots: List<SpellSlotState>,
    onSummonFree: () -> Unit,
    onSummonWithSlot: (Int) -> Unit,
    onDismissSteed: () -> Unit,
    onUpdateHp: (current: Int, maxOverride: Int?) -> Unit,
    onSelectCreatureType: (String) -> Unit,
    onToggleSpecialUsed: () -> Unit,
    onShowDetail: (DetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showEditHpDialog by remember { mutableStateOf(false) }

    val level2Slot = spellSlots.firstOrNull { it.level == 2 }
    val hasLevel2Slot = level2Slot != null && level2Slot.remainingSlots > 0

    val typeDisplayName = when (creatureType.lowercase()) {
        "fey", "fee" -> "Fee (Fey)"
        "fiend", "unhold" -> "Unhold (Fiend)"
        else -> "Himmlisch (Celestial)"
    }

    val damageTypeName = when (creatureType.lowercase()) {
        "fey", "fee" -> "Psychisch"
        "fiend", "unhold" -> "Nekrotisch"
        else -> "Gleißend"
    }

    val typeIcon = when (creatureType.lowercase()) {
        "fey", "fee" -> "🦋"
        "fiend", "unhold" -> "🔥"
        else -> "🌟"
    }

    val hpFraction = if (maxHp > 0) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 1f
    val hpBarColor = when {
        hpFraction > 0.5f -> ProficiencyGreen
        hpFraction > 0.2f -> PaladinGoldBright
        else -> HealthRed
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header (Clickable to collapse/expand)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Text("🐴", fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Treues Reittier (Faithful Steed)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGold
                            )
                        }
                        Text(
                            text = "Stufe 5 Klassenmerkmal • Find Steed",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isSummoned) {
                        OutlinedButton(
                            onClick = onDismissSteed,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthRed),
                            border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Entlassen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            color = SurfaceCardHighlight,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, BorderDark)
                        ) {
                            Text(
                                text = "Nicht aktiv",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "▲" else "▼",
                            color = PaladinGoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Collapsed Compact Summary Row
            if (!isExpanded && isSummoned) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHighlight.copy(alpha = 0.6f))
                        .clickable { isExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️ $currentHp / $maxHp HP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = hpBarColor
                    )
                    Text("🛡️ 13 RK", fontSize = 11.sp, color = TextPrimary)
                    Text("💨 60 ft", fontSize = 11.sp, color = TextPrimary)
                    Text("$typeIcon $typeDisplayName", fontSize = 11.sp, color = PaladinGoldBright)
                    Text("⏳ Dauerhaft", fontSize = 11.sp, color = TextSecondary)
                }
            }

            // Expanded Full Content
            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 1. Creature Type Selector Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Wesenheit des Reittiers (Typ):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Triple("Celestial", "🌟 Himmlisch", "Gleißend & Heilende Berührung"),
                                Triple("Fey", "🦋 Fee", "Psychisch & Schritt der Fey"),
                                Triple("Fiend", "🔥 Unhold", "Nekrotisch & Finsterer Blick")
                            ).forEach { (typeKey, label) ->
                                val isSelected = creatureType.equals(typeKey, ignoreCase = true)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .combinedClickable(
                                            onClick = { onSelectCreatureType(typeKey) },
                                            onLongClick = {
                                                onShowDetail(
                                                    DetailItem.FeatureInfo(
                                                        title = "$label (Otherworldly Steed)",
                                                        subtitle = "D&D 2024 Find Steed Typ",
                                                        badge = typeKey,
                                                        icon = label.take(2).trim(),
                                                        description = "Beim Rufen des Treuen Reittiers bestimmst du seine kosmische Natur: Himmlisch, Fee oder Unhold. Dies bestimmt die Schadensart des Nahkampf-Hiebs sowie die spezielle Bonus-Aktion des Reittiers.",
                                                        keyProperties = listOf(
                                                            "Typ" to typeKey,
                                                            "Schadensart" to when (typeKey) {
                                                                "Celestial" -> "Gleißend (Radiant)"
                                                                "Fey" -> "Psychisch (Psychic)"
                                                                else -> "Nekrotisch (Necrotic)"
                                                            },
                                                            "Spezial-Bonusaktion" to when (typeKey) {
                                                                "Celestial" -> "Heilende Berührung (2d8+2 HP)"
                                                                "Fey" -> "Schritt der Fey (60 ft Teleport)"
                                                                else -> "Finsterer Blick (WIS-Save oder Frightened)"
                                                            }
                                                        )
                                                    )
                                                )
                                            }
                                        ),
                                    color = if (isSelected) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) PaladinGold else BorderDark
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PaladinGoldBright else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Stat Block & HP with Edit Button
                    Surface(
                        color = SurfaceCardHighlight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderDark.copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // HP Bar & Edit Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("❤️", fontSize = 14.sp)
                                    Text(
                                        text = "Trefferpunkte:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "$currentHp / $maxHp HP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = hpBarColor
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showEditHpDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.7f))
                                ) {
                                    Text("✏️ HP anpassen", fontSize = 10.sp, color = PaladinGoldBright)
                                }
                            }

                            // Linear Progress Bar
                            LinearProgressIndicator(
                                progress = { hpFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = hpBarColor,
                                trackColor = DarkNavyBackground
                            )

                            // Quick Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                HeaderStatItem(label = "Rüstung (RK)", value = "13", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Tempo", value = "60 ft", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Zauber-SG", value = "$spellSaveDc", modifier = Modifier.weight(1f))
                                HeaderStatItem(label = "Hieb-Treffer", value = "+$spellAttackBonus", modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Steed Duration & Rules Banner
                    Surface(
                        color = SurfaceCardHighlight.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderDark.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⏳", fontSize = 14.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = "Dauer: Dauerhaft (kein Zeitlimit)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaladinGoldBright
                                )
                                Text(
                                    text = "Bleibt auch über kurze & lange Rasten an deiner Seite, bis es auf 0 HP fällt oder du es als Bonus-Aktion entlässt.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 3. Combat Actions & Mechanics Section
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚔️ Aktionen & Taktik",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGold
                            )
                            Text(
                                text = "ℹ️ Antippen für Details",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        // Action 1: Otherworldly Slam (Nahkampf-Angriff)
                        CombatActionCard(
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
                        CombatActionCard(
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
                        CombatActionCard(
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

                    // 4. Action Buttons (Summon / Dismiss)
                    if (!isSummoned) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSummonFree,
                                enabled = !freeUsageUsed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PaladinGold,
                                    contentColor = DarkNavyBackground
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (!freeUsageUsed) "✨ Gratis rufen (1/Rast)" else "Gratis verbraucht",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onSummonWithSlot(2) },
                                enabled = hasLevel2Slot,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SmiteBlue),
                                border = BorderStroke(1.dp, SmiteBlue.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Slot Stufe 2 rufen",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onDismissSteed,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthRed),
                            border = BorderStroke(1.dp, HealthRed.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reittier entlassen / zurücksenden", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showEditHpDialog) {
        EditSteedHpDialog(
            currentHp = currentHp,
            maxHp = maxHp,
            onDismiss = { showEditHpDialog = false },
            onSave = { newCurrent, newMaxOverride ->
                onUpdateHp(newCurrent, newMaxOverride)
                showEditHpDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CombatActionCard(
    icon: String,
    title: String,
    subtitle: String? = null,
    summary: String,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCardHighlight,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onClick
            )
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(icon, fontSize = 14.sp)
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PaladinGoldBright
                )
            }

            Text(
                text = summary,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun EditSteedHpDialog(
    currentHp: Int,
    maxHp: Int,
    onDismiss: () -> Unit,
    onSave: (current: Int, maxOverride: Int?) -> Unit
) {
    var editCurrentHp by remember { mutableStateOf(currentHp) }
    var editMaxHp by remember { mutableStateOf(maxHp) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, PaladinGold.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🐴", fontSize = 20.sp)
                    Text(
                        text = "Reittier Trefferpunkte (HP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                }

                // Current HP Stepper
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Aktuelle HP:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { editCurrentHp = (editCurrentHp - 5).coerceAtLeast(0) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("-5", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { editCurrentHp = (editCurrentHp - 1).coerceAtLeast(0) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("-1", fontSize = 12.sp)
                            }
                        }

                        Text(
                            text = "$editCurrentHp HP",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGoldBright
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { editCurrentHp = (editCurrentHp + 1).coerceAtMost(editMaxHp) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("+1", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { editCurrentHp = (editCurrentHp + 5).coerceAtMost(editMaxHp) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("+5", fontSize = 12.sp)
                            }
                        }
                    }

                    Button(
                        onClick = { editCurrentHp = editMaxHp },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = ProficiencyGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💚 Vollständig heilen ($editMaxHp HP)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Max HP Adjustment
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Maximale HP (Standard D&D 2024: $maxHp):", fontSize = 12.sp, color = TextSecondary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editMaxHp.toString(),
                            onValueChange = { str ->
                                str.toIntOrNull()?.let { editMaxHp = it.coerceAtLeast(1) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedButton(
                            onClick = {
                                editMaxHp = maxHp
                                if (editCurrentHp > maxHp) editCurrentHp = maxHp
                            }
                        ) {
                            Text("Reset", fontSize = 11.sp)
                        }
                    }
                }

                // Dialog Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Abbrechen", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(editCurrentHp, if (editMaxHp != maxHp) editMaxHp else null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
                    ) {
                        Text("Speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
