package com.paladin.app.ui.screens.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Skill
import com.paladin.app.ui.theme.*

@Composable
fun SkillsStepCard(
    selectedClassSkills: Set<Skill>,
    selectedBackgroundSkills: Set<Skill>,
    onToggleClassSkill: (Skill) -> Unit,
    onToggleBackgroundSkill: (Skill) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSkillsCount = (selectedClassSkills + selectedBackgroundSkills).size
    val isClassSkillsOverrun = selectedClassSkills.size > 2
    val isBackgroundSkillsOverrun = selectedBackgroundSkills.size > 3
    val isTotalOverrun = totalSkillsCount > 5

    Card(
        modifier = modifier.fillMaxWidth(),
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
                        .clickable { onToggleClassSkill(skill) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleClassSkill(skill) }
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
                        .clickable { onToggleBackgroundSkill(skill) }
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleBackgroundSkill(skill) }
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
