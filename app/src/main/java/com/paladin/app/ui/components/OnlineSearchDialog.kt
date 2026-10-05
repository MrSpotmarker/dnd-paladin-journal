package com.paladin.app.ui.components

import androidx.compose.animation.*
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.paladin.app.data.online.OnlineSearchCategory
import com.paladin.app.data.online.OnlineSearchResultItem
import com.paladin.app.data.online.OnlineSearchState
import com.paladin.app.model.DetailItem
import com.paladin.app.model.Spell
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

private enum class SearchTabMode(val label: String, val icon: String) {
    ONLINE_API("🌐 SRD Live-Suche", "🌐"),
    WIKI_TEXT("📋 Wiki / Text-Import", "📋")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnlineSearchDialog(
    viewModel: CharacterViewModel,
    initialCategory: OnlineSearchCategory = OnlineSearchCategory.ALL,
    onDismiss: () -> Unit
) {
    var activeTabMode by remember { mutableStateOf(SearchTabMode.ONLINE_API) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var searchQuery by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()

    // For Wiki / Raw Text Import
    var rawTextImport by remember { mutableStateOf("") }
    var parsedSuccessSpell by remember { mutableStateOf<Spell?>(null) }
    var parseError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = {
            viewModel.clearOnlineSearch()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PaladinGold.copy(alpha = 0.6f)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌐", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "D&D Online-Suche & Import",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PaladinGold
                            )
                            Text(
                                text = "Open5e SRD & Wiki-Schnellimport",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            viewModel.clearOnlineSearch()
                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextSecondary)
                    }
                }

