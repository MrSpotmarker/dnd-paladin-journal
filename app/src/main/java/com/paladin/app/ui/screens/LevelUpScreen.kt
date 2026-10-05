package com.paladin.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.FeatCatalog
import com.paladin.app.model.PaladinSubclass
import com.paladin.app.model.Species
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.CharacterBackupCard
import com.paladin.app.ui.screens.levelup.AsiDistributionCard
import com.paladin.app.ui.screens.levelup.CharacterImagesCard
import com.paladin.app.ui.screens.levelup.FeatSelectionSection
import com.paladin.app.ui.screens.levelup.OathSelectionCard
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun LevelUpScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    val character by viewModel.character.collectAsState()
    val stats by viewModel.calculatedStats.collectAsState()
    val context = LocalContext.current

    var selectedLevel by remember(character.level) { mutableIntStateOf((character.level + 1).coerceAtMost(20)) }

    // Feats & Fighting Styles
    var selectedFeats by remember(character.feats) {
        mutableStateOf(character.feats.toSet())
    }
    val featLimit = remember(selectedLevel) { FeatCatalog.getStandardFeatLimit(selectedLevel) }
    val isFeatOverrun = selectedFeats.size > featLimit

    // Ability Score Improvement (ASI)
    var modifiedAbilities by remember(character.baseAbilityScores) {
        mutableStateOf(character.baseAbilityScores)
    }

    // Sacred Oath (Level 3+)
    var selectedOath by remember(character.oath) {
        mutableStateOf<String?>(character.oath ?: PaladinSubclass.DEVOTION.displayName)
    }

    var hpMode by remember { mutableStateOf("average") } // "average" or "manual"
    var manualHpGain by remember { mutableStateOf("6") }

    val conMod = stats.modifiers[Ability.CONSTITUTION] ?: 0
    val averageHpGain = (6 + conMod).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Stufenaufstieg & Backup",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )

        // Level Up Wizard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🌟 Level-Up Assistent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Aktuelle Stufe: ${character.level} ➔ Neue Zielstufe:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (selectedLevel > 1) selectedLevel-- },
                        enabled = selectedLevel > 1
                    ) { Text("-") }

                    Text(
                        text = "Stufe $selectedLevel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PaladinGold
                    )

                    FilledTonalButton(
                        onClick = { if (selectedLevel < 20) selectedLevel++ },
                        enabled = selectedLevel < 20
                    ) { Text("+") }
                }

                // Feats & Fighting Styles Selection Section
                FeatSelectionSection(
                    selectedLevel = selectedLevel,
                    selectedFeats = selectedFeats,
                    featLimit = featLimit,
                    isFeatOverrun = isFeatOverrun,
                    onFeatsChanged = { selectedFeats = it },
                    onShowDetail = { viewModel.showDetail(it) }
                )

                // Level 3+: Sacred Oath choice (Single-Select)
                OathSelectionCard(
                    selectedLevel = selectedLevel,
                    currentOath = selectedOath,
                    onOathSelected = { selectedOath = it }
                )

                // Level 4+: Ability Score Improvement (ASI)
                AsiDistributionCard(
                    baseScores = character.baseAbilityScores,
                    modifiedAbilities = modifiedAbilities,
                    selectedLevel = selectedLevel,
                    onScoresChanged = { modifiedAbilities = it }
                )

                HorizontalDivider(color = BorderDark)

                // HP Selection
                Text(
                    text = "Trefferpunkte-Zuwachs:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = hpMode == "average",
                        onClick = { hpMode = "average" },
                        label = { Text("Fester Schnitt ($averageHpGain HP)") }
                    )
                    FilterChip(
                        selected = hpMode == "manual",
                        onClick = { hpMode = "manual" },
                        label = { Text("Selbst gewürfelt") }
                    )
                }

                if (hpMode == "manual") {
                    OutlinedTextField(
                        value = manualHpGain,
                        onValueChange = { manualHpGain = it },
                        label = { Text("Gewürfelter Wert (1d10 + CON)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        val hpGain = if (hpMode == "average") averageHpGain else manualHpGain.toIntOrNull() ?: averageHpGain
                        val resolvedOath = if (selectedLevel >= 3) selectedOath else null
                        val resolvedFightingStyle = selectedFeats.firstOrNull {
                            it.contains("Defense", ignoreCase = true) || it.contains("Dueling", ignoreCase = true)
                        }

                        viewModel.updateLevel(
                            newLevel = selectedLevel,
                            hpGain = hpGain,
                            newOath = resolvedOath,
                            newFightingStyle = resolvedFightingStyle,
                            newFeats = selectedFeats.toList(),
                            newAbilityScores = modifiedAbilities
                        )
                        Toast.makeText(context, "Auf Stufe $selectedLevel aufgestiegen!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Stufe anwenden", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Species / Volk Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👤 Volk & Spezies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    Text(
                        text = "Tempo: ${character.species.baseSpeedFt} ft",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProficiencyGreen
                    )
                }

                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Species.entries.forEach { sp ->
                        FilterChip(
                            selected = sp == character.species,
                            onClick = {
                                viewModel.updateSpecies(sp)
                                Toast.makeText(context, "Volk auf ${sp.displayName} geändert", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text(sp.displayName, fontSize = 12.sp) }
                        )
                    }
                }

                Text(
                    text = character.species.traitsDescription,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // New Character Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "⚔️ Charakter-Verwaltung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Text(
                    text = "Möchtest du einen neuen Paladin von Stufe 1 an erstellen?",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedButton(
                    onClick = { viewModel.startNewCharacterCreation() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Neuen Stufe-1-Paladin erschaffen")
                }
            }
        }

        // Image Customization Card
        CharacterImagesCard(
            customProfileImagePath = character.customProfileImagePath,
            customFullImagePath = character.customFullImagePath,
            onUpdateProfileImagePath = viewModel::updateProfileImagePath,
            onUpdateFullImagePath = viewModel::updateFullImagePath
        )

        // Backup Export / Import Card (.paladin Archiv & JSON)
        CharacterBackupCard(viewModel = viewModel)
    }
}
