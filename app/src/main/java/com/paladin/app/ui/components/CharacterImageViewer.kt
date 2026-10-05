package com.paladin.app.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paladin.app.R
import com.paladin.app.ui.theme.PaladinGold

/**
 * Circular profile avatar shown in the navigation bar.
 * Tapping it opens an animated full-image dialog.
 *
 * @param customProfileImagePath  URI-String of a user-chosen profile image, or null → use default.
 * @param customFullImagePath     URI-String of a user-chosen full image,    or null → use default.
 */
@Composable
fun CharacterProfileAvatar(
    customProfileImagePath: String?,
    customFullImagePath: String?,
    size: Int = 28,
    modifier: Modifier = Modifier
) {
    var showFullImage by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .border(1.5.dp, PaladinGold, CircleShape)
            .clickable { showFullImage = true }
    ) {
        if (customProfileImagePath != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(customProfileImagePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Profilbild",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.lein_profile),
                contentDescription = "Profilbild",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Animated full-view dialog
    if (showFullImage) {
        CharacterFullImageDialog(
            customFullImagePath = customFullImagePath,
            onDismiss = { showFullImage = false }
        )
    }
}

/**
 * Full-screen animated dialog showing the character's full image.
 */
@Composable
fun CharacterFullImageDialog(
    customFullImagePath: String?,
    onDismiss: () -> Unit
) {
    FullscreenImageDialog(
        imageModel = if (customFullImagePath != null) Uri.parse(customFullImagePath) else R.drawable.lein,
        contentDescription = "Charakterbild",
        onDismiss = onDismiss
    )
}

/**
 * Universal full-screen animated dialog with bounce / spring animation.
 * Accepts any image model supported by Coil (Uri, File, String path, or Int resId).
 */
@Composable
fun FullscreenImageDialog(
    imageModel: Any?,
    contentDescription: String = "Vollansicht",
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.7f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = scaleOut(targetScale = 0.7f) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (imageModel is Int) {
                        Image(
                            painter = painterResource(id = imageModel),
                            contentDescription = contentDescription,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = contentDescription,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
