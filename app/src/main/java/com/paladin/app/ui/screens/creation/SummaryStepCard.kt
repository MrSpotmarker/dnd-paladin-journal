package com.paladin.app.ui.screens.creation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.model.Skill
import com.paladin.app.ui.theme.*

@Composable
fun SummaryStepCard(
    name: String,
    finalScores: AbilityScores,
    selectedClassSkills: Set<Skill>,
    selectedBackgroundSkills: Set<Skill>,
    selectedMasteries: Set<String>,
    onCompleteCreation: (startingHp: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val conMod = Ability.calculateModifier(finalScores.constitution)
    val startingHp = 10 + conMod

    Card(
        modifier = modifier.fillMaxWidth(),
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
                onClick = { onCompleteCreation(startingHp) },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🛡️ Charakterbogen starten", fontWeight = FontWeight.Bold)
            }
        }
    }
}
