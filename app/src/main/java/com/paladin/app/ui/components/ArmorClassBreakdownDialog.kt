package com.paladin.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.model.CalculatedStats
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.ChaunteaGreenBright
import com.paladin.app.ui.theme.ChaunteaGreenContainer
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

/**
 * Modal dialog displaying the detailed breakdown of the character's Armor Class (AC).
 * Opened via long press on the AC panel in CharacterHeader.
 */
@Composable
fun ArmorClassBreakdownDialog(
    stats: CalculatedStats,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // 1. Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🛡️", fontSize = 28.sp)
                    Column {
                        Text(
                            text = "Rüstungsklasse (AC)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Aktueller Gesamtwert: ${stats.armorClass} RK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PaladinGoldBright
                        )
                    }
                }

                HorizontalDivider(
                    color = BorderDark,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 2. Scrollable Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Zusammensetzung der Rüstungsklasse:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Breakdown List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCardHighlight)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (stats.armorClassElements.isNotEmpty()) {
                            stats.armorClassElements.forEachIndexed { index, element ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(element.icon, fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = element.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (element.detail.isNotBlank()) {
                                                Text(
                                                    text = element.detail,
                                                    fontSize = 11.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        color = ChaunteaGreenContainer,
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, ChaunteaGreenBright.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = element.value,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PaladinGoldBright,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (index < stats.armorClassElements.size - 1) {
                                    HorizontalDivider(color = BorderDark.copy(alpha = 0.5f))
                                }
                            }
                        } else {
                            Text(
                                text = stats.armorClassBreakdown.ifBlank { "Basiswert: 10 + DEX" },
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    // 3. Calculation Summary Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PaladinGold.copy(alpha = 0.12f))
                            .border(1.dp, PaladinGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Berechnete Gesamtsumme:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaladinGold
                                )
                                Text(
                                    text = stats.armorClassBreakdown,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }
                            Text(
                                text = "${stats.armorClass} RK",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PaladinGoldBright
                            )
                        }
                    }

                    // 4. Rule explanation
                    Text(
                        text = "D&D 2024 Regelhinweis:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text(
                        text = "Die Rüstungsklasse bestimmt den Schwierigkeitsgrad für eingehende Angriffe. Ein Angreifer muss mit seinem W20-Angriffswurf mindestens ${stats.armorClass} erreichen, um einen Treffer zu landen.",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }

                HorizontalDivider(
                    color = BorderDark,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 5. Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PaladinGold.copy(alpha = 0.2f),
                            contentColor = PaladinGoldBright
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Schließen", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
