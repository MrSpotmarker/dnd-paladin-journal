package com.paladin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.model.*
import com.paladin.app.ui.theme.*

@Composable
fun DetailInfoDialog(
    item: DetailItem,
    onDismiss: () -> Unit,
    onEditItem: ((Item) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(PaladinGold.copy(alpha = 0.6f))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header based on DetailItem type
                when (item) {
                    is DetailItem.SpellInfo -> SpellHeader(item.spell)
                    is DetailItem.FeatInfo -> FeatHeader(item.feat)
                    is DetailItem.ItemInfo -> ItemHeader(item.item)
                    is DetailItem.FeatureInfo -> FeatureHeader(item)
                    is DetailItem.AbilityInfo -> AbilityHeader(item)
                }

                HorizontalDivider(
                    color = BorderDark,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (item) {
                        is DetailItem.SpellInfo -> SpellBody(item.spell)
                        is DetailItem.FeatInfo -> FeatBody(item.feat)
                        is DetailItem.ItemInfo -> ItemBody(item.item)
                        is DetailItem.FeatureInfo -> FeatureBody(item)
                        is DetailItem.AbilityInfo -> AbilityBody(item)
                    }
                }

                HorizontalDivider(
                    color = BorderDark,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (item is DetailItem.ItemInfo && onEditItem != null) Arrangement.SpaceBetween else Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item is DetailItem.ItemInfo && onEditItem != null) {
                        OutlinedButton(
                            onClick = { onEditItem(item.item) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f))
                        ) {
                            Text("✏️ Anpassen", color = PaladinGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaladinGold,
                            contentColor = DarkNavyBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Schließen", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SPELL DETAILS
// -------------------------------------------------------------
@Composable
private fun SpellHeader(spell: Spell) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("✨", fontSize = 28.sp)
        Column {
            Text(
                text = spell.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = if (spell.level == 0) "Zaubertrick (Cantrip)" else "Grad ${spell.level} ${spell.school.displayName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (spell.isConcentration) {
                    BadgeChip(text = "Konzentration", color = SpellSlotPurple)
                }
                if (spell.isRitual) {
                    BadgeChip(text = "Ritual", color = SmiteBlue)
                }
            }
        }
    }
}

@Composable
private fun SpellBody(spell: Spell) {
    // Quick-Stats Grid
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCardHighlight)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PropertyRow(label = "Zauberzeit", value = spell.castingTime)
        PropertyRow(label = "Reichweite", value = spell.range)
        PropertyRow(label = "Wirkungsdauer", value = spell.duration)

        val components = mutableListOf<String>()
        if (spell.verbal) components.add("V (Verbal)")
        if (spell.somatic) components.add("S (Geste)")
        if (spell.material) {
            val matText = if (spell.materialDescription.isNotBlank()) "M (${spell.materialDescription})" else "M (Material)"
            components.add(matText)
        }
        PropertyRow(label = "Komponenten", value = components.joinToString(", "))
        PropertyRow(label = "Regelwerk", value = spell.source)
    }

    // Full Description Text
    Text(
        text = "Beschreibung & Zauberwirkung:",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = PaladinGold
    )
    Text(
        text = spell.description,
        fontSize = 13.sp,
        color = TextPrimary,
        lineHeight = 20.sp
    )
}

// -------------------------------------------------------------
// 2. FEAT DETAILS
// -------------------------------------------------------------
@Composable
private fun FeatHeader(feat: FeatDefinition) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🛡️", fontSize = 28.sp)
        Column {
            Text(
                text = feat.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = feat.category.displayName,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FeatBody(feat: FeatDefinition) {
    // Highlight Box for Mechanical Benefit
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PaladinGold.copy(alpha = 0.12f))
            .border(1.dp, PaladinGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "⚔️ Mechanischer Vorteil & Boni:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = feat.mechanicalBenefit,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 18.sp
            )
        }
    }

    // Fluff / Background Description
    Text(
        text = "Hintergrund & Beschreibung:",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = PaladinGold
    )
    Text(
        text = feat.description,
        fontSize = 13.sp,
        color = TextPrimary,
        lineHeight = 19.sp
    )
}

