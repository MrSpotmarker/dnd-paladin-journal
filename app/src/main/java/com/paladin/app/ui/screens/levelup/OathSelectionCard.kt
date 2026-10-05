package com.paladin.app.ui.screens.levelup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.PaladinSubclass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun OathSelectionCard(
    selectedLevel: Int,
    currentOath: String?,
    onOathSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedLevel < 3) return

    val standardOaths = remember {
        PaladinSubclass.entries.map {
            it.displayName to "${it.germanTitle}: ${it.channelDivinityOptions.joinToString(", ")} • ${it.summary}"
        }
    }

    var isCustomOath by remember(currentOath) {
        mutableStateOf(currentOath != null && standardOaths.none { it.first == currentOath })
    }
    var selectedStandardOath by remember(currentOath) {
        mutableStateOf(currentOath?.takeIf { oath -> standardOaths.any { it.first == oath } } ?: PaladinSubclass.DEVOTION.displayName)
    }
    var customOathInput by remember(currentOath) {
        mutableStateOf(if (isCustomOath) currentOath ?: "" else "")
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalDivider(color = BorderDark)
        Text(
            text = "🛡️ Heiliger Eid (Sacred Oath - Stufe 3):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PaladinGold
        )
        Text(
            text = "Wähle genau einen Unterklassen-Eid (Single-Select):",
            fontSize = 11.sp,
            color = TextSecondary
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            standardOaths.forEach { (oathTitle, description) ->
                val isSelected = !isCustomOath && selectedStandardOath == oathTitle
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isCustomOath = false
                            selectedStandardOath = oathTitle
                            onOathSelected(oathTitle)
                        }
                        .padding(vertical = 2.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            isCustomOath = false
                            selectedStandardOath = oathTitle
                            onOathSelected(oathTitle)
                        }
                    )
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = oathTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PaladinGold else TextPrimary
                        )
                        Text(
                            text = description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Custom Oath Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isCustomOath = true }
            ) {
                RadioButton(
                    selected = isCustomOath,
                    onClick = { isCustomOath = true }
                )
                Text("Eigener Eid (Custom Oath)", fontSize = 13.sp, color = TextPrimary)
            }

            if (isCustomOath) {
                OutlinedTextField(
                    value = customOathInput,
                    onValueChange = {
                        customOathInput = it
                        onOathSelected(it.ifBlank { null })
                    },
                    label = { Text("Name deines eigenen Eids") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 8.dp),
                    singleLine = true
                )
            }
        }
    }
}
