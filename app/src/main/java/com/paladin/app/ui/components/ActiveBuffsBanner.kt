package com.paladin.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.paladin.app.model.ActiveBuffInfo
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SmiteBlue
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActiveBuffsBanner(
    activeBuffs: List<ActiveBuffInfo>,
    onDismissBuff: (String) -> Unit,
    modifier: Modifier = Modifier,
    onShowDetail: (ActiveBuffInfo) -> Unit = {}
) {
    if (activeBuffs.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, PaladinGold.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚡", fontSize = 14.sp)
                    Text(
                        text = "Aktive Zauber & Effekte",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGoldBright
                    )
                }

                val hasConcentration = activeBuffs.any { it.isConcentration }
                if (hasConcentration) {
                    Surface(
                        color = SmiteBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, SmiteBlue)
                    ) {
                        Text(
                            text = "🔮 Konzentration aktiv",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmiteBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderDark)

            // Chips for each active buff
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                activeBuffs.forEach { buff ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardHighlight)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .combinedClickable(
                                    onClick = { onShowDetail(buff) },
                                    onLongClick = { onShowDetail(buff) }
                                )
                                .padding(end = 4.dp)
                        ) {
                            Text(buff.icon, fontSize = 16.sp)
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = buff.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (buff.isConcentration) {
                                        Text(
                                            text = "(K)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SmiteBlue
                                        )
                                    }
                                }
                                Text(
                                    text = "${buff.effectSummary} • Info antippen",
                                    fontSize = 11.sp,
                                    color = PaladinGold
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDismissBuff(buff.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effekt beenden",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
