package com.paladin.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Spell
import com.paladin.app.ui.theme.*

@Composable
fun PreparedSpellsDialog(
    allSpells: List<Spell>,
    currentPreparedIds: Set<String>,
    maxPreparedAllowed: Int,
    characterLevel: Int,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit,
    onShowDetail: (DetailItem) -> Unit = {}
) {
    var selectedIds by remember { mutableStateOf(currentPreparedIds) }
    var selectedLevelFilter by remember { mutableStateOf<Int?>(null) }

    val isOverLimit = selectedIds.size > maxPreparedAllowed

    // Only show spells up to the highest slot level the character can cast
    val maxSpellLevel = when {
        characterLevel >= 17 -> 5
        characterLevel >= 13 -> 4
        characterLevel >= 9 -> 3
        characterLevel >= 5 -> 2
        else -> 1
    }

    val availableSpells = remember(allSpells, maxSpellLevel) {
        allSpells.filter { it.level <= maxSpellLevel }
    }

    val filteredSpells = remember(availableSpells, selectedLevelFilter) {
        if (selectedLevelFilter == null) availableSpells
        else availableSpells.filter { it.level == selectedLevelFilter }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkNavyBackground),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title and Limit Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Zauber vorbereiten",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Täglich nach einer Langen Rast wählbar",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = if (isOverLimit) HealthRed.copy(alpha = 0.2f) else ProficiencyGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isOverLimit) HealthRed else ProficiencyGreen
                        )
                    ) {
                        Text(
                            text = "${selectedIds.size} / $maxPreparedAllowed",
                            color = if (isOverLimit) HealthRed else ProficiencyGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Level Filter Chips
                if (maxSpellLevel > 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedLevelFilter == null,
                            onClick = { selectedLevelFilter = null },
                            label = { Text("Alle", fontSize = 11.sp) }
                        )
                        (1..maxSpellLevel).forEach { lvl ->
                            FilterChip(
                                selected = selectedLevelFilter == lvl,
                                onClick = { selectedLevelFilter = if (selectedLevelFilter == lvl) null else lvl },
                                label = { Text("Grad $lvl", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderDark)

                // Spell list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSpells, key = { it.id }) { spell ->
                        val isSelected = spell.id in selectedIds
                        @OptIn(ExperimentalFoundationApi::class)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        selectedIds = if (isSelected) {
                                            selectedIds - spell.id
                                        } else {
                                            selectedIds + spell.id
                                        }
                                    },
                                    onLongClick = { onShowDetail(DetailItem.SpellInfo(spell)) }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SurfaceCardHighlight else SurfaceCard
                            ),
                            border = if (isSelected) {
                                androidx.compose.foundation.BorderStroke(1.dp, PaladinGold)
                            } else null,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = spell.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PaladinGoldBright else TextPrimary
                                        )
                                        if (spell.isConcentration) {
                                            Surface(
                                                color = SmiteBlue.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "K",
                                                    fontSize = 10.sp,
                                                    color = SmiteBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "${spell.level}. Grad • ${spell.castingTime} • ${spell.range}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = spell.description.take(100) + if (spell.description.length > 100) "..." else "",
                                        fontSize = 11.sp,
                                        color = TextSecondary.copy(alpha = 0.8f),
                                        maxLines = 2
                                    )
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) {
                                            selectedIds + spell.id
                                        } else {
                                            selectedIds - spell.id
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PaladinGold,
                                        checkmarkColor = DarkNavyBackground
                                    )
                                )
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Abbrechen")
                    }

                    Button(
                        onClick = { onSave(selectedIds) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PaladinGold,
                            contentColor = DarkNavyBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
