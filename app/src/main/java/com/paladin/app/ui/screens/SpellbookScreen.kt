package com.paladin.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.data.online.OnlineSearchCategory
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Spell
import com.paladin.app.model.SpellSchool
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.OnlineSearchDialog
import com.paladin.app.ui.theme.*
import java.util.UUID

@Composable
fun SpellbookScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()
    val srdSpells by viewModel.srdSpells.collectAsState()

    var selectedLevelFilter by remember { mutableStateOf<Int?>(null) }
    var onlyPreparedFilter by remember { mutableStateOf(false) }
    var showAddSpellDialog by remember { mutableStateOf(false) }
    var showOnlineSearchDialog by remember { mutableStateOf(false) }

    val allSpells = remember(srdSpells, character.customSpells) {
        srdSpells + character.customSpells
    }

    val filteredSpells = allSpells.filter { spell ->
        (selectedLevelFilter == null || spell.level == selectedLevelFilter) &&
                (!onlyPreparedFilter || spell.id in character.preparedSpellIds)
    }

    val preparedCount = character.preparedSpellIds.size
    val maxPrepared = stats.maxPreparedSpells

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Zauberbuch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Vorbereitet: $preparedCount / $maxPrepared Zauber",
                    fontSize = 12.sp,
                    color = if (preparedCount >= maxPrepared) HealthRed else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { showOnlineSearchDialog = true },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ChaunteaGreenContainer,
                        contentColor = ChaunteaGreenBright
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Online", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FloatingActionButton(
                    onClick = { showAddSpellDialog = true },
                    containerColor = PaladinGold,
                    contentColor = DarkNavyBackground,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Eigenen Zauber eintragen")
                }
            }
        }

        // Spell DC & Attack Bonus Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${stats.spellSaveDc}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SpellSlotPurple)
                    Text(text = "Zauber-Rettungswurf (DC)", fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "+${stats.spellAttackBonus}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SpellSlotPurple)
                    Text(text = "Zauber-Angriffsbonus", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = onlyPreparedFilter,
                    onClick = { onlyPreparedFilter = !onlyPreparedFilter },
                    label = { Text("Vorbereitet", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedLevelFilter == null,
                    onClick = { selectedLevelFilter = null },
                    label = { Text("Alle", fontSize = 11.sp) }
                )
            }
            (1..5).forEach { lvl ->
                item {
                    FilterChip(
                        selected = selectedLevelFilter == lvl,
                        onClick = { selectedLevelFilter = if (selectedLevelFilter == lvl) null else lvl },
                        label = { Text("Grad $lvl", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Spells List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSpells, key = { it.id }) { spell ->
                val isPrepared = spell.id in character.preparedSpellIds
                val isCustom = character.customSpells.any { it.id == spell.id }
                SpellCard(
                    spell = spell,
                    isPrepared = isPrepared,
                    isCustom = isCustom,
                    onTogglePrepared = { viewModel.togglePrepareSpell(spell.id) },
                    onDelete = if (isCustom) { { viewModel.removeCustomSpell(spell.id) } } else null,
                    onShowDetail = { viewModel.showDetail(DetailItem.SpellInfo(spell)) }
                )
            }
        }
    }

    if (showAddSpellDialog) {
        AddCustomSpellDialog(
            onDismiss = { showAddSpellDialog = false },
            onAdd = { newSpell ->
                viewModel.addCustomSpell(newSpell)
                showAddSpellDialog = false
            }
        )
    }

    if (showOnlineSearchDialog) {
        OnlineSearchDialog(
            viewModel = viewModel,
            initialCategory = OnlineSearchCategory.SPELLS,
            onDismiss = { showOnlineSearchDialog = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpellCard(
    spell: Spell,
    isPrepared: Boolean,
    isCustom: Boolean = false,
    onTogglePrepared: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onShowDetail: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = onShowDetail
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrepared) SurfaceCardHighlight else SurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isPrepared) {
            androidx.compose.foundation.BorderStroke(1.dp, SpellSlotPurple.copy(alpha = 0.6f))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, BorderDark.copy(alpha = 0.6f))
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spell.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrepared) SpellSlotPurple else TextPrimary
                    )
                    Text(
                        text = "Grad ${spell.level} • ${spell.school.displayName} • ${spell.castingTime}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isCustom && onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen",
                                tint = HealthRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Prepare Button
                    IconButton(
                        onClick = onTogglePrepared,
                        modifier = Modifier.background(
                            if (isPrepared) SpellSlotPurple else SurfaceCardHighlight,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Vorbereiten",
                            tint = if (isPrepared) DarkNavyBackground else TextSecondary
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Reichweite: ${spell.range}", fontSize = 11.sp, color = PaladinGold)
                        Text(text = "Dauer: ${spell.duration}", fontSize = 11.sp, color = PaladinGold)
                    }
                    if (spell.isConcentration) {
                        Surface(color = HealthRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "Konzentration",
                                color = HealthRed,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = spell.description,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clickable { onShowDetail() },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✨ Vollständige Zauberdetails (oder lange drücken) ➔",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PaladinGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomSpellDialog(
    onDismiss: () -> Unit,
    onAdd: (Spell) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var levelStr by remember { mutableStateOf("1") }
    var school by remember { mutableStateOf(SpellSchool.EVOCATION) }
    var castingTime by remember { mutableStateOf("1 Action") }
    var range by remember { mutableStateOf("Touch") }
    var duration by remember { mutableStateOf("Instantaneous") }
    var isConcentration by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neuen Zauber eintragen (PHB/Homebrew)", color = PaladinGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Zaubername") })
                OutlinedTextField(value = levelStr, onValueChange = { levelStr = it }, label = { Text("Grad (1..5)") })
                OutlinedTextField(value = castingTime, onValueChange = { castingTime = it }, label = { Text("Zauberzeit") })
                OutlinedTextField(value = range, onValueChange = { range = it }, label = { Text("Reichweite") })
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Wirkungsdauer") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isConcentration, onCheckedChange = { isConcentration = it })
                    Text("Konzentration erforderlich", fontSize = 12.sp)
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Vollständige Zauberbeschreibung") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val lvl = levelStr.toIntOrNull()?.coerceIn(1, 5) ?: 1
                        onAdd(
                            Spell(
                                id = "custom_${UUID.randomUUID()}",
                                name = name,
                                level = lvl,
                                school = school,
                                castingTime = castingTime,
                                range = range,
                                duration = duration,
                                isConcentration = isConcentration,
                                description = description,
                                source = "Custom / PHB"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
            ) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen", color = TextSecondary) }
        }
    )
}
