package com.paladin.app.ui.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.AbilityScores
import com.paladin.app.model.Skill
import com.paladin.app.model.Species
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun SummaryStepCard(
    name: String,
    finalScores: AbilityScores,
    selectedClassSkills: Set<Skill>,
    selectedBackgroundSkills: Set<Skill>,
    selectedMasteries: Set<String>,
    onCompleteCreation: (startingHp: Int) -> Unit,
    modifier: Modifier = Modifier,
    species: Species = Species.HUMAN
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
                text = "Stufe 1 ${species.displayName}-Paladin (2024 Regeln)",
                fontSize = 13.sp,
                color = TextSecondary
            )

            HorizontalDivider(color = BorderDark)

            Text(
                text = "• Volk/Spezies: ${species.displayName} (Tempo: ${species.baseSpeedFt} ft)\n" +
                        "• Trefferpunkte: $startingHp HP (10 + CON $conMod)\n" +
                        "• Rüstungsklasse: 18 AC (Kettenhemd + Schild)\n" +
                        "• Handauflegen: 5 HP Pool (Bonus-Aktion)\n" +
                        "• Heroische Inspiration: ${if (species == Species.HUMAN) "1 (Mensch: Resourceful)" else "0"}\n" +
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
