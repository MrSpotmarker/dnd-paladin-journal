package com.paladin.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.model.Ability
import com.paladin.app.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AbilityBox(
    ability: Ability,
    score: Int,
    mod: Int,
    save: Int,
    isProficientSave: Boolean,
    hasAura: Boolean,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isProficientSave) BorderBrass.copy(alpha = 0.7f) else BorderDark.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ability.abbreviation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = Ability.formatModifier(mod),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "$score",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = if (isProficientSave) PaladinGold.copy(alpha = 0.2f) else SurfaceCardHighlight,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Save ${Ability.formatModifier(save)}",
                    fontSize = 10.sp,
                    fontWeight = if (isProficientSave) FontWeight.Bold else FontWeight.Normal,
                    color = if (isProficientSave) PaladinGold else TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
