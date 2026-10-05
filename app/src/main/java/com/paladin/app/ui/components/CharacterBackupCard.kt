package com.paladin.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.*

@Composable
fun CharacterBackupCard(
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var importJsonText by remember { mutableStateOf("") }
    var showImportSuccessMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    val json = onExportJson()
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
                        val success = onImportJson(importJsonText)
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
