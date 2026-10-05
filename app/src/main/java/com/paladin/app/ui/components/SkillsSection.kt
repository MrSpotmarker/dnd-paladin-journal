package com.paladin.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.Skill
import com.paladin.app.ui.theme.*

@Composable
fun SkillsSection(
    isExpanded: Boolean,
    skillModifiers: Map<Skill, Int>,
    proficientSkills: Set<Skill>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎲 Fertigkeiten & Skill Checks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaladinGold
                    )
                    val passivePerception = 10 + (skillModifiers[Skill.PERCEPTION] ?: 0)
                    Text(
                        text = "Passive Wahrnehmung: $passivePerception",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                HorizontalDivider(color = BorderDark)

                val allSkills = Skill.entries
                val firstHalf = allSkills.take(9)
                val secondHalf = allSkills.drop(9)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        firstHalf.forEach { skill ->
                            SkillCheckRow(
                                skill = skill,
                                modifier = skillModifiers[skill] ?: 0,
                                isProficient = skill in proficientSkills
                            )
                        }
                    }

                    // Right column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        secondHalf.forEach { skill ->
                            SkillCheckRow(
                                skill = skill,
                                modifier = skillModifiers[skill] ?: 0,
                                isProficient = skill in proficientSkills
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillCheckRow(
    skill: Skill,
    modifier: Int,
    isProficient: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isProficient) "⭐" else "○",
                fontSize = 10.sp,
                color = if (isProficient) PaladinGold else TextSecondary.copy(alpha = 0.5f)
            )
            Text(
                text = "${skill.displayName} (${skill.ability.abbreviation})",
                fontSize = 11.sp,
                fontWeight = if (isProficient) FontWeight.Bold else FontWeight.Normal,
                color = if (isProficient) TextPrimary else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val modColor = when {
            modifier > 0 -> if (isProficient) PaladinGold else ProficiencyGreen
            modifier < 0 -> HealthRed
            else -> TextSecondary
        }

        Text(
            text = Ability.formatModifier(modifier),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = modColor,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
