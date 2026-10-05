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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paladin.app.data.CharacterRepository
import com.paladin.app.ui.CharacterViewModel
import com.paladin.app.ui.components.DetailInfoDialog
import com.paladin.app.ui.components.EditItemDialog
import com.paladin.app.ui.screens.*
import com.paladin.app.ui.theme.*

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
                            NavigationBar(
                                containerColor = DarkNavyBackground,
                                tonalElevation = 4.dp
                            ) {
                                AppTab.entries.forEach { tab ->
                                    NavigationBarItem(
                                        selected = selectedTab == tab,
                                        onClick = { selectedTab = tab },
                                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                                        label = { 
                                            Text(
                                                tab.title,
                                                maxLines = 1,
                                                fontSize = 10.sp,
                                                fontWeight = if (selectedTab == tab) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                            ) 
                                        },
                                        alwaysShowLabel = false,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = PaladinGold,
                                            selectedTextColor = PaladinGold,
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary,
                                            indicatorColor = ChaunteaGreenContainer
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        when (selectedTab) {
                            AppTab.SHEET -> CombatDashboardScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppTab.SPELLS -> SpellbookScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppTab.INVENTORY -> InventoryScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppTab.JOURNAL -> JournalScreen(
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
}
