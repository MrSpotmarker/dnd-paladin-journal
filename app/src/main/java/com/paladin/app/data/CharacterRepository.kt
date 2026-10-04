package com.paladin.app.data

import android.content.Context
import com.paladin.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SrdCatalogData(
    val spells: List<Spell> = emptyList(),
    val items: List<Item> = emptyList()
)

class CharacterRepository(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val characterFile = File(context.filesDir, "active_character.json")

    private val _character = MutableStateFlow(createDefaultCharacter())
    val character: StateFlow<CharacterSheet> = _character.asStateFlow()

    private val _srdSpells = MutableStateFlow<List<Spell>>(emptyList())
    val srdSpells: StateFlow<List<Spell>> = _srdSpells.asStateFlow()

    private val _srdItems = MutableStateFlow<List<Item>>(emptyList())
    val srdItems: StateFlow<List<Item>> = _srdItems.asStateFlow()

    init {
        loadSrdCatalog()
        loadSavedCharacter()
    }

    private fun loadSrdCatalog() {
        try {
            val jsonString = context.assets.open("srd_catalog.json").bufferedReader().use { it.readText() }
            val catalog = json.decodeFromString<SrdCatalogData>(jsonString)
            _srdSpells.value = catalog.spells
            _srdItems.value = catalog.items
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSavedCharacter() {
        try {
            if (characterFile.exists()) {
                val jsonStr = characterFile.readText()
                val loaded = json.decodeFromString<CharacterSheet>(jsonStr)
                _character.value = loaded
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateCharacter(updated: CharacterSheet) {
        _character.value = updated
        saveCharacterLocally(updated)
    }

    private fun saveCharacterLocally(char: CharacterSheet) {
        try {
            val jsonStr = json.encodeToString(char)
            characterFile.writeText(jsonStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Exportiert den gesamten Charakter als JSON String für Datei-Backups.
     */
    fun exportCharacterToJson(): String {
        return json.encodeToString(_character.value)
    }

    /**
     * Importiert einen Charakter aus einem JSON-Backup String.
     */
    fun importCharacterFromJson(jsonContent: String): Result<CharacterSheet> {
        return runCatching {
            val imported = json.decodeFromString<CharacterSheet>(jsonContent)
            updateCharacter(imported)
            imported
        }
    }

    fun startNewCharacterCreation() {
        val blank = createDefaultCharacter().copy(hasCompletedCreation = false)
        updateCharacter(blank)
    }

    private fun createDefaultCharacter(): CharacterSheet {
        val longsword = Item(
            id = "start_longsword",
            name = "Longsword",
            type = ItemType.WEAPON,
            description = "Paladin-Standardwaffe mit Sap-Mastery.",
            weightLbs = 3.0,
            damageDice = "1d8",
            damageType = "Slashing",
            isVersatile = true,
            versatileDamageDice = "1d10",
            mastery = WeaponMastery.SAP,
            isEquipped = true
        )

        val chainMail = Item(
            id = "start_chain_mail",
            name = "Kettenhemd",
            type = ItemType.ARMOR,
            armorType = ArmorType.HEAVY,
            baseAc = 16,
            weightLbs = 55.0,
            stealthDisadvantage = true,
            minStrength = 13,
            isEquipped = true
        )

        val shield = Item(
            id = "start_shield",
            name = "Schild",
            type = ItemType.SHIELD,
            armorType = ArmorType.SHIELD,
            baseAc = 2,
            weightLbs = 6.0,
            isEquipped = true
        )

        return CharacterSheet(
            name = "Sir Valerius",
            level = 1,
            oath = null,
            baseAbilityScores = AbilityScores(
                strength = 16,
                dexterity = 10,
                constitution = 14,
                intelligence = 8,
                wisdom = 10,
                charisma = 16
            ),
            currentHp = 12,
            tempHp = 0,
            proficientSkills = setOf(Skill.ATHLETICS, Skill.RELIGION),
            masteredWeaponNames = listOf("Longsword", "Halberd"),
            preparedSpellIds = setOf("srd_bless", "srd_cure_wounds", "srd_paladins_smite", "srd_shield_of_faith"),
            inventory = listOf(longsword, chainMail, shield),
            goldPieces = 25.0
        )
    }
}