// -------------------------------------------------------------
// 3. ITEM DETAILS
// -------------------------------------------------------------
@Composable
private fun ItemHeader(item: Item) {
    val icon = when (item.type) {
        ItemType.WEAPON -> "⚔️"
        ItemType.ARMOR, ItemType.SHIELD -> "🛡️"
        ItemType.POTION -> "🧪"
        ItemType.CONSUMABLE -> "🌿"
        ItemType.MAGIC_ITEM -> "🔮"
        ItemType.GEAR -> "🎒"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(icon, fontSize = 28.sp)
        Column {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = when (item.type) {
                        ItemType.WEAPON -> "Waffe"
                        ItemType.ARMOR -> "Rüstung (${item.armorType?.name ?: "Allgemein"})"
                        ItemType.SHIELD -> "Schild"
                        ItemType.MAGIC_ITEM -> "Magischer Gegenstand"
                        ItemType.POTION -> "Trank"
                        ItemType.CONSUMABLE -> "Pflanze / Verbrauchsgut"
                        ItemType.GEAR -> "Ausrüstung"
                    },
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (item.isEquipped) {
                    BadgeChip(text = "Ausgerüstet", color = ProficiencyGreen)
                }
                if (item.requiresAttunement) {
                    BadgeChip(
                        text = if (item.isAttuned) "Eingestimmt" else "Einstimmung nötig",
                        color = if (item.isAttuned) SmiteBlue else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemBody(item: Item) {
    // Quick-Stats Grid
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCardHighlight)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PropertyRow(label = "Gewicht", value = "${item.weightLbs} lbs")
        PropertyRow(label = "Wert / Kosten", value = item.cost)
        PropertyRow(label = "Menge", value = "${item.quantity}x")

        // Weapon specific properties
        if (item.type == ItemType.WEAPON) {
            PropertyRow(label = "Schaden", value = "${item.damageDice} ${item.damageType}")
            if (item.isVersatile && item.versatileDamageDice != null) {
                PropertyRow(label = "Vielseitig (Versatile)", value = "${item.versatileDamageDice} (zweihändig)")
            }
            val weaponProps = mutableListOf<String>()
            if (item.isFinesse) weaponProps.add("Finesse")
            if (item.isHeavy) weaponProps.add("Schwer")
            if (item.isLight) weaponProps.add("Leicht")
            if (item.isReach) weaponProps.add("Reichweite")
            if (item.isTwoHanded) weaponProps.add("Zweihändig")
            if (item.isVersatile) weaponProps.add("Vielseitig")
            if (weaponProps.isNotEmpty()) {
                PropertyRow(label = "Eigenschaften", value = weaponProps.joinToString(", "))
            }
        }

        // Armor specific properties
        if (item.type == ItemType.ARMOR || item.type == ItemType.SHIELD) {
            PropertyRow(label = "Rüstungsklasse (AC)", value = if (item.type == ItemType.SHIELD) "+${item.baseAc} AC" else "${item.baseAc} AC")
            if (item.stealthDisadvantage) {
                PropertyRow(label = "Heimlichkeit", value = "Nachteil (Disadvantage)")
            }
            if (item.minStrength > 0) {
                PropertyRow(label = "Mindest-Stärke", value = "${item.minStrength} STR")
            }
        }
    }

    // Weapon Mastery Box
    if (item.mastery != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PaladinGold.copy(alpha = 0.12f))
                .border(1.dp, PaladinGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "⚔️ Waffenmeisterschaft (Mastery: ${item.mastery.propertyName}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = item.mastery.description,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }

    // Item Effects
    if (item.effects.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SmiteBlue.copy(alpha = 0.12f))
                .border(1.dp, SmiteBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "✨ Magische Effekte & Boni:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SmiteBlue
            )
            item.effects.forEach { effect ->
                val effectText = when (effect) {
                    is ItemEffect.AcFlat -> "Setzt RK auf ${effect.ac}"
                    is ItemEffect.AcBonus -> "+${effect.bonus} Rüstungsklasse"
                    is ItemEffect.AbilityBonus -> "+${effect.bonus} auf ${effect.ability.displayName}"
                    is ItemEffect.AbilityOverride -> "Setzt ${effect.ability.displayName} auf ${effect.score}"
                    is ItemEffect.AttackBonus -> "+${effect.bonus} auf Angriffswürfe"
                    is ItemEffect.DamageBonus -> "+${effect.bonus} auf Waffenschaden"
                    is ItemEffect.SavingThrowBonus -> "+${effect.bonus} auf Rettungswürfe"
                }
                Text(text = "• $effectText", fontSize = 12.sp, color = TextPrimary)
            }
        }
    }

    // Description
    if (item.description.isNotBlank()) {
        Text(
            text = "Beschreibung:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )
        Text(
            text = item.description,
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 19.sp
        )
    }
}

// -------------------------------------------------------------
// 4. FEATURE / SPECIAL ABILITY DETAILS
// -------------------------------------------------------------
@Composable
private fun FeatureHeader(feature: DetailItem.FeatureInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(feature.icon, fontSize = 28.sp)
        Column {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                if (feature.subtitle != null) {
                    Text(
                        text = feature.subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                if (feature.badge != null) {
                    BadgeChip(text = feature.badge, color = PaladinGold)
                }
            }
        }
    }
}

@Composable
private fun FeatureBody(feature: DetailItem.FeatureInfo) {
    if (feature.keyProperties.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceCardHighlight)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            feature.keyProperties.forEach { (key, value) ->
                PropertyRow(label = key, value = value)
            }
        }
    }

    if (feature.mechanicalBenefits.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PaladinGold.copy(alpha = 0.12f))
                .border(1.dp, PaladinGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "⚔️ Mechanische Wirkungsweise (2024 Regeln):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            feature.mechanicalBenefits.forEach { benefit ->
                Text(
                    text = "• $benefit",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }

    Text(
        text = "Regelerklärung & Details:",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = PaladinGold
    )
    Text(
        text = feature.description,
        fontSize = 13.sp,
        color = TextPrimary,
        lineHeight = 19.sp
    )
}

// -------------------------------------------------------------
// 5. ABILITY DETAILS
// -------------------------------------------------------------
@Composable
private fun AbilityHeader(ability: DetailItem.AbilityInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎲", fontSize = 28.sp)
        Column {
            Text(
                text = "${ability.ability.displayName} (${ability.ability.abbreviation})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = "Attributswert: ${ability.score} • Modifikator: ${Ability.formatModifier(ability.modifier)}",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun AbilityBody(ability: DetailItem.AbilityInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCardHighlight)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PropertyRow(label = "Basiswert", value = "${ability.score}")
        PropertyRow(label = "Attributs-Modifikator", value = Ability.formatModifier(ability.modifier))
        PropertyRow(
            label = "Rettungswurf (Saving Throw)",
            value = "${if (ability.saveBonus >= 0) "+${ability.saveBonus}" else "${ability.saveBonus}"} ${if (ability.isSaveProficient) "(Geübt / Proficient ✨)" else "(Ungeübt)"}"
        )
    }

    // Associated Skills
    val associatedSkills = Skill.entries.filter { it.ability == ability.ability }
    if (associatedSkills.isNotEmpty()) {
        Text(
            text = "Zugeordnete Fertigkeiten:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkNavyBackground.copy(alpha = 0.5f))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            associatedSkills.forEach { skill ->
                Text(
                    text = "• ${skill.displayName}",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }
    }

    // Rules Text
    Text(
        text = "Bedeutung im Regelwerk (D&D 2024):",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = PaladinGold
    )
    val ruleText = when (ability.ability) {
        Ability.STRENGTH -> "Beeinflusst Nahkampfangriffe, Nahkampfschaden, Athletik-Würfe, Traglast (STR x 15 lbs) und Mindeststärke für schwere Rüstungen."
        Ability.DEXTERITY -> "Beeinflusst Initiative, Rüstungsklasse (bei leichter/mittlerer Rüstung), Fernkampf- und Finesse-Angriffe, Akrobatik, Fingerfertigkeit und Heimlichkeit."
        Ability.CONSTITUTION -> "Beeinflusst Trefferpunkte (+CON pro Stufe), Hit Dice Heilung und Konzentrations-Rettungswürfe bei erlittenem Schaden."
        Ability.INTELLIGENCE -> "Beeinflusst Arkankunde, Geschichte, Nachforschungen, Naturkunde und Religions-Checks."
        Ability.WISDOM -> "Beeinflusst Wahrnehmung, Motiv erkennen, Tierkunde, Heilkunde, Überlebenskunst und den Schutz gegen mentale Effekte (Bezauberung, Furcht)."
        Ability.CHARISMA -> "Hauptattribut des Paladins! Bestimmt deinen Zauberangriffsbonus (+CHA + PB), deinen Spell Save DC (8 + PB + CHA), Handauflegen-Effizienz, Aura des Schutzes (Stufe 6+) und soziale Fertigkeiten."
    }
    Text(
        text = ruleText,
        fontSize = 13.sp,
        color = TextPrimary,
        lineHeight = 19.sp
    )
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
private fun PropertyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun BadgeChip(text: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
