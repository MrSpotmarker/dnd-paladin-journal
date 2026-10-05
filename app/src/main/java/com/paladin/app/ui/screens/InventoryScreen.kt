package com.paladin.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Item
import com.paladin.app.model.ItemType
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*
import java.util.UUID

@Composable
fun InventoryScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()
    val srdItems by viewModel.srdItems.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Inventar & Ausrüstung",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Gewicht: ${"%.1f".format(stats.totalWeightLbs)} / ${"%.0f".format(stats.carryCapacityLbs)} lbs • Gold: ${character.goldPieces} GP",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            FloatingActionButton(
                onClick = { showAddItemDialog = true },
                containerColor = PaladinGold,
                contentColor = DarkNavyBackground,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Gegenstand hinzufügen")
            }
        }

        // Weight bar
        val weightFraction = if (stats.carryCapacityLbs > 0) {
            (stats.totalWeightLbs / stats.carryCapacityLbs).toFloat().coerceIn(0f, 1f)
        } else 0f
        LinearProgressIndicator(
            progress = { weightFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (weightFraction > 0.8f) HealthRed else PaladinGold,
            trackColor = SurfaceCardHighlight
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(character.inventory) { item ->
                InventoryItemRow(
                    item = item,
                    onToggleEquip = { viewModel.toggleEquipItem(item.id) },
                    onToggleAttune = { viewModel.toggleAttuneItem(item.id) },
                    onDelete = { viewModel.removeItem(item.id) },
                    onShowDetail = { viewModel.showDetail(DetailItem.ItemInfo(item)) }
                )
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            srdItems = srdItems,
            onDismiss = { showAddItemDialog = false },
            onAdd = { newItem ->
                viewModel.addItem(newItem)
                showAddItemDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryItemRow(
    item: Item,
    onToggleEquip: () -> Unit,
    onToggleAttune: () -> Unit,
    onDelete: () -> Unit,
    onShowDetail: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onShowDetail,
                onLongClick = onShowDetail
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isEquipped) SurfaceCardHighlight else SurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (item.isEquipped) androidx.compose.foundation.BorderStroke(1.dp, PaladinGold.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isEquipped) PaladinGold else TextPrimary
                    )
                    if (item.isEquipped) {
                        Surface(
                            color = PaladinGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = "Angelegt",
                                color = PaladinGold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${item.type.name} • ${item.weightLbs} lbs ${if (item.description.isNotBlank()) "• ${item.description}" else ""}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                // Equip button
                OutlinedButton(
                    onClick = onToggleEquip,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(if (item.isEquipped) "Ablegen" else "Anlegen", fontSize = 11.sp)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = HealthRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    srdItems: List<Item>,
    onDismiss: () -> Unit,
    onAdd: (Item) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var customName by remember { mutableStateOf("") }
    var customType by remember { mutableStateOf(ItemType.GEAR) }
    var customDesc by remember { mutableStateOf("") }
    var customWeight by remember { mutableStateOf("1.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Gegenstand hinzufügen", color = PaladinGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceCard,
                    contentColor = PaladinGold
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("SRD 5.2 Katalog", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Eigener Gegenstand", fontSize = 12.sp) }
                    )
                }

                if (selectedTab == 0) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(srdItems) { item ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAdd(item.copy(id = UUID.randomUUID().toString())) },
                                color = SurfaceCardHighlight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                    Text("${item.type} • ${item.weightLbs} lbs", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customWeight,
                        onValueChange = { customWeight = it },
                        label = { Text("Gewicht (lbs)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDesc,
                        onValueChange = { customDesc = it },
                        label = { Text("Beschreibung / Effekte") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (customName.isNotBlank()) {
                                onAdd(
                                    Item(
                                        id = UUID.randomUUID().toString(),
                                        name = customName,
                                        type = customType,
                                        description = customDesc,
                                        weightLbs = customWeight.toDoubleOrNull() ?: 1.0
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
                    ) {
                        Text("Gegenstand erstellen")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Schließen", color = TextSecondary) }
        }
    )
}
