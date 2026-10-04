package com.paladin.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var selectedOath by remember(character.oath) { mutableStateOf(character.oath ?: "Oath of Devotion") }
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

                // Subclass / Oath choice at Level 3+
                if (selectedLevel >= 3) {
                    Text(
                        text = "Heiliger Eid (Sacred Oath):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    val oaths = listOf("Oath of Devotion (SRD)", "Oath of Vengeance", "Oath of the Ancients", "Oath of Glory", "Custom Eid")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        oaths.forEach { oathOption ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedOath.startsWith(oathOption.substringBefore(" ")),
                                    onClick = { selectedOath = oathOption }
                                )
                                Text(oathOption, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }
                }

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
                        viewModel.updateLevel(
                            newLevel = selectedLevel,
                            hpGain = hpGain,
                            newOath = if (selectedLevel >= 3) selectedOath else null
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
