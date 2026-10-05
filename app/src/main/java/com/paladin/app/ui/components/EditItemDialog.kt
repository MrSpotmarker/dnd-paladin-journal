package com.paladin.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.model.*
import com.paladin.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditItemDialog(
    item: Item,
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit,
    onSaveAsCopy: (Item) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var type by remember { mutableStateOf(item.type) }
    var description by remember { mutableStateOf(item.description) }
    var cost by remember { mutableStateOf(item.cost) }
    var weightText by remember { mutableStateOf(if (item.weightLbs % 1.0 == 0.0) item.weightLbs.toInt().toString() else item.weightLbs.toString()) }
    var quantityText by remember { mutableStateOf(item.quantity.toString()) }
    var requiresAttunement by remember { mutableStateOf(item.requiresAttunement) }

    // Weapon properties
    var damageDice by remember { mutableStateOf(item.damageDice) }
    var damageType by remember { mutableStateOf(item.damageType) }
    var isVersatile by remember { mutableStateOf(item.isVersatile) }
    var versatileDamageDice by remember { mutableStateOf(item.versatileDamageDice ?: "1d10") }
    var mastery by remember { mutableStateOf(item.mastery) }
    var isFinesse by remember { mutableStateOf(item.isFinesse) }
    var isLight by remember { mutableStateOf(item.isLight) }
    var isHeavy by remember { mutableStateOf(item.isHeavy) }
    var isReach by remember { mutableStateOf(item.isReach) }
    var isTwoHanded by remember { mutableStateOf(item.isTwoHanded) }

    // Armor properties
    var baseAcText by remember { mutableStateOf(item.baseAc.toString()) }
    var stealthDisadvantage by remember { mutableStateOf(item.stealthDisadvantage) }
    var minStrengthText by remember { mutableStateOf(item.minStrength.toString()) }
    var armorType by remember { mutableStateOf(item.armorType ?: ArmorType.LIGHT) }

    var isSaved by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun buildItem(targetId: String): Item {
        return item.copy(
            id = targetId,
            name = name.trim().ifBlank { item.name },
            type = type,
            description = description.trim(),
            cost = cost.trim().ifBlank { "0 gp" },
            weightLbs = weightText.toDoubleOrNull() ?: item.weightLbs,
            quantity = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: item.quantity,
            requiresAttunement = requiresAttunement,

            // Armor details
            armorType = if (type == ItemType.ARMOR || type == ItemType.SHIELD) armorType else null,
            baseAc = if (type == ItemType.ARMOR || type == ItemType.SHIELD) (baseAcText.toIntOrNull() ?: 0) else 0,
            stealthDisadvantage = if (type == ItemType.ARMOR) stealthDisadvantage else false,
            minStrength = if (type == ItemType.ARMOR) (minStrengthText.toIntOrNull() ?: 0) else 0,

            // Weapon details
            damageDice = if (type == ItemType.WEAPON) damageDice.trim().ifBlank { "1d8" } else item.damageDice,
            damageType = if (type == ItemType.WEAPON) damageType.trim().ifBlank { "Slashing" } else item.damageType,
            isVersatile = if (type == ItemType.WEAPON) isVersatile else false,
            versatileDamageDice = if (type == ItemType.WEAPON && isVersatile) versatileDamageDice.trim() else null,
            mastery = if (type == ItemType.WEAPON) mastery else null,
            isFinesse = if (type == ItemType.WEAPON) isFinesse else false,
            isLight = if (type == ItemType.WEAPON) isLight else false,
            isHeavy = if (type == ItemType.WEAPON) isHeavy else false,
            isReach = if (type == ItemType.WEAPON) isReach else false,
            isTwoHanded = if (type == ItemType.WEAPON) isTwoHanded else false
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(16.dp),
            color = DarkNavyBackground,
            border = BorderStroke(1.dp, PaladinGold.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✏️ Gegenstand anpassen",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Passe Werte, Würfel, Effekte oder DM-Sonderregeln an",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Item Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name des Gegenstands") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaladinGold,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    // Category selection
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Kategorie", fontSize = 12.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ItemType.entries.forEach { cat ->
                                val isConsumable = cat.isConsumableOrPotion
                                FilterChip(
                                    selected = type == cat,
                                    onClick = { type = cat },
                                    label = { Text("${cat.icon} ${cat.displayName}", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (isConsumable) ChaunteaGreen else PaladinGold,
                                        selectedLabelColor = if (isConsumable) androidx.compose.ui.graphics.Color.White else DarkNavyBackground,
                                        containerColor = SurfaceCardHighlight,
                                        labelColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }

                    // Description / Healing notes / DM notes
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Beschreibung / Effekte / DM-Werte") },
                        placeholder = { Text("Z. B. Heilt 1d4 + 3 Trefferpunkte, Spezialboni des Spielleiters, etc.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PaladinGold,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    // Quantity, Weight & Cost Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) quantityText = it },
                            label = { Text("Anzahl") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                unfocusedBorderColor = BorderDark
                            )
                        )

                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("Gewicht (lbs)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                unfocusedBorderColor = BorderDark
                            )
                        )

                        OutlinedTextField(
                            value = cost,
                            onValueChange = { cost = it },
                            label = { Text("Wert") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PaladinGold,
                                unfocusedBorderColor = BorderDark
                            )
                        )
                    }

                    // Weapon Section
                    if (type == ItemType.WEAPON) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCardHighlight)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("⚔️ Waffeneigenschaften & Schaden", fontWeight = FontWeight.Bold, color = PaladinGold, fontSize = 13.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = damageDice,
                                    onValueChange = { damageDice = it },
                                    label = { Text("Schaden (z.B. 1d8, 1d4+3)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = damageType,
                                    onValueChange = { damageType = it },
                                    label = { Text("Schadensart") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            // Quick Damage Type suggestions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Slashing", "Piercing", "Bludgeoning", "Radiant", "Fire", "Poison").forEach { dt ->
                                    FilterChip(
                                        selected = damageType.equals(dt, ignoreCase = true),
                                        onClick = { damageType = dt },
                                        label = { Text(dt, fontSize = 10.sp) }
                                    )
                                }
                            }

                            // Versatile Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Vielseitig (Versatile)", fontSize = 12.sp, color = TextPrimary)
                                Switch(
                                    checked = isVersatile,
                                    onCheckedChange = { isVersatile = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PaladinGold, checkedTrackColor = PaladinGold.copy(alpha = 0.5f))
                                )
                            }

                            if (isVersatile) {
                                OutlinedTextField(
                                    value = versatileDamageDice,
                                    onValueChange = { versatileDamageDice = it },
                                    label = { Text("Zweihändiger Schaden (z. B. 1d10)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            // Weapon Properties Chips
                            Text("Eigenschaften", fontSize = 11.sp, color = TextSecondary)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FilterChip(selected = isFinesse, onClick = { isFinesse = !isFinesse }, label = { Text("Finesse", fontSize = 10.sp) })
                                FilterChip(selected = isLight, onClick = { isLight = !isLight }, label = { Text("Leicht (Light)", fontSize = 10.sp) })
                                FilterChip(selected = isHeavy, onClick = { isHeavy = !isHeavy }, label = { Text("Schwer (Heavy)", fontSize = 10.sp) })
                                FilterChip(selected = isReach, onClick = { isReach = !isReach }, label = { Text("Reichweite (Reach)", fontSize = 10.sp) })
                                FilterChip(selected = isTwoHanded, onClick = { isTwoHanded = !isTwoHanded }, label = { Text("Zweihändig", fontSize = 10.sp) })
                            }

                            // Mastery Selection
                            Text("Waffenmeisterschaft (2024 Mastery)", fontSize = 11.sp, color = TextSecondary)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = mastery == null,
                                    onClick = { mastery = null },
                                    label = { Text("Keine", fontSize = 10.sp) }
                                )
                                WeaponMastery.entries.forEach { wm ->
                                    FilterChip(
                                        selected = mastery == wm,
                                        onClick = { mastery = wm },
                                        label = { Text(wm.propertyName, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // Armor Section
                    if (type == ItemType.ARMOR || type == ItemType.SHIELD) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCardHighlight)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🛡️ Rüstungswerte & Schutz", fontWeight = FontWeight.Bold, color = PaladinGold, fontSize = 13.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = baseAcText,
                                    onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) baseAcText = it },
                                    label = { Text(if (type == ItemType.SHIELD) "RK-Bonus (+2)" else "Basis-RK (AC)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                if (type == ItemType.ARMOR) {
                                    OutlinedTextField(
                                        value = minStrengthText,
                                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*$"""))) minStrengthText = it },
                                        label = { Text("Min. Stärke") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }

                            if (type == ItemType.ARMOR) {
                                Text("Rüstungstyp", fontSize = 11.sp, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(ArmorType.LIGHT to "Leicht", ArmorType.MEDIUM to "Mittel", ArmorType.HEAVY to "Schwer").forEach { (at, label) ->
                                        FilterChip(
                                            selected = armorType == at,
                                            onClick = { armorType = at },
                                            label = { Text(label, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Nachteil auf Heimlichkeit (Stealth)", fontSize = 12.sp, color = TextPrimary)
                                    Switch(
                                        checked = stealthDisadvantage,
                                        onCheckedChange = { stealthDisadvantage = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = HealthRed, checkedTrackColor = HealthRed.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }

                    // Attunement
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceCardHighlight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Magische Einstimmung", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Erfordert magische Einstimmung (Attunement)", fontSize = 10.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = requiresAttunement,
                                onCheckedChange = { requiresAttunement = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = SmiteBlue, checkedTrackColor = SmiteBlue.copy(alpha = 0.5f))
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 10.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!isCopied && !isSaved) {
                                val copy = buildItem(UUID.randomUUID().toString())
                                isCopied = true
                                onSaveAsCopy(copy)
                                coroutineScope.launch {
                                    delay(500)
                                    onDismiss()
                                }
                            }
                        },
                        enabled = !isCopied && !isSaved,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCopied) "✓ Als Kopie angelegt" else "Als Kopie anlegen", fontSize = 11.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss, enabled = !isSaved && !isCopied) {
                            Text("Abbrechen", color = TextSecondary, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (!isSaved && !isCopied) {
                                    val updated = buildItem(item.id)
                                    isSaved = true
                                    onSave(updated)
                                    coroutineScope.launch {
                                        delay(500)
                                        onDismiss()
                                    }
                                }
                            },
                            enabled = !isSaved && !isCopied && name.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSaved) ProficiencyGreen else PaladinGold,
                                contentColor = if (isSaved) androidx.compose.ui.graphics.Color.White else DarkNavyBackground
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            if (isSaved) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✓ Gespeichert", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            } else {
                                Text("Änderungen speichern", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
