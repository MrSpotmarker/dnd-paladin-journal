package com.paladin.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.model.DmOverrides
import com.paladin.app.ui.theme.*

@Composable
fun DmOverrideDialog(
    currentOverrides: DmOverrides,
    onDismiss: () -> Unit,
    onSave: (DmOverrides) -> Unit
) {
    var acOverrideStr by remember { mutableStateOf(currentOverrides.acOverride?.toString() ?: "") }
    var acBonusStr by remember { mutableStateOf(currentOverrides.acBonus.toString()) }
    var hpMaxBonusStr by remember { mutableStateOf(currentOverrides.hpMaxBonus.toString()) }
    var saveBonusStr by remember { mutableStateOf(currentOverrides.savingThrowBonus.toString()) }
    var spellDcBonusStr by remember { mutableStateOf(currentOverrides.spellDcBonus.toString()) }
    var notes by remember { mutableStateOf(currentOverrides.notes) }

    // Ability bonuses
    var strBonusStr by remember { mutableStateOf((currentOverrides.abilityBonuses[Ability.STRENGTH] ?: 0).toString()) }
    var chaBonusStr by remember { mutableStateOf((currentOverrides.abilityBonuses[Ability.CHARISMA] ?: 0).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚡ DM Override & Hausregeln",
                color = PaladinGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Hier kannst du automatische Berechnungen manuell überschreiben, falls dein DM spontane Sonderboni, Flüche oder Artefakte vergibt.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = acOverrideStr,
                    onValueChange = { acOverrideStr = it },
                    label = { Text("Fester AC-Wert (Override)") },
                    placeholder = { Text("z.B. 22 (überschreibt Rüstung)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = acBonusStr,
                        onValueChange = { acBonusStr = it },
                        label = { Text("AC Bonus (+/-)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hpMaxBonusStr,
                        onValueChange = { hpMaxBonusStr = it },
                        label = { Text("HP Max Bonus") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = saveBonusStr,
                        onValueChange = { saveBonusStr = it },
                        label = { Text("Save Bonus") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = spellDcBonusStr,
                        onValueChange = { spellDcBonusStr = it },
                        label = { Text("Spell DC Bonus") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Attributs-Zusatzboni:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGold
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = strBonusStr,
                        onValueChange = { strBonusStr = it },
                        label = { Text("Stärke Bonus") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = chaBonusStr,
                        onValueChange = { chaBonusStr = it },
                        label = { Text("Charisma Bonus") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notizen / DM Begründung") },
                    placeholder = { Text("z.B. Segen von Helm, DM Hausregel...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                TextButton(
                    onClick = {
                        acOverrideStr = ""
                        acBonusStr = "0"
                        hpMaxBonusStr = "0"
                        saveBonusStr = "0"
                        spellDcBonusStr = "0"
                        strBonusStr = "0"
                        chaBonusStr = "0"
                        notes = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔄 Alle Overrides auf Standard zurücksetzen", color = HealthRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val abilityBonuses = mutableMapOf<Ability, Int>()
                    strBonusStr.toIntOrNull()?.let { if (it != 0) abilityBonuses[Ability.STRENGTH] = it }
                    chaBonusStr.toIntOrNull()?.let { if (it != 0) abilityBonuses[Ability.CHARISMA] = it }

                    val overrides = DmOverrides(
                        acOverride = acOverrideStr.toIntOrNull(),
                        acBonus = acBonusStr.toIntOrNull() ?: 0,
                        hpMaxBonus = hpMaxBonusStr.toIntOrNull() ?: 0,
                        savingThrowBonus = saveBonusStr.toIntOrNull() ?: 0,
                        spellDcBonus = spellDcBonusStr.toIntOrNull() ?: 0,
                        abilityBonuses = abilityBonuses,
                        notes = notes
                    )
                    onSave(overrides)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PaladinGold, contentColor = DarkNavyBackground)
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = TextSecondary)
            }
        }
    )
}
