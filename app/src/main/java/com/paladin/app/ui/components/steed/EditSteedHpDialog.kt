package com.paladin.app.ui.components.steed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun EditSteedHpDialog(
    currentHp: Int,
    maxHp: Int,
    onDismiss: () -> Unit,
    onSave: (current: Int, maxOverride: Int?) -> Unit
) {
    var editCurrentHp by remember { mutableIntStateOf(currentHp) }
    var editMaxHp by remember { mutableIntStateOf(maxHp) }

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
