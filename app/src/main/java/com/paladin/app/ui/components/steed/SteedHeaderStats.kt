package com.paladin.app.ui.components.steed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGoldBright
import com.paladin.app.ui.theme.SurfaceCardHighlight
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun HeaderStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceCardHighlight)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PaladinGoldBright)
    }
}

@Composable
fun SteedDurationBanner(modifier: Modifier = Modifier) {
    Surface(
        color = SurfaceCardHighlight.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderDark.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("⏳", fontSize = 14.sp)
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = "Dauer: Dauerhaft (kein Zeitlimit)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PaladinGoldBright
                )
                Text(
                    text = "Bleibt auch über kurze & lange Rasten an deiner Seite, bis es auf 0 HP fällt oder du es als Bonus-Aktion entlässt.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
