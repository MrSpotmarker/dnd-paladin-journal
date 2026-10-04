package com.paladin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.model.FeatCatalog
import com.paladin.app.model.FeatCategory
import com.paladin.app.ui.theme.*

@Composable
fun FeatsDialog(
    characterLevel: Int,
    currentFeats: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var selectedFeats by remember { mutableStateOf(currentFeats.toSet()) }
    var customFeatInput by remember { mutableStateOf("") }

    val standardLimit = remember(characterLevel) { FeatCatalog.getStandardFeatLimit(characterLevel) }
    val isOverLimit = selectedFeats.size > standardLimit

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "✨ Talente & Fähigkeiten (Feats)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "D&D 2024 Regelwerk & Hausregeln",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Limit Indicator Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isOverLimit) HealthRed.copy(alpha = 0.15f)
                            else PaladinGold.copy(alpha = 0.12f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isOverLimit) HealthRed else PaladinGold.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isOverLimit) "⚠️ Regel-Limit überschritten:" else "Aktive Talente & Kampfstile:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverLimit) HealthRed else PaladinGold
                            )
                            Text(
                                text = "${selectedFeats.size} / $standardLimit gewählt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverLimit) HealthRed else ProficiencyGreen
                            )
                        }
                        Text(
                            text = if (isOverLimit)
                                "Du hast mehr Talente gewählt als laut Standard vorgesehen (+${selectedFeats.size - standardLimit}). Überschreitungen sind erlaubt!"
                            else
                                "Stufe $characterLevel Paladin: bis zu $standardLimit Talente (inkl. Herkunft & Mensch).",
                            fontSize = 11.sp,
                            color = if (isOverLimit) HealthRed.copy(alpha = 0.9f) else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category: Origin Feats
                    Text(
                        text = "1. Herkunftstalente (Stufe 1):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    FeatCatalog.allFeats.filter { it.category == FeatCategory.ORIGIN }.forEach { feat ->
                        val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) || it.equals(feat.id, ignoreCase = true) }
                        FeatSelectionRow(
                            name = feat.name,
                            desc = feat.description,
                            benefit = feat.mechanicalBenefit,
                            isChecked = isChecked,
                            onToggle = {
                                selectedFeats = if (isChecked) {
                                    selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) || it.equals(feat.id, ignoreCase = true) }.toSet()
                                } else {
                                    selectedFeats + feat.name
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = BorderDark)

                    // Category: Fighting Style Feats
                    Text(
                        text = "2. Kampfstil-Talente (Fighting Styles):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    FeatCatalog.allFeats.filter { it.category == FeatCategory.FIGHTING_STYLE }.forEach { feat ->
                        val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) || it.equals(feat.id, ignoreCase = true) }
                        FeatSelectionRow(
                            name = feat.name,
                            desc = feat.description,
                            benefit = feat.mechanicalBenefit,
                            isChecked = isChecked,
                            onToggle = {
                                selectedFeats = if (isChecked) {
                                    selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) || it.equals(feat.id, ignoreCase = true) }.toSet()
                                } else {
                                    selectedFeats + feat.name
                                }
                            }
                        )
                    }

                    HorizontalDivider(color = BorderDark)

                    // Custom Feats Section
                    Text(
                        text = "3. Eigene Talente & Fähigkeiten:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )

                    // Display already active custom feats
                    val catalogNames = FeatCatalog.allFeats.map { it.name.lowercase() } + FeatCatalog.allFeats.map { it.id.lowercase() }
                    val customFeats = selectedFeats.filter { it.lowercase() !in catalogNames }
                    if (customFeats.isNotEmpty()) {
                        customFeats.forEach { customName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• $customName",
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                IconButton(
                                    onClick = { selectedFeats = selectedFeats - customName },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Entfernen", tint = HealthRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Add Custom Feat Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customFeatInput,
                            onValueChange = { customFeatInput = it },
                            label = { Text("Eigenes Talent / Fähigkeit") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        FilledTonalButton(
                            onClick = {
                                if (customFeatInput.isNotBlank()) {
                                    selectedFeats = selectedFeats + customFeatInput.trim()
                                    customFeatInput = ""
                                }
                            },
                            enabled = customFeatInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Button
                Button(
                    onClick = {
                        onSave(selectedFeats.toList())
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Auswahl speichern & anwenden", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FeatSelectionRow(
    name: String,
    desc: String,
    benefit: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            modifier = Modifier.padding(top = 2.dp)
        )
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isChecked) PaladinGold else TextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "Effekt: $benefit",
                fontSize = 11.sp,
                color = if (isChecked) PaladinGoldBright else TextSecondary.copy(alpha = 0.8f)
            )
        }
    }
}
