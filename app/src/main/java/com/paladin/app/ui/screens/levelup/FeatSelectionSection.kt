package com.paladin.app.ui.screens.levelup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.DetailItem
import com.paladin.app.model.FeatCatalog
import com.paladin.app.model.FeatCategory
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatSelectionSection(
    selectedLevel: Int,
    selectedFeats: Set<String>,
    featLimit: Int,
    isFeatOverrun: Boolean,
    onFeatsChanged: (Set<String>) -> Unit,
    onShowDetail: (DetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var customFeatInput by remember { mutableStateOf("") }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalDivider(color = BorderDark)
        Text(
            text = "✨ Talente & Kampfstile (Feats):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )

        // Feats Budget Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isFeatOverrun) HealthRed.copy(alpha = 0.15f)
                    else PaladinGold.copy(alpha = 0.12f)
                )
                .border(
                    width = 1.dp,
                    color = if (isFeatOverrun) HealthRed else PaladinGold.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFeatOverrun) "⚠️ Feat-Limit überschritten" else "Aktive Talente:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeatOverrun) HealthRed else PaladinGold
                    )
                    Text(
                        text = "${selectedFeats.size} / $featLimit gewählt",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeatOverrun) HealthRed else ProficiencyGreen
                    )
                }
                Text(
                    text = if (isFeatOverrun)
                        "Du hast ${selectedFeats.size} Talente gewählt (Regelstandard für Stufe $selectedLevel: $featLimit). Überschreitungen sind erlaubt!"
                    else
                        "Wähle Herkunftstalente, Kampfstile (z. B. Defense, Dueling) und Hausregeln.",
                    fontSize = 11.sp,
                    color = if (isFeatOverrun) HealthRed.copy(alpha = 0.9f) else TextSecondary
                )
            }
        }

        // Checkboxes for Fighting Styles
        Text("Kampfstile (Stufe 2+):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        FeatCatalog.allFeats
            .filter { it.category == FeatCategory.FIGHTING_STYLE }
            .forEach { feat ->
                val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                val updated = if (isChecked) {
                                    selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) }.toSet()
                                } else {
                                    selectedFeats + feat.name
                                }
                                onFeatsChanged(updated)
                            },
                            onLongClick = { onShowDetail(DetailItem.FeatInfo(feat)) }
                        )
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            val updated = if (isChecked) {
                                selectedFeats.filterNot { f -> f.equals(feat.name, ignoreCase = true) }.toSet()
                            } else {
                                selectedFeats + feat.name
                            }
                            onFeatsChanged(updated)
                        }
                    )
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = feat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) PaladinGold else TextPrimary
                        )
                        Text(
                            text = feat.mechanicalBenefit,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

        // Origin Feats
        Text("Herkunftstalente (Stufe 1):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        FeatCatalog.allFeats
            .filter { it.category == FeatCategory.ORIGIN }
            .forEach { feat ->
                val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                val updated = if (isChecked) {
                                    selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) }.toSet()
                                } else {
                                    selectedFeats + feat.name
                                }
                                onFeatsChanged(updated)
                            },
                            onLongClick = { onShowDetail(DetailItem.FeatInfo(feat)) }
                        )
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            val updated = if (isChecked) {
                                selectedFeats.filterNot { f -> f.equals(feat.name, ignoreCase = true) }.toSet()
                            } else {
                                selectedFeats + feat.name
                            }
                            onFeatsChanged(updated)
                        }
                    )
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = feat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) PaladinGold else TextPrimary
                        )
                        Text(
                            text = feat.mechanicalBenefit,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

        // General Feats (Stufe 4+)
        if (selectedLevel >= 4) {
            Text("Allgemeine Talente (Stufe 4+):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            FeatCatalog.allFeats
                .filter { it.category == FeatCategory.GENERAL }
                .forEach { feat ->
                    val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) || it.contains(feat.id, ignoreCase = true) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                            onClick = {
                                val updated = if (isChecked) {
                                    selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) || it.contains(feat.id, ignoreCase = true) }.toSet()
                                } else {
                                    selectedFeats + feat.name
                                }
                                onFeatsChanged(updated)
                            },
                            onLongClick = { onShowDetail(DetailItem.FeatInfo(feat)) }
                        )
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            val updated = if (isChecked) {
                                selectedFeats.filterNot { f -> f.equals(feat.name, ignoreCase = true) || f.contains(feat.id, ignoreCase = true) }.toSet()
                            } else {
                                selectedFeats + feat.name
                            }
                            onFeatsChanged(updated)
                        }
                    )
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = feat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) PaladinGold else TextPrimary
                        )
                        Text(
                            text = feat.mechanicalBenefit,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Custom Feat Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customFeatInput,
                onValueChange = { customFeatInput = it },
                label = { Text("Eigenes Talent / Fähigkeit hinzufügen") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            FilledTonalButton(
                onClick = {
                    if (customFeatInput.isNotBlank()) {
                        onFeatsChanged(selectedFeats + customFeatInput.trim())
                        customFeatInput = ""
                    }
                },
                enabled = customFeatInput.isNotBlank()
            ) {
                Text("Add")
            }
        }
    }
}