                // Mode Tabs
                TabRow(
                    selectedTabIndex = activeTabMode.ordinal,
                    containerColor = SurfaceCardHighlight,
                    contentColor = PaladinGold,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    SearchTabMode.entries.forEach { mode ->
                        Tab(
                            selected = activeTabMode == mode,
                            onClick = { activeTabMode = mode },
                            text = {
                                Text(
                                    text = mode.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (activeTabMode == mode) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                HorizontalDivider(color = BorderDark)

                // Tab Content
                when (activeTabMode) {
                    SearchTabMode.ONLINE_API -> {
                        OnlineApiSearchContent(
                            viewModel = viewModel,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            selectedCategory = selectedCategory,
                            onCategoryChange = { newCat ->
                                selectedCategory = newCat
                                if (searchQuery.isNotBlank()) {
                                    viewModel.searchOnline(searchQuery, newCat)
                                }
                            },
                            searchState = searchState
                        )
                    }
                    SearchTabMode.WIKI_TEXT -> {
                        WikiTextImportContent(
                            viewModel = viewModel,
                            rawText = rawTextImport,
                            onTextChange = {
                                rawTextImport = it
                                parseError = null
                                parsedSuccessSpell = null
                            },
                            parsedSuccessSpell = parsedSuccessSpell,
                            parseError = parseError,
                            onParseAndImport = {
                                if (rawTextImport.isBlank()) {
                                    parseError = "Bitte füge zuerst den Text eines Zaubers oder Gegenstands ein."
                                } else {
                                    val parsed = viewModel.parseAndImportRawSpell(rawTextImport)
                                    if (parsed != null) {
                                        parsedSuccessSpell = parsed
                                        parseError = null
                                    } else {
                                        parseError = "Text konnte nicht automatisch als Zauberspruch erkannt werden."
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.OnlineApiSearchContent(
    viewModel: CharacterViewModel,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: OnlineSearchCategory,
    onCategoryChange: (OnlineSearchCategory) -> Unit,
    searchState: OnlineSearchState
) {
    // Search Field
    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            onQueryChange(it)
            if (it.length >= 2) {
                viewModel.searchOnline(it, selectedCategory)
            } else if (it.isBlank()) {
                viewModel.clearOnlineSearch()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Z. B. Shield, Sun Blade, Smite, Plate, Alert...", fontSize = 13.sp) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Suchen", tint = PaladinGold) },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = {
                    onQueryChange("")
                    viewModel.clearOnlineSearch()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Löschen", tint = TextSecondary)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (searchQuery.isNotBlank()) {
                viewModel.searchOnline(searchQuery, selectedCategory)
            }
        }),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PaladinGold,
            unfocusedBorderColor = BorderDark,
            focusedContainerColor = DarkNavyBackground,
            unfocusedContainerColor = DarkNavyBackground
        )
    )

    // Category Filter Chips
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OnlineSearchCategory.entries.forEach { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
                selected = isSelected,
                onClick = { onCategoryChange(cat) },
                label = { Text("${cat.icon} ${cat.displayName}", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PaladinGold.copy(alpha = 0.25f),
                    selectedLabelColor = PaladinGoldBright,
                    containerColor = SurfaceCardHighlight,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) PaladinGold else BorderDark
                )
            )
        }
    }

    // Results / State Container
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        when (searchState) {
            is OnlineSearchState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🔍", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Suche nach offiziellen D&D 5e / SRD Inhalten",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Tippe einen Namen auf Englisch ein (z. B. 'Shield', 'Smite', 'Sun Blade', 'Plate', 'Mage Armor').",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                        lineHeight = 16.sp
                    )

                    // Quick suggestion chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("Shield", "Sun Blade", "Flaming Sphere", "Plate", "Alert").forEach { term ->
                            SuggestionChip(
                                onClick = {
                                    onQueryChange(term)
                                    viewModel.searchOnline(term, selectedCategory)
                                },
                                label = { Text(term, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            is OnlineSearchState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = PaladinGold, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Durchsuche Open5e SRD Datenbank...",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            is OnlineSearchState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("⚠️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Suche fehlgeschlagen",
                        fontWeight = FontWeight.Bold,
                        color = HealthRed,
                        fontSize = 14.sp
                    )
                    Text(
                        text = searchState.message,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Button(
                        onClick = { viewModel.searchOnline(searchQuery, selectedCategory) },
                        colors = ButtonDefaults.buttonColors(containerColor = SmiteBlue),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Erneut versuchen", fontSize = 12.sp)
                    }
                }
            }

            is OnlineSearchState.Success -> {
                if (searchState.results.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔎", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Keine Treffer für \"${searchState.query}\"",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Prüfe die englische Schreibweise oder wechsle oben auf 'Wiki / Text-Import'.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(searchState.results, key = { it.id }) { resultItem ->
                            SearchResultCard(
                                item = resultItem,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchResultCard(
    item: OnlineSearchResultItem,
    viewModel: CharacterViewModel
) {
    val character by viewModel.character.collectAsState()
    var justAdded by remember { mutableStateOf(false) }

    val isSpellImported = item is OnlineSearchResultItem.SpellResult &&
            viewModel.isSpellImported(item.spell.id, item.spell.name)
    val isFeatImported = item is OnlineSearchResultItem.FeatResult &&
            viewModel.isFeatImported(item.feat.name)
    val existingItemCount = if (item is OnlineSearchResultItem.ItemResult) {
        character.inventory.firstOrNull { it.name.equals(item.item.name, ignoreCase = true) }?.quantity ?: 0
    } else 0

    val isAddedOnce = isSpellImported || isFeatImported || justAdded

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    when (item) {
                        is OnlineSearchResultItem.SpellResult -> viewModel.showDetail(DetailItem.SpellInfo(item.spell))
                        is OnlineSearchResultItem.ItemResult -> viewModel.showDetail(DetailItem.ItemInfo(item.item))
                        is OnlineSearchResultItem.FeatResult -> viewModel.showDetail(DetailItem.FeatInfo(item.feat))
                    }
                },
                onLongClick = {
                    when (item) {
                        is OnlineSearchResultItem.SpellResult -> viewModel.showDetail(DetailItem.SpellInfo(item.spell))
                        is OnlineSearchResultItem.ItemResult -> viewModel.showDetail(DetailItem.ItemInfo(item.item))
                        is OnlineSearchResultItem.FeatResult -> viewModel.showDetail(DetailItem.FeatInfo(item.feat))
                    }
                }
            ),
        colors = CardDefaults.cardColors(containerColor = DarkNavyBackground),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isAddedOnce || existingItemCount > 0) ProficiencyGreen.copy(alpha = 0.5f) else BorderDark
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(item.icon, fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Surface(
                                color = SurfaceCardHighlight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = item.source,
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = item.subtitle,
                            fontSize = 11.sp,
                            color = PaladinGold
                        )
                    }
                }

                // In-Button Feedback: Changes immediately to "Added"
                when (item) {
                    is OnlineSearchResultItem.SpellResult, is OnlineSearchResultItem.FeatResult -> {
                        if (isAddedOnce) {
                            Surface(
                                color = ProficiencyGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProficiencyGreen)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ProficiencyGreen)
                                    Text("Added", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ProficiencyGreen)
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    justAdded = true
                                    when (item) {
                                        is OnlineSearchResultItem.SpellResult -> viewModel.importSpell(item.spell)
                                        is OnlineSearchResultItem.FeatResult -> viewModel.importFeat(item.feat)
                                        else -> {}
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SmiteBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                val btnText = when (item) {
                                    is OnlineSearchResultItem.SpellResult -> "+ Zauber"
                                    is OnlineSearchResultItem.FeatResult -> "+ Lernen"
                                    else -> "+ Hinzufügen"
                                }
                                Text(btnText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    is OnlineSearchResultItem.ItemResult -> {
                        if (existingItemCount > 0 || justAdded) {
                            Button(
                                onClick = {
                                    justAdded = true
                                    viewModel.importItem(item.item)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProficiencyGreen.copy(alpha = 0.2f),
                                    contentColor = ProficiencyGreen
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProficiencyGreen),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                val count = maxOf(1, existingItemCount)
                                Text("✓ Added ($count) +", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    justAdded = true
                                    viewModel.importItem(item.item)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SmiteBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Inventar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Description preview
            Text(
                text = item.description,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Tippen für volle Details ➔",
                    fontSize = 10.sp,
                    color = SmiteBlue
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.WikiTextImportContent(
    viewModel: CharacterViewModel,
    rawText: String,
    onTextChange: (String) -> Unit,
    parsedSuccessSpell: Spell?,
    parseError: String?,
    onParseAndImport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = SurfaceCardHighlight,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "💡 Nicht im freien SRD oder aus einem Wiki?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Kopiere einfach den Text aus einem Wiki (z. B. Wikidot, Fandom, D&D Beyond) oder Homebrew hier hinein. Der intelligente Parser erkennt automatisch Zaubergrad, Schule, Zauberzeit, Reichweite und Wirkungsdauer!",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                    lineHeight = 15.sp
                )
            }
        }

        OutlinedTextField(
            value = rawText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = {
                Text(
                    text = "Beispiel-Eingabe:\n\nSpirit Shroud\n3rd-level Necromancy\nCasting Time: 1 bonus action\nRange: Self\nComponents: V, S\nDuration: Concentration, up to 1 minute\n\nYou call forth spirits of the dead...",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PaladinGold,
                unfocusedBorderColor = BorderDark,
                focusedContainerColor = DarkNavyBackground,
                unfocusedContainerColor = DarkNavyBackground
            )
        )

        Button(
            onClick = onParseAndImport,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (parsedSuccessSpell != null) ProficiencyGreen else PaladinGold,
                contentColor = DarkNavyBackground
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                if (parsedSuccessSpell != null) Icons.Default.Check else Icons.Default.AutoFixHigh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            val btnText = if (parsedSuccessSpell != null) {
                "✓ Added • Weiteren Zauber analysieren"
            } else {
                "✨ Text analysieren & ins Zauberbuch aufnehmen"
            }
            Text(btnText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        // Error message
        if (parseError != null) {
            Surface(
                color = HealthRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HealthRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ $parseError",
                    color = HealthRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Success confirmation card
        if (parsedSuccessSpell != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ProficiencyGreen.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProficiencyGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🎉 Erfolgreich importiert!",
                        fontWeight = FontWeight.Bold,
                        color = ProficiencyGreen,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Zauber: ${parsedSuccessSpell.name} (Grad ${parsedSuccessSpell.level} ${parsedSuccessSpell.school.displayName})",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Zauberzeit: ${parsedSuccessSpell.castingTime} • Reichweite: ${parsedSuccessSpell.range} • Dauer: ${parsedSuccessSpell.duration}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Der Zauber steht ab sofort im Zauberbuch zur Vorbereitung bereit!",
                        fontSize = 11.sp,
                        color = PaladinGold,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
