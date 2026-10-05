package com.paladin.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Condition
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.LayOnHandsGreen
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun ConditionsSection(
    activeConditions: Set<Condition>,
    onToggleCondition: (Condition) -> Unit,
    onRemoveCondition: (Condition) -> Unit,
    modifier: Modifier = Modifier,
    onCurePoisonLayOnHands: () -> Unit = {},
    remainingLayOnHands: Int = 0
) {
    var showConditionPicker by remember { mutableStateOf(false) }
    var selectedConditionForDetail by remember { mutableStateOf<Condition?>(null) }

    Surface(
        color = SurfaceCardHighlight.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderDark.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🩸", fontSize = 13.sp)
                    Text(
                        text = "Status-Zustände (Conditions)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeConditions.isNotEmpty()) HealthRed else PaladinGold
                    )
                }

                FilledTonalButton(
                    onClick = { showConditionPicker = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(if (activeConditions.isEmpty()) "Zustand wählen" else "+ Zustand", fontSize = 11.sp)
                }
            }

            if (activeConditions.isEmpty()) {
                Text(
                    text = "Keine aktiven Zustände. Du bist voll einsatzbereit!",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    activeConditions.forEach { condition ->
                        Surface(
                            color = HealthRed.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HealthRed.copy(alpha = 0.6f)),
                            modifier = Modifier.clickable { selectedConditionForDetail = condition }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("${condition.icon} ${condition.displayName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(HealthRed)
                                        .clickable { onRemoveCondition(condition) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Zustand aufheben", tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                    }
                }

                // Schnell-Heilung für Vergiftet über Handauflegen
                if (Condition.POISONED in activeConditions && remainingLayOnHands >= 5) {
                    OutlinedButton(
                        onClick = onCurePoisonLayOnHands,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LayOnHandsGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LayOnHandsGreen.copy(alpha = 0.6f))
                    ) {
                        Text("💚 Vergiftung heilen (5 HP Handauflegen)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Condition Picker Dialog
    if (showConditionPicker) {
        AlertDialog(
            onDismissRequest = { showConditionPicker = false },
            title = {
                Text("Zustand auswählen", color = PaladinGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Wähle einen Zustand, um ihn dem Charakterbogen hinzuzufügen:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Condition.entries.forEach { condition ->
                        val isActive = condition in activeConditions
                        Surface(
                            color = if (isActive) HealthRed.copy(alpha = 0.25f) else SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isActive) HealthRed else BorderDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onToggleCondition(condition)
                                    showConditionPicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(condition.icon, fontSize = 16.sp)
                                    Column {
                                        Text(condition.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(condition.shortSummary, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                                    }
                                }
                                if (isActive) {
                                    Text("Aktiv ✓", fontSize = 10.sp, color = HealthRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showConditionPicker = false }) {
                    Text("Schließen", color = PaladinGold)
                }
            }
        )
    }

    // Condition Detail Dialog
    selectedConditionForDetail?.let { condition ->
        AlertDialog(
            onDismissRequest = { selectedConditionForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(condition.icon, fontSize = 20.sp)
                    Text(condition.displayName, color = HealthRed, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Regeln & Effekte:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text(
                        text = condition.description,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveCondition(condition)
                        selectedConditionForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen)
                ) {
                    Text("Zustand aufheben")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedConditionForDetail = null }) {
                    Text("Schließen", color = TextSecondary)
                }
            }
        )
    }
}
