package com.paladin.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paladin.app.model.JournalEntry
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.FullscreenImageDialog
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.HealthRed
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary
import java.io.File

@Composable
fun JournalScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val entries = character.journalEntries
    val context = LocalContext.current

    var entryToDelete by remember { mutableStateOf<JournalEntry?>(null) }
    var selectedFullscreenImage by remember { mutableStateOf<String?>(null) }

    // Reorder drag state
    var draggedIndex by remember { mutableStateOf<Int?>(null) }

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
                    val isBeingDragged = draggedIndex == index

                    JournalSectionItem(
                        entry = entry,
                        isFirst = index == 0,
                        canMoveUp = index > 0,
                        canMoveDown = index < entries.size - 1,
                        isDragging = isBeingDragged,
                        onMoveUp = { viewModel.moveJournalEntry(index, index - 1) },
                        onMoveDown = { viewModel.moveJournalEntry(index, index + 1) },
                        onDragStart = { draggedIndex = index },
                        onDragEnd = { draggedIndex = null },
                        onDragMove = { dragAmountY ->
                            // Dynamischer Tausch bei vertikalem Drag über Schwellenwert
                            val threshold = 90f // Pixel-Schwelle für Item-Wechsel
                            val currentIndex = draggedIndex
                            if (currentIndex != null) {
                                if (dragAmountY > threshold && currentIndex < entries.size - 1) {
                                    viewModel.moveJournalEntry(currentIndex, currentIndex + 1)
                                    draggedIndex = currentIndex + 1
                                } else if (dragAmountY < -threshold && currentIndex > 0) {
                                    viewModel.moveJournalEntry(currentIndex, currentIndex - 1)
                                    draggedIndex = currentIndex - 1
                                }
                            }
                        },
                        onUpdate = { updatedTitle, updatedContent ->
                            viewModel.updateJournalEntry(entry.id, updatedTitle, updatedContent)
                        },
                        onDeleteRequest = {
                            entryToDelete = entry
                        },
                        onAddPhoto = { uri ->
                            viewModel.addPhotoToJournalEntry(entry.id, uri, context)
                        },
                        onRemovePhoto = { path ->
                            viewModel.removePhotoFromJournalEntry(entry.id, path)
                        },
                        onImageClick = { path ->
                            selectedFullscreenImage = path
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
                    text = "Möchtest du den Abschnitt '${entryToDelete?.displayTitle}' wirklich unwiderruflich löschen?",
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

    // Animated Fullscreen Image Dialog for Journal Photos
    selectedFullscreenImage?.let { imagePath ->
        FullscreenImageDialog(
            imageModel = File(imagePath),
            contentDescription = "Journal Foto",
            onDismiss = { selectedFullscreenImage = null }
        )
    }
}

@Composable
fun JournalSectionItem(
    entry: JournalEntry,
    isFirst: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    isDragging: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragMove: (Float) -> Unit,
    onUpdate: (String, String) -> Unit,
    onDeleteRequest: () -> Unit,
    onAddPhoto: (Uri) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onImageClick: (String) -> Unit
) {
    var titleText by remember(entry.id, entry.title, entry.dateText) { 
        mutableStateOf(if (entry.title.isNotBlank()) entry.title else entry.dateText) 
    }
    var contentText by remember(entry.id, entry.content) { mutableStateOf(entry.content) }
    var isEditingTitle by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddPhoto(uri)
        }
    }

    // Drag gesture tracking
    var accumulatedDragY by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (isDragging) 10f else 1f),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) SurfaceCardHighlight else SurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isDragging) 2.dp else 1.dp,
            if (isDragging) PaladinGoldBright else if (isFirst) PaladinGold.copy(alpha = 0.5f) else BorderDark
        )
    ) {
        Column {
            // Horizontaler Reiter (Tab Banner / Header mit Reorder Drag Handle & Pfeiltasten)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isFirst) PaladinGold.copy(alpha = 0.18f) else SurfaceCardHighlight)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drag Handle & Title Row
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Drag Handle mit Longpress
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        accumulatedDragY = 0f
                                        onDragStart()
                                    },
                                    onDragEnd = {
                                        accumulatedDragY = 0f
                                        onDragEnd()
                                    },
                                    onDragCancel = {
                                        accumulatedDragY = 0f
                                        onDragEnd()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        accumulatedDragY += dragAmount.y
                                        onDragMove(accumulatedDragY)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Gedrückt halten zum Verschieben",
                            tint = if (isDragging) PaladinGoldBright else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isEditingTitle) {
                        OutlinedTextField(
                            value = titleText,
                            onValueChange = {
                                titleText = it
                                onUpdate(it, contentText)
                            },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGoldBright
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                unfocusedBorderColor = BorderDark,
                                focusedContainerColor = DarkNavyBackground.copy(alpha = 0.5f),
                                unfocusedContainerColor = DarkNavyBackground.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .padding(vertical = 2.dp)
                        )
                    } else {
                        Text(
                            text = titleText.ifBlank { "Unbenannter Abschnitt" },
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirst) PaladinGoldBright else PaladinGold,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isEditingTitle = true }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                }

                // Action Buttons: Edit, Move Up, Move Down, Delete
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Title edit button
                    IconButton(
                        onClick = { isEditingTitle = !isEditingTitle },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditingTitle) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Titel bearbeiten",
                            tint = if (isEditingTitle) PaladinGold else TextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Move Up button
                    IconButton(
                        onClick = onMoveUp,
                        enabled = canMoveUp,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Nach oben verschieben",
                            tint = if (canMoveUp) TextSecondary else TextSecondary.copy(alpha = 0.25f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Move Down button
                    IconButton(
                        onClick = onMoveDown,
                        enabled = canMoveDown,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Nach unten verschieben",
                            tint = if (canMoveDown) TextSecondary else TextSecondary.copy(alpha = 0.25f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Delete button
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
                        onUpdate(titleText, it)
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

            // Fotos / Anhänge Bereich am unteren Rand des Eintrags
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fotos Thumbnails Row
                if (entry.imagePaths.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        entry.imagePaths.forEach { imagePath ->
                            Box(
                                modifier = Modifier.size(64.dp)
                            ) {
                                // Thumbnail Image (Clickable for Fullscreen)
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(File(imagePath))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Journal Foto Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.5.dp, PaladinGold.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                        .clickable { onImageClick(imagePath) }
                                )

                                // Delete Badge (✕) on top-right corner
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(HealthRed)
                                        .clickable { onRemovePhoto(imagePath) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Foto entfernen",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Button "Foto hinzufügen"
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PaladinGold
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (entry.imagePaths.isEmpty()) "Foto hinzufügen" else "Weiteres Foto",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
