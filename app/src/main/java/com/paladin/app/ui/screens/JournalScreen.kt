package com.paladin.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.JournalEntry
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

@Composable
fun JournalScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val entries = character.journalEntries

    var entryToDelete by remember { mutableStateOf<JournalEntry?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar: Title & Add Section Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tagebuch & Chronik",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "${entries.size} ${if (entries.size == 1) "Abschnitt" else "Abschnitte"}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { viewModel.addJournalEntry() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PaladinGold,
                    contentColor = DarkNavyBackground
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Neuer Abschnitt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        HorizontalDivider(color = BorderDark)

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "📜",
                        fontSize = 40.sp
                    )
                    Text(
                        text = "Noch keine Einträge",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tippe oben auf '+ Neuer Abschnitt', um deine Abenteuer und Erlebnisse chronologisch festzuhalten.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { viewModel.addJournalEntry() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaladinGold,
                            contentColor = DarkNavyBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ersten Abschnitt anlegen (${viewModel.getFormattedCurrentDate()})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(entries, key = { _, entry -> entry.id }) { index, entry ->
                    JournalSectionItem(
                        entry = entry,
                        isFirst = index == 0,
                        onUpdate = { updatedDate, updatedContent ->
                            viewModel.updateJournalEntry(entry.id, updatedDate, updatedContent)
                        },
                        onDeleteRequest = {
                            entryToDelete = entry
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = {
                Text(
                    text = "Abschnitt löschen?",
                    fontWeight = FontWeight.Bold,
                    color = HealthRed
                )
            },
            text = {
                Text(
                    text = "Möchtest du den Abschnitt vom '${entryToDelete?.dateText}' wirklich unwiderruflich löschen?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        entryToDelete?.let { viewModel.deleteJournalEntry(it.id) }
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRed)
                ) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { entryToDelete = null }) {
                    Text("Abbrechen")
                }
            },
            containerColor = DarkNavyBackground
        )
    }
}

@Composable
fun JournalSectionItem(
    entry: JournalEntry,
    isFirst: Boolean,
    onUpdate: (String, String) -> Unit,
    onDeleteRequest: () -> Unit
) {
    var dateText by remember(entry.id, entry.dateText) { mutableStateOf(entry.dateText) }
    var contentText by remember(entry.id, entry.content) { mutableStateOf(entry.content) }
    var isEditingDate by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isFirst) PaladinGold.copy(alpha = 0.5f) else BorderDark
        )
    ) {
        Column {
            // Horizontaler Reiter (Tab Banner / Header)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isFirst) PaladinGold.copy(alpha = 0.18f) else SurfaceCardHighlight)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📅", fontSize = 14.sp)

                    if (isEditingDate) {
                        OutlinedTextField(
                            value = dateText,
                            onValueChange = {
                                dateText = it
                                onUpdate(it, contentText)
                            },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGoldBright
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                unfocusedBorderColor = BorderDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(44.dp)
                        )
                    } else {
                        Text(
                            text = dateText.ifBlank { "Unbenannter Abschnitt" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirst) PaladinGoldBright else PaladinGold,
                            modifier = Modifier.clickable { isEditingDate = true }
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isEditingDate = !isEditingDate },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Datum bearbeiten",
                            tint = if (isEditingDate) PaladinGold else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteRequest,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Abschnitt löschen",
                            tint = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderDark.copy(alpha = 0.5f))

            // Dokument-Textbereich für diesen Abschnitt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = contentText,
                    onValueChange = {
                        contentText = it
                        onUpdate(dateText, it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp),
                    placeholder = {
                        Text(
                            text = "Notizen, Questziele oder Geschehnisse eintragen...",
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PaladinGold.copy(alpha = 0.5f),
                        unfocusedBorderColor = BorderDark.copy(alpha = 0.4f),
                        focusedContainerColor = DarkNavyBackground.copy(alpha = 0.4f),
                        unfocusedContainerColor = DarkNavyBackground.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
