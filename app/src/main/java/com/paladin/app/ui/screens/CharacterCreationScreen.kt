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
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.theme.*

enum class CreationStep(val title: String) {
    IDENTITY("1. Name"),
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

    // Step 1: Identity
    var name by remember { mutableStateOf("Sir Valerius") }

    // Step 2: Ability Scores
    var attributeMethod by remember { mutableStateOf("standard") } // "standard", "pointbuy", "manual"
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

    // Point Buy Cost Calculation (D&D 5e/2024 budget: 27 points)
    fun calculatePointCost(score: Int): Int = when {
        score <= 8 -> 0
        score == 9 -> 1
        score == 10 -> 2
        score == 11 -> 3
        score == 12 -> 4
        score == 13 -> 5
        score == 14 -> 7
        score == 15 -> 9
        score == 16 -> 11
        score == 17 -> 13
        score >= 18 -> 15 + (score - 18) * 2
        else -> 0
    }

    val totalPointsSpent = standardScores.values.sumOf { calculatePointCost(it) }
    val pointsRemaining = 27 - totalPointsSpent
    val isAttributeOverrun = pointsRemaining < 0

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
                    }
                }
            }

            CreationStep.ATTRIBUTES -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Attribute festlegen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )

                        // Budget Indicator Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isAttributeOverrun) HealthRed.copy(alpha = 0.15f)
                                    else PaladinGold.copy(alpha = 0.12f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isAttributeOverrun) HealthRed else PaladinGold.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isAttributeOverrun) "⚠️ Punktebudget überschritten" else "✨ Point-Buy Punkte:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAttributeOverrun) HealthRed else PaladinGold
                                    )
                                    Text(
                                        text = if (isAttributeOverrun)
                                            "${totalPointsSpent} / 27 Pkt (+${-pointsRemaining})"
                                        else
                                            "$pointsRemaining / 27 verbleibend",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAttributeOverrun) HealthRed else ProficiencyGreen
                                    )
                                }
                                Text(
                                    text = if (isAttributeOverrun)
                                        "Du hast mehr als 27 Punkte verteilt. Das Weitergehen ist trotzdem erlaubt (z.B. für gewürfelte Werte oder Hausregeln)."
                                    else
                                        "Standard Point-Buy Budget sind 27 Punkte (Startwert 8 = 0 Pkt).",
                                    fontSize = 11.sp,
                                    color = if (isAttributeOverrun) HealthRed.copy(alpha = 0.9f) else TextSecondary
                                )
                            }
                        }

                        // Ability Score adjustment rows
                        Ability.entries.forEach { ability ->
                            val base = standardScores[ability] ?: 10
                            val bonus = (if (plus2Ability == ability) 2 else 0) + (if (plus1Ability == ability) 1 else 0)
                            val finalScore = base + bonus
                            val mod = Ability.calculateModifier(finalScore)
                            val cost = calculatePointCost(base)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = "${ability.displayName} (${ability.abbreviation})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Basis $base ($cost Pkt) ${if (bonus > 0) "+$bonus Bonus" else ""}",
                                        fontSize = 11.sp,
                                        color = if (bonus > 0) PaladinGold else TextSecondary
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(2f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            if (base > 8) {
                                                standardScores = standardScores + (ability to base - 1)
                                            }
                                        },
                                        enabled = base > 8,
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) { Text("-") }

                                    Text(
                                        text = "$finalScore (${Ability.formatModifier(mod)})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PaladinGold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    FilledTonalButton(
                                        onClick = {
                                            if (base < 18) {
                                                standardScores = standardScores + (ability to base + 1)
                                            }
                                        },
                                        enabled = base < 18,
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) { Text("+") }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "2024 Hintergrund-Boni zuweisen:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )

                        // +2 Picker
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("+2 Bonus auf:", fontSize = 12.sp, color = TextPrimary)
                            var expanded2 by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(onClick = { expanded2 = true }) {
                                    Text(plus2Ability.displayName)
                                }
                                DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                                    Ability.entries.forEach { ab ->
                                        DropdownMenuItem(
                                            text = { Text(ab.displayName) },
                                            onClick = {
                                                plus2Ability = ab
                                                expanded2 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // +1 Picker
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("+1 Bonus auf:", fontSize = 12.sp, color = TextPrimary)
                            var expanded1 by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(onClick = { expanded1 = true }) {
                                    Text(plus1Ability.displayName)
                                }
                                DropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                                    Ability.entries.filter { it != plus2Ability }.forEach { ab ->
                                        DropdownMenuItem(
                                            text = { Text(ab.displayName) },
                                            onClick = {
                                                plus1Ability = ab
                                                expanded1 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            CreationStep.SKILLS -> {
                val totalSkillsCount = (selectedClassSkills + selectedBackgroundSkills).size
                val isClassSkillsOverrun = selectedClassSkills.size > 2
                val isBackgroundSkillsOverrun = selectedBackgroundSkills.size > 3
                val isTotalOverrun = totalSkillsCount > 5

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Fertigkeiten (Proficiencies)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )

                        // Skills Budget Indicator Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isClassSkillsOverrun || isBackgroundSkillsOverrun || isTotalOverrun) HealthRed.copy(alpha = 0.15f)
                                    else PaladinGold.copy(alpha = 0.12f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isClassSkillsOverrun || isBackgroundSkillsOverrun || isTotalOverrun) HealthRed else PaladinGold.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isTotalOverrun) "⚠️ Fertigkeiten-Limit überschritten" else "Fertigkeiten Gesamt:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTotalOverrun) HealthRed else PaladinGold
                                    )
                                    Text(
                                        text = "$totalSkillsCount gewählt (Standard: 4, Mensch: 5)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTotalOverrun) HealthRed else ProficiencyGreen
                                    )
                                }
                                Text(
                                    text = if (isTotalOverrun)
                                        "Du hast mehr als 5 Fertigkeiten gewählt. Überschreitungen sind erlaubt."
                                    else
                                        "Paladin: 2 Klassen-Fertigkeiten | Hintergrund: 2 | Mensch: +1 Bonus-Fertigkeit.",
                                    fontSize = 11.sp,
                                    color = if (isTotalOverrun) HealthRed.copy(alpha = 0.9f) else TextSecondary
                                )
                            }
                        }

                        // Section 1: Class Skills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. Klassen-Fertigkeiten (Paladin):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${selectedClassSkills.size} / 2",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isClassSkillsOverrun) HealthRed else ProficiencyGreen
                            )
                        }

                        Skill.paladinClassSkills.forEach { skill ->
                            val isSelected = skill in selectedClassSkills
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedClassSkills = if (isSelected) {
                                            selectedClassSkills - skill
                                        } else {
                                            selectedClassSkills + skill
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedClassSkills = if (checked) {
                                            selectedClassSkills + skill
                                        } else {
                                            selectedClassSkills - skill
                                        }
                                    }
                                )
                                Text(
                                    text = "${skill.displayName} (${skill.ability.abbreviation})",
                                    fontSize = 13.sp,
                                    color = if (isSelected) PaladinGold else TextPrimary
                                )
                            }
                        }

                        HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 4.dp))

                        // Section 2: Background / Additional Skills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Hintergrund- & Bonus-Fertigkeiten:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${selectedBackgroundSkills.size} / 2 (bis 3 für Mensch)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBackgroundSkillsOverrun) HealthRed else ProficiencyGreen
                            )
                        }

                        Skill.entries.filter { it !in selectedClassSkills }.forEach { skill ->
                            val isSelected = skill in selectedBackgroundSkills
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedBackgroundSkills = if (isSelected) {
                                            selectedBackgroundSkills - skill
                                        } else {
                                            selectedBackgroundSkills + skill
                                        }
                                    }
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedBackgroundSkills = if (checked) {
                                            selectedBackgroundSkills + skill
                                        } else {
                                            selectedBackgroundSkills - skill
                                        }
                                    }
                                )
                                Text(
                                    text = "${skill.displayName} (${skill.ability.abbreviation})",
                                    fontSize = 13.sp,
                                    color = if (isSelected) PaladinGold else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            CreationStep.MASTERIES -> {
                val isMasteryOverrun = selectedMasteries.size > 2

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Waffenmeisterschaften (Weapon Mastery)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )

                        // Mastery Indicator Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isMasteryOverrun) HealthRed.copy(alpha = 0.15f)
                                    else PaladinGold.copy(alpha = 0.12f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isMasteryOverrun) HealthRed else PaladinGold.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isMasteryOverrun) "⚠️ Limit überschritten:" else "Waffenmeisterschaften:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMasteryOverrun) HealthRed else PaladinGold
                                )
                                Text(
                                    text = "${selectedMasteries.size} / 2 gewählt",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMasteryOverrun) HealthRed else ProficiencyGreen
                                )
                            }
                        }

                        availableMasteryWeapons.forEach { weaponEntry ->
                            val weaponName = weaponEntry.substringBefore(" ")
                            val isSelected = weaponName in selectedMasteries

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMasteries = if (isSelected) {
                                            selectedMasteries - weaponName
                                        } else {
                                            selectedMasteries + weaponName
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedMasteries = if (checked) {
                                            selectedMasteries + weaponName
                                        } else {
                                            selectedMasteries - weaponName
                                        }
                                    }
                                )
                                Text(
                                    text = weaponEntry,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PaladinGold else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            CreationStep.SUMMARY -> {
                val conMod = Ability.calculateModifier(finalScores.constitution)
                val startingHp = 10 + conMod

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Zusammenfassung: ${name.ifBlank { "Sir Valerius" }}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PaladinGold
                        )
                        Text(
                            text = "Stufe 1 Paladin (2024 Regeln)",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(color = BorderDark)

                        Text(
                            text = "• Trefferpunkte: $startingHp HP (10 + CON $conMod)\n" +
                                    "• Rüstungsklasse: 18 AC (Kettenhemd + Schild)\n" +
                                    "• Handauflegen: 5 HP Pool (Bonus-Aktion)\n" +
                                    "• Zauberplätze: 2x Grad 1 Slots\n" +
                                    "• Vorbereitete Zauber: 4 (Bless, Cure Wounds, Smite, Shield of Faith)\n" +
                                    "• Waffenmeisterschaften: ${selectedMasteries.joinToString(", ")}\n" +
                                    "• Fertigkeiten: ${(selectedClassSkills + selectedBackgroundSkills).joinToString { it.displayName }}",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.completeCharacterCreation(
                                    name = name,
                                    abilityScores = finalScores,
                                    skills = selectedClassSkills + selectedBackgroundSkills,
                                    weaponMasteries = selectedMasteries.toList(),
                                    manualHp = startingHp
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🛡️ Charakterbogen starten", fontWeight = FontWeight.Bold)
                        }
                    }
                }
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
