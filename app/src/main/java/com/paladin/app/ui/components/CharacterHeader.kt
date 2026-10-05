package com.paladin.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.CalculatedStats
import com.paladin.app.model.CharacterSheet
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun CharacterHeader(
    character: CharacterSheet,
    stats: CalculatedStats,
    onOpenOverrides: () -> Unit,
    onOpenFeats: () -> Unit,
    modifier: Modifier = Modifier,
    onModifyInspiration: (Int) -> Unit = {},
    onShowInspirationDetail: () -> Unit = {},
    onShowSpeciesDetail: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Profilbild + Name/Subtitle ────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    CharacterProfileAvatar(
                        customProfileImagePath = character.customProfileImagePath,
                        customFullImagePath = character.customFullImagePath,
                        size = 56
                    )

                    Column {
                        Text(
                            text = character.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val subtitleParts = buildList {
                            add("Stufe ${character.level} Paladin")
                            add(character.species.displayName)
                            character.oath?.let { add(it) }
                        }
                        Text(
                            text = subtitleParts.joinToString(" • "),
                            fontSize = 13.sp,
                            color = PaladinGold,
                            modifier = Modifier.clickable { onShowSpeciesDetail() }
                        )
                    }
                }

                // ── DM-Override Button ────────────────────────────────────
                IconButton(
                    onClick = onOpenOverrides,
                    modifier = Modifier
                        .background(
                            if (stats.hasDmOverrides) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "DM Overrides",
                        tint = if (stats.hasDmOverrides) PaladinGold else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats Row: AC, PB, Speed (Bewegungsreichweite), Hit Dice (gleichmäßig verteilt)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeaderStatItem(
                    label = "AC",
                    value = "${stats.armorClass}",
                    modifier = Modifier.weight(1f)
                )
                HeaderStatItem(
                    label = "PB",
                    value = "+${stats.proficiencyBonus}",
                    modifier = Modifier.weight(1f)
                )
                HeaderStatItem(
                    label = "Bewegung",
                    value = "${stats.speedFt} ft",
                    modifier = Modifier.weight(1f)
                )
                HeaderStatItem(
                    label = "Trefferwürfel",
                    value = "${stats.remainingHitDice}/${stats.maxHitDice} d10",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Heroic Inspiration Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PaladinGold.copy(alpha = 0.12f))
                    .border(1.dp, PaladinGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onShowInspirationDetail() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎲", fontSize = 14.sp)
                    Column {
                        Text(
                            text = "Heroische Inspiration",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGoldBright
                        )
                        Text(
                            text = "W20-Wiederholung (${character.species.displayName}: 1 bei Rast)",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onModifyInspiration(-1) },
                        enabled = character.heroicInspirations > 0,
                        modifier = Modifier.size(28.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SurfaceCardHighlight,
                            contentColor = TextPrimary
                        )
                    ) {
                        Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (character.heroicInspirations > 0) PaladinGold else SurfaceCardHighlight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${character.heroicInspirations}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (character.heroicInspirations > 0) DarkNavyBackground else TextSecondary
                        )
                    }

                    FilledTonalButton(
                        onClick = { onModifyInspiration(1) },
                        modifier = Modifier.size(28.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PaladinGold.copy(alpha = 0.35f),
                            contentColor = PaladinGoldBright
                        )
                    ) {
                        Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtle Compact Feats Bar
            val featLimit = com.paladin.app.model.FeatCatalog.getStandardFeatLimit(character.level)
            val isOver = character.feats.size > featLimit
            val featsText = if (character.feats.isEmpty()) {
                "Keine Talente aktiv • Tippe zum Auswählen"
            } else {
                character.feats.joinToString(" • ") { it.substringBefore(" (") }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceCardHighlight.copy(alpha = 0.5f))
                    .clickable { onOpenFeats() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("✨", fontSize = 11.sp)
                    Text(
                        text = featsText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PaladinGold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = "${character.feats.size}/$featLimit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOver) HealthRed else ProficiencyGreen
                    )
                    Text("✏️", fontSize = 10.sp)
                }
            }

            if (stats.hasDmOverrides) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚡ Aktive DM Overrides/Hausregeln",
                    fontSize = 11.sp,
                    color = PaladinGoldBright,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HeaderStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCardHighlight.copy(alpha = 0.6f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 6.dp)
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = PaladinGold,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 9.5.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
