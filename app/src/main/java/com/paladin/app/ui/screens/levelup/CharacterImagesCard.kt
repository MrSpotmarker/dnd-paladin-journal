package com.paladin.app.ui.screens.levelup

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paladin.app.R
import com.paladin.app.ui.components.CharacterProfileAvatar
import com.paladin.app.ui.theme.BorderBrass
import com.paladin.app.ui.theme.BorderDark
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.ProficiencyGreen
import com.paladin.app.ui.theme.SurfaceCard
import com.paladin.app.ui.theme.TextPrimary
import com.paladin.app.ui.theme.TextSecondary

@Composable
fun CharacterImagesCard(
    customProfileImagePath: String?,
    customFullImagePath: String?,
    onUpdateProfileImagePath: (String?) -> Unit,
    onUpdateFullImagePath: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Launcher: Profilbild
    val profilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onUpdateProfileImagePath(uri.toString())
        }
    }

    // Launcher: Vollbild
    val fullPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onUpdateFullImagePath(uri.toString())
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBrass.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "🖼️ Charakterbilder",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PaladinGold
            )
            Text(
                text = "Wähle eigene Bilder vom Gerät oder setze auf die Standard-Illustrationen zurück.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            HorizontalDivider(color = BorderDark)

            // Profilbild
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vorschau
                CharacterProfileAvatar(
                    customProfileImagePath = customProfileImagePath,
                    customFullImagePath = customFullImagePath,
                    size = 52
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Profilbild (Tab-Icon)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (customProfileImagePath != null) {
                        Text("✅ Eigenes Bild aktiv", fontSize = 11.sp, color = ProficiencyGreen)
                    } else {
                        Text("Standard-Bild", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(
                        onClick = { profilePickerLauncher.launch(arrayOf("image/*")) },
                        modifier = Modifier.height(36.dp)
                    ) { Text("Ändern", fontSize = 12.sp) }
                    if (customProfileImagePath != null) {
                        OutlinedButton(
                            onClick = { onUpdateProfileImagePath(null) },
                            modifier = Modifier.height(36.dp)
                        ) { Text("Reset", fontSize = 12.sp) }
                    }
                }
            }

            HorizontalDivider(color = BorderDark)

            // Vollbild
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail des Vollbildes
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, PaladinGold, RoundedCornerShape(8.dp))
                ) {
                    if (customFullImagePath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(customFullImagePath.toUri())
                                .crossfade(true)
                                .build(),
                            contentDescription = "Vollbild Vorschau",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.lein),
                            contentDescription = "Vollbild Vorschau",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vollansicht (Klick auf Profilbild)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (customFullImagePath != null) {
                        Text("✅ Eigenes Bild aktiv", fontSize = 11.sp, color = ProficiencyGreen)
                    } else {
                        Text("Standard-Bild", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(
                        onClick = { fullPickerLauncher.launch(arrayOf("image/*")) },
                        modifier = Modifier.height(36.dp)
                    ) { Text("Ändern", fontSize = 12.sp) }
                    if (customFullImagePath != null) {
                        OutlinedButton(
                            onClick = { onUpdateFullImagePath(null) },
                            modifier = Modifier.height(36.dp)
                        ) { Text("Reset", fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}
