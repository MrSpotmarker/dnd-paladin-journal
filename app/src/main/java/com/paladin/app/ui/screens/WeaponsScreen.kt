package com.paladin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.AttackInfo
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

@Composable
fun WeaponsScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.calculatedStats.collectAsState()
    val character by viewModel.character.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Waffen & Angriffe (2024)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )

        // Weapon Mastery Notice
        Surface(
            color = SurfaceCardHighlight,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "⚔️ Deine Waffenmeisterschaften (Masteries):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PaladinGold
                )
                Text(
                    text = character.masteredWeaponNames.joinToString(", "),
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        if (stats.attacks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Keine Waffe angelegt.\nGehe ins Inventar, um eine Waffe auszurüsten!",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stats.attacks) { attack ->
                    AttackCard(attack = attack)
                }
            }
        }

        // Paladin Smite reminder card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "✨ Paladin's Smite (2024 Regel)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SmiteBlue
                )
                Text(
                    text = "Wird als Bonus-Aktion direkt nach dem Treffen ausgelöst (kostet 1 Zauberplatz, +2d8 Radiant + 1d8 pro höherem Slot).",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun AttackCard(attack: AttackInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = attack.item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (attack.isMasteryActive && attack.masteryEffect != null) {
                    Surface(
                        color = PaladinGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold)
                    ) {
                        Text(
                            text = "Mastery: ${attack.masteryEffect.propertyName}",
                            color = PaladinGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+${attack.attackBonus}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PaladinGold
                    )
                    Text(text = "Angriffsbonus", fontSize = 11.sp, color = TextSecondary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = attack.damageString,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Schaden (${attack.damageType})",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            if (attack.isMasteryActive && attack.masteryEffect != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SurfaceCardHighlight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ${attack.masteryEffect.description}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (attack.activeBuffNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    attack.activeBuffNotes.forEach { note ->
                        Surface(
                            color = PaladinGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = note,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaladinGoldBright,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
