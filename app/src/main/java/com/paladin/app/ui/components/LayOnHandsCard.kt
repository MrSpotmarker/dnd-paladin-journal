package com.paladin.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LayOnHandsCard(
    remaining: Int,
    maxPool: Int,
    onUse: (Int) -> Unit,
    onCureCondition: () -> Unit,
    onShowDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { isExpanded = !isExpanded },
                        onLongClick = onShowDetail
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("💚", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "Handauflegen (Lay on Hands)",
                            fontWeight = FontWeight.Bold,
                            color = LayOnHandsGreen,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Bonus-Aktion • 2024 Regel",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$remaining / $maxPool HP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (remaining == 0) HealthRed else LayOnHandsGreen
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Handauflegen einklappen" else "Handauflegen ausklappen",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = BorderDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { if (remaining >= 1) onUse(1) },
                            enabled = remaining >= 1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("1 HP", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { if (remaining >= 5) onUse(5) },
                            enabled = remaining >= 5,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("5 HP", fontSize = 12.sp)
                        }
                        Button(
                            onClick = onCureCondition,
                            enabled = remaining >= 5,
                            colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen),
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Zustand heilen (5 HP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
