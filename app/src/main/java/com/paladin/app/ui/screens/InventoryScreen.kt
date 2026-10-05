package com.paladin.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.data.online.OnlineSearchCategory
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Item
import com.paladin.app.model.ItemType
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.EditMoneyDialog
import com.paladin.app.ui.components.OnlineSearchDialog
import com.paladin.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

enum class InventoryCategoryFilter(val label: String, val icon: String) {
    ALL("Alle", "📦"),
    WEAPONS("Waffen", "⚔️"),
    ARMOR("Rüstung", "🛡️"),
    CONSUMABLES("Tränke & Pflanzen", "🧪"),
    MAGIC_ITEMS("Magisch", "💍"),
    GEAR("Ausrüstung", "🎒")
}

@Composable
fun InventoryScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()
    val srdItems by viewModel.srdItems.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showOnlineSearchDialog by remember { mutableStateOf(false) }
    var showEditMoneyDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(InventoryCategoryFilter.ALL) }

    val filteredItems = remember(character.inventory, selectedFilter) {
        when (selectedFilter) {
            InventoryCategoryFilter.ALL -> character.inventory
            InventoryCategoryFilter.WEAPONS -> character.inventory.filter { it.type == ItemType.WEAPON }
            InventoryCategoryFilter.ARMOR -> character.inventory.filter { it.type == ItemType.ARMOR || it.type == ItemType.SHIELD }
            InventoryCategoryFilter.CONSUMABLES -> character.inventory.filter { it.type.isConsumableOrPotion }
            InventoryCategoryFilter.MAGIC_ITEMS -> character.inventory.filter { it.type == ItemType.MAGIC_ITEM }
            InventoryCategoryFilter.GEAR -> character.inventory.filter { it.type == ItemType.GEAR }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header: Title and Actions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventar & Ausrüstung",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showOnlineSearchDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SmiteBlue.copy(alpha = 0.2f),
                            contentColor = SmiteBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Online", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    FloatingActionButton(
                        onClick = { showAddItemDialog = true },
                        containerColor = PaladinGold,
                        contentColor = DarkNavyBackground,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Gegenstand hinzufügen", modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Stats Sub-Row: Weight & Money Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gewicht: ${"%.1f".format(stats.totalWeightLbs)} / ${"%.0f".format(stats.carryCapacityLbs)} lbs",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PaladinGold.copy(alpha = 0.15f))
                        .clickable { showEditMoneyDialog = true }
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "🪙 ${if (character.goldPieces % 1.0 == 0.0) character.goldPieces.toInt() else character.goldPieces} GP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text("•", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "🥈 ${character.silverPieces} SP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text("•", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "🥉 ${character.copperPieces} CP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGoldBright
                    )
                    Text("✏️", fontSize = 10.sp)
                }
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

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InventoryCategoryFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text("${filter.icon} ${filter.label}", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PaladinGold,
                        selectedLabelColor = DarkNavyBackground,
                        containerColor = SurfaceCard,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (character.inventory.isEmpty()) "Dein Inventar ist leer." else "Keine Gegenstände in dieser Kategorie.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    InventoryItemRow(
                        item = item,
                        onToggleEquip = { viewModel.toggleEquipItem(item.id) },
                        onToggleAttune = { viewModel.toggleAttuneItem(item.id) },
                        onUpdateQuantity = { newQty -> viewModel.updateItemQuantity(item.id, newQty) },
                        onEdit = { viewModel.startEditingItem(item) },
                        onDelete = { viewModel.removeItem(item.id) },
                        onShowDetail = { viewModel.showDetail(DetailItem.ItemInfo(item)) }
                    )
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            srdItems = srdItems,
            currentInventory = character.inventory,
            onDismiss = { showAddItemDialog = false },
            onAdd = { newItem ->
                viewModel.importItem(newItem)
            }
        )
    }

    if (showOnlineSearchDialog) {
        OnlineSearchDialog(
            viewModel = viewModel,
            initialCategory = OnlineSearchCategory.WEAPONS,
            onDismiss = { showOnlineSearchDialog = false }
        )
    }

    if (showEditMoneyDialog) {
        EditMoneyDialog(
            currentGold = character.goldPieces,
            currentSilver = character.silverPieces,
            currentCopper = character.copperPieces,
            onDismiss = { showEditMoneyDialog = false },
            onSave = { newGold, newSilver, newCopper ->
                viewModel.updateCurrency(newGold, newSilver, newCopper)
                showEditMoneyDialog = false
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
    onUpdateQuantity: (Int) -> Unit,
    onEdit: () -> Unit,
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
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isEquipped) PaladinGold else TextPrimary
                    )
                    if (item.quantity > 1) {
                        Surface(
                            color = PaladinGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = "${item.quantity}x",
                                color = PaladinGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
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

                val totalWeight = item.weightLbs * item.quantity
                val weightStr = if (item.quantity > 1) {
                    "${"%.1f".format(totalWeight)} lbs (${item.weightLbs} lbs/Stk)"
                } else {
                    "${item.weightLbs} lbs"
                }

                Text(
                    text = "${item.type.name} • $weightStr${if (item.description.isNotBlank()) " • ${item.description}" else ""}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Counter [-] [X] [+]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHighlight.copy(alpha = 0.8f))
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 1) onUpdateQuantity(item.quantity - 1) },
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.quantity > 1) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.quantity > 1) PaladinGold else TextPrimary,
                        modifier = Modifier.padding(horizontal = 3.dp)
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text(
                            text = "+",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                    }
                }

                // Equip / Unequip button for equippable items
                if (item.type in setOf(ItemType.WEAPON, ItemType.ARMOR, ItemType.SHIELD, ItemType.MAGIC_ITEM)) {
                    OutlinedButton(
                        onClick = onToggleEquip,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(if (item.isEquipped) "Ablegen" else "Anlegen", fontSize = 11.sp)
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Anpassen",
                        tint = PaladinGold.copy(alpha = 0.85f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Löschen",
                        tint = HealthRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    srdItems: List<Item>,
    currentInventory: List<Item>,
    onDismiss: () -> Unit,
    onAdd: (Item) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var customName by remember { mutableStateOf("") }
    var customType by remember { mutableStateOf(ItemType.CONSUMABLE) }
    var customDesc by remember { mutableStateOf("") }
    var customWeight by remember { mutableStateOf("0.5") }
    var customQuantity by remember { mutableStateOf("1") }
    var isCustomAdded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(srdItems, key = { it.id }) { item ->
                            val countInInventory = currentInventory.firstOrNull { it.name.equals(item.name, ignoreCase = true) }?.quantity ?: 0
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = SurfaceCardHighlight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Text(item.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                        Text("${item.type.icon} ${item.type.name} • ${item.weightLbs} lbs", color = TextSecondary, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onAdd(item.copy(id = UUID.randomUUID().toString())) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (countInInventory > 0) ProficiencyGreen.copy(alpha = 0.2f) else PaladinGold,
                                            contentColor = if (countInInventory > 0) ProficiencyGreen else DarkNavyBackground
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (countInInventory > 0) androidx.compose.foundation.BorderStroke(1.dp, ProficiencyGreen) else null,
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = if (countInInventory > 0) "✓ Added (${countInInventory}x) +" else "+ Hinzufügen",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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

                    // Category selection
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Kategorie", fontSize = 11.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                ItemType.POTION to "🧪 Zaubertrank",
                                ItemType.CONSUMABLE to "🌿 Pflanze / Zutat",
                                ItemType.GEAR to "🎒 Ausrüstung",
                                ItemType.WEAPON to "⚔️ Waffe",
                                ItemType.ARMOR to "🛡️ Rüstung",
                                ItemType.MAGIC_ITEM to "💍 Magisch"
                            ).forEach { (type, label) ->
                                FilterChip(
                                    selected = customType == type,
                                    onClick = { customType = type },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PaladinGold,
                                        selectedLabelColor = DarkNavyBackground,
                                        containerColor = SurfaceCardHighlight,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customQuantity,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) customQuantity = it },
                            label = { Text("Anzahl") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customWeight,
                            onValueChange = { customWeight = it },
                            label = { Text("Gewicht/Stk (lbs)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = customDesc,
                        onValueChange = { customDesc = it },
                        label = { Text("Beschreibung / Effekte") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (customName.isNotBlank() && !isCustomAdded) {
                                onAdd(
                                    Item(
                                        id = UUID.randomUUID().toString(),
                                        name = customName.trim(),
                                        type = customType,
                                        description = customDesc.trim(),
                                        weightLbs = customWeight.toDoubleOrNull() ?: 1.0,
                                        quantity = customQuantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                    )
                                )
                                isCustomAdded = true
                                coroutineScope.launch {
                                    delay(600)
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCustomAdded && customName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCustomAdded) ProficiencyGreen else PaladinGold,
                            contentColor = if (isCustomAdded) androidx.compose.ui.graphics.Color.White else DarkNavyBackground
                        )
                    ) {
                        Text(if (isCustomAdded) "✓ Added" else "Gegenstand erstellen", fontWeight = FontWeight.Bold)
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
