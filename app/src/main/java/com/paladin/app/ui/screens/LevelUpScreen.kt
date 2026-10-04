package com.paladin.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

@Composable
fun LevelUpScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()
    val context = LocalContext.current

    var selectedLevel by remember(character.level) { mutableIntStateOf((character.level + 1).coerceAtMost(20)) }
    
    // Feats & Fighting Styles (Multi-Select with Budget Indicator)
    var selectedFeats by remember(character.feats) {
        mutableStateOf(character.feats.toSet())
    }
    var customFeatInput by remember { mutableStateOf("") }
    val featLimit = remember(selectedLevel) { com.paladin.app.model.FeatCatalog.getStandardFeatLimit(selectedLevel) }
    val isFeatOverrun = selectedFeats.size > featLimit

    // Level 3: Sacred Oaths (Single-Select)
    val standardOaths = remember {
        listOf(
            "Oath of Devotion" to "Hingabe (SRD): Sacred Weapon (+CHA auf Angriff), Turn the Unholy, Smite of Protection",
            "Oath of Vengeance" to "Rache: Vow of Enmity (Vorteil auf Angriffe gegen ein Ziel), Abjure Enemy, Unerbittlicher Verfolger",
            "Oath of the Ancients" to "Uralte: Nature's Wrath, Turn the Faithless, Magieresistenz-Aura",
            "Oath of Glory" to "Ruhm: Peerless Athlete, Inspiring Smite, Geschwindigkeitsbonus"
        )
    }
    var isCustomOath by remember(character.oath) {
        mutableStateOf(character.oath != null && standardOaths.none { it.first == character.oath })
    }
    var selectedStandardOath by remember(character.oath) {
        mutableStateOf(character.oath?.takeIf { oath -> standardOaths.any { it.first == oath } } ?: "Oath of Devotion")
    }
    var customOathInput by remember(character.oath) {
        mutableStateOf(if (isCustomOath) character.oath ?: "" else "")
    }

    var hpMode by remember { mutableStateOf("average") } // "average" or "manual"
    var manualHpGain by remember { mutableStateOf("6") }

    var importJsonText by remember { mutableStateOf("") }
    var showImportSuccessMessage by remember { mutableStateOf<String?>(null) }

    val conMod = stats.modifiers[Ability.CONSTITUTION] ?: 0
    val averageHpGain = (6 + conMod).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Stufenaufstieg & Backup",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )

        // Level Up Wizard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🌟 Level-Up Assistent (Paladin 2024)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Aktuelle Stufe: ${character.level} ➔ Neue Zielstufe:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (selectedLevel > 1) selectedLevel-- },
                        enabled = selectedLevel > 1
                    ) { Text("-") }

                    Text(
                        text = "Stufe $selectedLevel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PaladinGold
                    )

                    FilledTonalButton(
                        onClick = { if (selectedLevel < 20) selectedLevel++ },
                        enabled = selectedLevel < 20
                    ) { Text("+") }
                }

                // Feats & Fighting Styles Selection
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

                // Checkboxes for Feats
                Text("Kampfstile (Stufe 2+):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                com.paladin.app.model.FeatCatalog.allFeats
                    .filter { it.category == com.paladin.app.model.FeatCategory.FIGHTING_STYLE }
                    .forEach { feat ->
                        val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFeats = if (isChecked) {
                                        selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) }.toSet()
                                    } else {
                                        selectedFeats + feat.name
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    selectedFeats = if (isChecked) {
                                        selectedFeats.filterNot { f -> f.equals(feat.name, ignoreCase = true) }.toSet()
                                    } else {
                                        selectedFeats + feat.name
                                    }
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

                Text("Herkunftstalente (Stufe 1):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                com.paladin.app.model.FeatCatalog.allFeats
                    .filter { it.category == com.paladin.app.model.FeatCategory.ORIGIN }
                    .forEach { feat ->
                        val isChecked = selectedFeats.any { it.equals(feat.name, ignoreCase = true) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedFeats = if (isChecked) {
                                        selectedFeats.filterNot { it.equals(feat.name, ignoreCase = true) }.toSet()
                                    } else {
                                        selectedFeats + feat.name
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    selectedFeats = if (isChecked) {
                                        selectedFeats.filterNot { f -> f.equals(feat.name, ignoreCase = true) }.toSet()
                                    } else {
                                        selectedFeats + feat.name
                                    }
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

                // Custom Feat addition
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
                                selectedFeats = selectedFeats + customFeatInput.trim()
                                customFeatInput = ""
                            }
                        },
                        enabled = customFeatInput.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                // Level 3+: Sacred Oath choice (Single-Select)
                if (selectedLevel >= 3) {
                    HorizontalDivider(color = BorderDark)
                    Text(
                        text = "🛡️ Heiliger Eid (Sacred Oath - Stufe 3):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text(
                        text = "Wähle genau einen Unterklassen-Eid (Single-Select):",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        standardOaths.forEach { (oathTitle, description) ->
                            val isSelected = !isCustomOath && selectedStandardOath == oathTitle
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        isCustomOath = false
                                        selectedStandardOath = oathTitle
                                    }
                                )
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    Text(
                                        text = oathTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PaladinGold else TextPrimary
                                    )
                                    Text(
                                        text = description,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Custom Oath Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = isCustomOath,
                                onClick = { isCustomOath = true }
                            )
                            Text("Eigener Eid (Custom Oath)", fontSize = 13.sp, color = TextPrimary)
                        }

                        if (isCustomOath) {
                            OutlinedTextField(
                                value = customOathInput,
                                onValueChange = { customOathInput = it },
                                label = { Text("Name deines eigenen Eids") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 32.dp, end = 8.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderDark)

                // HP Selection
                Text(
                    text = "Trefferpunkte-Zuwachs:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = hpMode == "average",
                        onClick = { hpMode = "average" },
                        label = { Text("Fester Schnitt ($averageHpGain HP)") }
                    )
                    FilterChip(
                        selected = hpMode == "manual",
                        onClick = { hpMode = "manual" },
                        label = { Text("Selbst gewürfelt") }
                    )
                }

                if (hpMode == "manual") {
                    OutlinedTextField(
                        value = manualHpGain,
                        onValueChange = { manualHpGain = it },
                        label = { Text("Gewürfelter Wert (1d10 + CON)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        val hpGain = if (hpMode == "average") averageHpGain else manualHpGain.toIntOrNull() ?: averageHpGain
                        val resolvedOath = if (selectedLevel >= 3) {
                            if (isCustomOath) customOathInput.ifBlank { "Custom Oath" } else selectedStandardOath
                        } else null
                        val resolvedFightingStyle = selectedFeats.firstOrNull { it.contains("Defense", ignoreCase = true) || it.contains("Dueling", ignoreCase = true) }

                        viewModel.updateLevel(
                            newLevel = selectedLevel,
                            hpGain = hpGain,
                            newOath = resolvedOath,
                            newFightingStyle = resolvedFightingStyle,
                            newFeats = selectedFeats.toList()
                        )
                        Toast.makeText(context, "Auf Stufe $selectedLevel aufgestiegen!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Stufe anwenden", fontWeight = FontWeight.Bold)
                }
            }
        }

        // New Character Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⚔️ Charakter-Verwaltung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Möchtest du einen neuen Paladin von Stufe 1 an erstellen?",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedButton(
                    onClick = { viewModel.startNewCharacterCreation() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Neuen Stufe-1-Paladin erschaffen")
                }
            }
        }

        // Backup Export / Import Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "💾 Lokales Backup (JSON Export & Import)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Sichere deine Charakterdaten als JSON-Textdatei oder übertrage sie auf ein anderes Gerät.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Button(
                    onClick = {
                        val json = viewModel.exportBackupJson()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Paladin Backup", json)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Backup JSON in die Zwischenablage kopiert!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = PaladinGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📋 Charakter-Backup in Zwischenablage kopieren")
                }

                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = { importJsonText = it },
                    label = { Text("Backup JSON zum Importieren hier einfügen") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            val success = viewModel.importBackupJson(importJsonText)
                            if (success) {
                                showImportSuccessMessage = "Charakter erfolgreich wiederhergestellt!"
                                importJsonText = ""
                            } else {
                                Toast.makeText(context, "Fehler beim Lesen des JSON Backups!", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = importJsonText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📥 Backup wiederherstellen", fontWeight = FontWeight.Bold)
                }

                if (showImportSuccessMessage != null) {
                    Text(
                        text = showImportSuccessMessage!!,
                        color = LayOnHandsGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
