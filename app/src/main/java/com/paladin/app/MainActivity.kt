package com.paladin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paladin.app.data.CharacterRepository
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.screens.*
import com.paladin.app.ui.theme.PaladinAppTheme
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.SurfaceCard

enum class AppTab(val title: String, val icon: ImageVector) {
    COMBAT("Bogen", Icons.Default.Shield),
    WEAPONS("Waffen", Icons.Default.Gavel),
    INVENTORY("Inventar", Icons.Default.Backpack),
    SPELLS("Zauber", Icons.Default.AutoStories),
    SETTINGS("Stufe & Backup", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CharacterRepository(applicationContext)

        setContent {
            PaladinAppTheme {
                var selectedTab by remember { mutableStateOf(AppTab.COMBAT) }
                val viewModel = remember { CharacterViewModel(repository) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(containerColor = SurfaceCard) {
                            AppTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = { selectedTab = tab },
                                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                                    label = { Text(tab.title) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = PaladinGold,
                                        selectedTextColor = PaladinGold,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        AppTab.COMBAT -> CombatDashboardScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.WEAPONS -> WeaponsScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.INVENTORY -> InventoryScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.SPELLS -> SpellbookScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        AppTab.SETTINGS -> LevelUpScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
