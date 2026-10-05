package com.paladin.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

@Composable
fun CharacterBackupCard(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val character by viewModel.character.collectAsState()

    var selectedBackupTab by remember { mutableIntStateOf(0) } // 0 = .paladin Datei (mit Fotos), 1 = JSON Zwischenablage
    var importJsonText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isStatusError by remember { mutableStateOf(false) }

    // Launcher for creating .paladin archive file (Save as document)
    val createArchiveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            val result = viewModel.exportArchiveBackup(context, uri)
            result.fold(
                onSuccess = { imagesCount ->
                    isStatusError = false
                    statusMessage = "Backup erfolgreich gespeichert ($imagesCount Fotos komprimiert & gebündelt)!"
                    Toast.makeText(context, "Backup erfolgreich exportiert!", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    isStatusError = true
                    statusMessage = "Fehler beim Exportieren: ${error.localizedMessage}"
                }
            )
        }
    }

    // Launcher for importing .paladin archive file
    val openArchiveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val result = viewModel.importArchiveBackup(context, uri)
            result.fold(
                onSuccess = { restored ->
                    isStatusError = false
                    val photosCount = restored.journalEntries.sumOf { it.imagePaths.size }
                    statusMessage = "Erfolgreich wiederhergestellt: ${restored.name} (Stufe ${restored.level}) mit $photosCount Fotos!"
                    Toast.makeText(context, "Charakter & Fotos wiederhergestellt!", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    isStatusError = true
                    statusMessage = "Fehler beim Importieren: ${error.localizedMessage}"
                }
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💾 Backup & Wiederherstellung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
            }

            // Tab-Switch between Archive (.paladin) and Text (JSON)
            TabRow(
                selectedTabIndex = selectedBackupTab,
                containerColor = SurfaceCardHighlight,
                contentColor = PaladinGold,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedBackupTab == 0,
                    onClick = { selectedBackupTab = 0 },
                    text = { Text("📦 .paladin Archiv (mit Fotos)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedBackupTab == 1,
                    onClick = { selectedBackupTab = 1 },
                    text = { Text("📋 JSON Text", fontSize = 12.sp) }
                )
            }

            if (selectedBackupTab == 0) {
                // ── .paladin Archiv Section ────────────────────────────────────
                Text(
                    text = "Sichert deinen gesamten Paladin inklusive aller Journal-Fotos in einem komprimierten .paladin Archiv.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Info-Box Compression & Fotos
                val totalPhotos = character.journalEntries.sumOf { it.imagePaths.size }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PaladinGold.copy(alpha = 0.10f))
                        .border(1.dp, PaladinGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "📸 Enthaltene Fotos: $totalPhotos Journal-Foto${if (totalPhotos == 1) "" else "s"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGoldBright
                        )
                        Text(
                            text = "Fotos werden beim Export automatisch für Mobilgeräte komprimiert, um Speicherplatz zu sparen.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export & Save File
                    Button(
                        onClick = {
                            val sanitizedName = character.name.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "Paladin" }
                            val defaultFileName = "${sanitizedName}_Stufe${character.level}_Backup.paladin"
                            createArchiveLauncher.launch(defaultFileName)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaladinGold,
                            contentColor = DarkNavyBackground
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exportieren", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Direct Share Button
                    OutlinedButton(
                        onClick = {
                            val result = viewModel.createShareableArchive()
                            result.fold(
                                onSuccess = { file ->
                                    try {
                                        val contentUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/octet-stream"
                                            putExtra(Intent.EXTRA_STREAM, contentUri)
                                            putExtra(Intent.EXTRA_SUBJECT, "Paladin Backup: ${character.name}")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Backup teilen via"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Fehler beim Teilen: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Konnte Archiv nicht erstellen: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PaladinGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Teilen", fontSize = 13.sp)
                    }
                }

                // Import Button
                Button(
                    onClick = {
                        openArchiveLauncher.launch(arrayOf("*/*"))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LayOnHandsGreen,
                        contentColor = DarkNavyBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("📥 .paladin Backup-Datei importieren", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

            } else {
                // ── JSON Text Section ──────────────────────────────────────────
                Text(
                    text = "Sichere deine Charakterdaten als reinen JSON-Text für Notizen oder die Zwischenablage (ohne Fotos).",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Button(
                    onClick = {
                        val json = viewModel.exportBackupJson()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Paladin Backup", json)
                        clipboard.setPrimaryClip(clip)
                        isStatusError = false
                        statusMessage = "Backup JSON in die Zwischenablage kopiert!"
                        Toast.makeText(context, "In die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHighlight, contentColor = PaladinGold),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📋 Charakter-JSON in Zwischenablage kopieren", fontSize = 13.sp)
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
                                isStatusError = false
                                statusMessage = "Charakter erfolgreich aus JSON wiederhergestellt!"
                                importJsonText = ""
                            } else {
                                isStatusError = true
                                statusMessage = "Fehler beim Lesen des JSON Backups!"
                            }
                        }
                    },
                    enabled = importJsonText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = LayOnHandsGreen, contentColor = DarkNavyBackground),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📥 JSON wiederherstellen", fontWeight = FontWeight.Bold)
                }
            }

            // Status / Error message
            if (statusMessage != null) {
                Text(
                    text = statusMessage!!,
                    color = if (isStatusError) HealthRed else ProficiencyGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
