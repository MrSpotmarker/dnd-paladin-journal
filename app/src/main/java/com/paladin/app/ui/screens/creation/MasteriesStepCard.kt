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
import com.paladin.app.ui.theme.*

@Composable
fun MasteriesStepCard(
    availableMasteryWeapons: List<String>,
    selectedMasteries: Set<String>,
    onToggleMastery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMasteryOverrun = selectedMasteries.size > 2

    Card(
        modifier = modifier.fillMaxWidth(),
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
                        .clickable { onToggleMastery(weaponName) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleMastery(weaponName) }
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
