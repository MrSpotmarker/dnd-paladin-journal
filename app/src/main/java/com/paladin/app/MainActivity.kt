package com.paladin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.paladin.app.data.CharacterRepository
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.DetailInfoDialog
import com.paladin.app.ui.components.EditItemDialog
import com.paladin.app.ui.components.fadingBottomEdge
import com.paladin.app.ui.screens.CharacterCreationScreen
import com.paladin.app.ui.screens.CombatDashboardScreen
import com.paladin.app.ui.screens.InventoryScreen
import com.paladin.app.ui.screens.JournalScreen
import com.paladin.app.ui.screens.LevelUpScreen
import com.paladin.app.ui.screens.SpellbookScreen
import com.paladin.app.ui.theme.ChaunteaGreenContainer
import com.paladin.app.ui.theme.DarkNavyBackground
import com.paladin.app.ui.theme.PaladinAppTheme
import com.paladin.app.ui.theme.PaladinGold
import com.paladin.app.ui.theme.TextSecondary

enum class AppTab(val title: String, val icon: ImageVector) {
    SHEET("Sheet", Icons.Default.Shield),
    SPELLS("Zauber", Icons.Default.AutoStories),
    INVENTORY("Inventar", Icons.Default.Backpack),
    JOURNAL("Tagebuch", Icons.Default.Book),
    SETTINGS("Stufe & DM", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CharacterRepository(applicationContext)

        setContent {
            PaladinAppTheme {
                val viewModel = remember { CharacterViewModel(repository) }
                val character by viewModel.character.collectAsState()
                val activeDetail by viewModel.activeDetail.collectAsState()
                val editingItem by viewModel.editingItem.collectAsState()
                var selectedTab by remember { mutableStateOf(AppTab.SHEET) }

                activeDetail?.let { detailItem ->
                    DetailInfoDialog(
                        item = detailItem,
                        onDismiss = viewModel::dismissDetail,
                        onEditItem = { itemToEdit ->
                            viewModel.startEditingItem(itemToEdit)
                        }
                    )
                }

                editingItem?.let { itemToEdit ->
                    EditItemDialog(
                        item = itemToEdit,
                        onDismiss = viewModel::stopEditingItem,
                        onSave = { updatedItem ->
                            viewModel.updateItem(updatedItem)
                        },
                        onSaveAsCopy = { newItem ->
                            viewModel.addItem(newItem)
                        }
                    )
                }

                if (!character.hasCompletedCreation) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        CharacterCreationScreen(viewModel = viewModel)
                    }
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Surface(
                                color = DarkNavyBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                                        .height(52.dp)
                                        .selectableGroup(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppTab.entries.forEach { tab ->
                                        NavigationBarItem(
                                            selected = selectedTab == tab,
                                            onClick = { selectedTab = tab },
                                            icon = {
                                                Icon(tab.icon, contentDescription = tab.title)
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = PaladinGold,
                                                unselectedIconColor = TextSecondary,
                                                indicatorColor = ChaunteaGreenContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .fadingBottomEdge(fadeHeight = 16.dp)
                        ) {
                            when (selectedTab) {
                                AppTab.SHEET -> CombatDashboardScreen(viewModel = viewModel)
                                AppTab.SPELLS -> SpellbookScreen(viewModel = viewModel)
                                AppTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                                AppTab.JOURNAL -> JournalScreen(viewModel = viewModel)
                                AppTab.SETTINGS -> LevelUpScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
