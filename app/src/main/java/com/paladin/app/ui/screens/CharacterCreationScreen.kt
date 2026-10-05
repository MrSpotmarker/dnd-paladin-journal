package com.paladin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.model.Skill
import com.paladin.app.model.Species
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.screens.creation.*
import com.paladin.app.ui.theme.*

enum class CreationStep(val title: String) {
    IDENTITY("1. Identität & Volk"),
    ATTRIBUTES("2. Attribute"),
    SKILLS("3. Fertigkeiten"),
    MASTERIES("4. Waffen"),
    SUMMARY("5. Start")
}

@Composable
fun CharacterCreationScreen(
    viewModel: CharacterViewModel,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(CreationStep.IDENTITY) }

    // Step 1: Identity & Species
    var name by remember { mutableStateOf("Sir Valerius") }
    var selectedSpecies by remember { mutableStateOf(Species.HUMAN) }

    // Step 2: Ability Scores
    var standardScores by remember {
        mutableStateOf(
            mapOf(
                Ability.STRENGTH to 15,
                Ability.DEXTERITY to 10,
                Ability.CONSTITUTION to 14,
                Ability.INTELLIGENCE to 8,
                Ability.WISDOM to 12,
                Ability.CHARISMA to 13
            )
        )
    }
    // Background bonuses (2024 rules: +2 to one, +1 to second)
    var plus2Ability by remember { mutableStateOf(Ability.STRENGTH) }
    var plus1Ability by remember { mutableStateOf(Ability.CHARISMA) }

    // Step 3: Skills
    var selectedClassSkills by remember { mutableStateOf(setOf(Skill.ATHLETICS, Skill.RELIGION)) }
    var selectedBackgroundSkills by remember { mutableStateOf(setOf(Skill.INSIGHT, Skill.PERSUASION)) }

    // Step 4: Weapon Masteries (choose 2)
    val availableMasteryWeapons = listOf(
        "Longsword (Sap)",
        "Greatsword (Graze)",
        "Halberd (Cleave)",
        "Warhammer (Push)",
        "Mace (Slow)",
        "Javelin (Slow)",
        "Shortsword (Vex)",
        "Dagger (Nick)",
        "Heavy Crossbow (Push)",
        "Morningstar (Sap)"
    )
    var selectedMasteries by remember { mutableStateOf(setOf("Longsword", "Javelin")) }

    // Calculate final scores
    val finalScores = remember(standardScores, plus2Ability, plus1Ability) {
        AbilityScores(
            strength = (standardScores[Ability.STRENGTH] ?: 10) + (if (plus2Ability == Ability.STRENGTH) 2 else 0) + (if (plus1Ability == Ability.STRENGTH) 1 else 0),
            dexterity = (standardScores[Ability.DEXTERITY] ?: 10) + (if (plus2Ability == Ability.DEXTERITY) 2 else 0) + (if (plus1Ability == Ability.DEXTERITY) 1 else 0),
            constitution = (standardScores[Ability.CONSTITUTION] ?: 10) + (if (plus2Ability == Ability.CONSTITUTION) 2 else 0) + (if (plus1Ability == Ability.CONSTITUTION) 1 else 0),
            intelligence = (standardScores[Ability.INTELLIGENCE] ?: 10) + (if (plus2Ability == Ability.INTELLIGENCE) 2 else 0) + (if (plus1Ability == Ability.INTELLIGENCE) 1 else 0),
            wisdom = (standardScores[Ability.WISDOM] ?: 10) + (if (plus2Ability == Ability.WISDOM) 2 else 0) + (if (plus1Ability == Ability.WISDOM) 1 else 0),
            charisma = (standardScores[Ability.CHARISMA] ?: 10) + (if (plus2Ability == Ability.CHARISMA) 2 else 0) + (if (plus1Ability == Ability.CHARISMA) 1 else 0)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stepper Header
        Text(
            text = "⚔️ Paladin Erschaffung (PHB 2024)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )
        Text(
            text = "Stufe 1 Charakter-Assistent",
            fontSize = 12.sp,
            color = TextSecondary
        )

        // Progress Bar
        val stepIndex = CreationStep.entries.indexOf(currentStep)
        val progress = (stepIndex + 1).toFloat() / CreationStep.entries.size
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = PaladinGold,
            trackColor = SurfaceCardHighlight
        )

        // Step Content
        when (currentStep) {
            CreationStep.IDENTITY -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Name & Erscheinung",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Klasse: Paladin (Stufe 1)\nWürfeltyp: d10 Trefferwürfel\nRüstungsübung: Alle Rüstungen & Schilde\nWaffenübung: Einfache und Kriegswaffen",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name deines Paladins") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        HorizontalDivider(color = BorderDark)

                        Text(
                            text = "Volk / Spezies (PHB 2024)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )

                        // Species Chips FlowRow
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Species.entries.forEach { sp ->
                                FilterChip(
                                    selected = sp == selectedSpecies,
                                    onClick = { selectedSpecies = sp },
                                    label = { Text(sp.displayName, fontSize = 12.sp) }
                                )
                            }
                        }

                        // Selected species traits card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCardHighlight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedSpecies.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PaladinGoldBright
                                    )
                                    Text(
                                        text = "Tempo: ${selectedSpecies.baseSpeedFt} ft",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ProficiencyGreen
                                    )
                                }
                                Text(
                                    text = selectedSpecies.traitsDescription,
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                if (selectedSpecies == Species.HUMAN) {
                                    Text(
                                        text = "✓ Startet mit 1 Heroischen Inspiration (wird bei jeder Rast regeneriert)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PaladinGold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            CreationStep.ATTRIBUTES -> {
                AttributesStepCard(
                    standardScores = standardScores,
                    plus2Ability = plus2Ability,
                    plus1Ability = plus1Ability,
                    onUpdateScore = { ability, newScore ->
                        standardScores = standardScores + (ability to newScore)
                    },
                    onUpdatePlus2 = { plus2Ability = it },
                    onUpdatePlus1 = { plus1Ability = it }
                )
            }

            CreationStep.SKILLS -> {
                SkillsStepCard(
                    selectedClassSkills = selectedClassSkills,
                    selectedBackgroundSkills = selectedBackgroundSkills,
                    onToggleClassSkill = { skill ->
                        selectedClassSkills = if (skill in selectedClassSkills) {
                            selectedClassSkills - skill
                        } else {
                            selectedClassSkills + skill
                        }
                    },
                    onToggleBackgroundSkill = { skill ->
                        selectedBackgroundSkills = if (skill in selectedBackgroundSkills) {
                            selectedBackgroundSkills - skill
                        } else {
                            selectedBackgroundSkills + skill
                        }
                    }
                )
            }

            CreationStep.MASTERIES -> {
                MasteriesStepCard(
                    availableMasteryWeapons = availableMasteryWeapons,
                    selectedMasteries = selectedMasteries,
                    onToggleMastery = { weaponName ->
                        selectedMasteries = if (weaponName in selectedMasteries) {
                            selectedMasteries - weaponName
                        } else {
                            selectedMasteries + weaponName
                        }
                    }
                )
            }

            CreationStep.SUMMARY -> {
                SummaryStepCard(
                    name = name,
                    species = selectedSpecies,
                    finalScores = finalScores,
                    selectedClassSkills = selectedClassSkills,
                    selectedBackgroundSkills = selectedBackgroundSkills,
                    selectedMasteries = selectedMasteries,
                    onCompleteCreation = { startingHp ->
                        viewModel.completeCharacterCreation(
                            name = name,
                            abilityScores = finalScores,
                            skills = selectedClassSkills + selectedBackgroundSkills,
                            weaponMasteries = selectedMasteries.toList(),
                            manualHp = startingHp,
                            species = selectedSpecies
                        )
                    }
                )
            }
        }

        // Stepper Navigation Buttons (Back / Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep != CreationStep.IDENTITY) {
                OutlinedButton(
                    onClick = {
                        val prevIndex = stepIndex - 1
                        if (prevIndex >= 0) currentStep = CreationStep.entries[prevIndex]
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Zurück")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (currentStep != CreationStep.SUMMARY) {
                Button(
                    onClick = {
                        val nextIndex = stepIndex + 1
                        if (nextIndex < CreationStep.entries.size) currentStep = CreationStep.entries[nextIndex]
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
                ) {
                    Text("Weiter")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Weiter", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
