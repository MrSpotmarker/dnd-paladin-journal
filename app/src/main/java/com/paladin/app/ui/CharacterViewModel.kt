package com.paladin.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paladin.app.data.CharacterRepository
import com.paladin.app.engine.CharacterStatsEngine
import com.paladin.app.engine.RestService
import com.paladin.app.model.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CharacterViewModel(private val repository: CharacterRepository) : ViewModel() {

    val character: StateFlow<CharacterSheet> = repository.character

    val calculatedStats: StateFlow<CalculatedStats> = character.map { char ->
        CharacterStatsEngine.calculate(char)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CharacterStatsEngine.calculate(character.value)
    )

    val srdSpells: StateFlow<List<Spell>> = repository.srdSpells
    val srdItems: StateFlow<List<Item>> = repository.srdItems

    fun takeDamage(amount: Int) {
        val updated = RestService.applyDamage(character.value, amount)
        repository.updateCharacter(updated)
    }

    fun heal(amount: Int) {
        val maxHp = calculatedStats.value.maxHp
        val updated = RestService.applyHealing(character.value, amount, maxHp)
        repository.updateCharacter(updated)
    }

    fun setTempHp(amount: Int) {
        repository.updateCharacter(character.value.copy(tempHp = amount.coerceAtLeast(0)))
    }

    fun addTempHp(amount: Int) {
        val currentTemp = character.value.tempHp
        repository.updateCharacter(character.value.copy(tempHp = (currentTemp + amount).coerceAtLeast(0)))
    }

    fun useLayOnHands(amount: Int) {
        val currentUsed = character.value.layOnHandsUsed
        val maxPool = calculatedStats.value.maxLayOnHands
        val newUsed = (currentUsed + amount).coerceIn(0, maxPool)
        val maxHp = calculatedStats.value.maxHp
        val healed = RestService.applyHealing(character.value, amount, maxHp)
        repository.updateCharacter(healed.copy(layOnHandsUsed = newUsed))
    }

    fun cureConditionLayOnHands() {
        val remaining = calculatedStats.value.remainingLayOnHands
        if (remaining >= 5) {
            val currentUsed = character.value.layOnHandsUsed
            repository.updateCharacter(character.value.copy(layOnHandsUsed = currentUsed + 5))
        }
    }

    fun useSpellSlot(level: Int) {
        val currentUsages = character.value.spellSlotUsages.toMutableMap()
        val current = currentUsages[level] ?: 0
        val maxSlots = calculatedStats.value.spellSlots.firstOrNull { it.level == level }?.maxSlots ?: 0
        if (current < maxSlots) {
            currentUsages[level] = current + 1
            repository.updateCharacter(character.value.copy(spellSlotUsages = currentUsages))
        }
    }

    fun restoreSpellSlot(level: Int) {
        val currentUsages = character.value.spellSlotUsages.toMutableMap()
        val current = currentUsages[level] ?: 0
        if (current > 0) {
            currentUsages[level] = current - 1
            repository.updateCharacter(character.value.copy(spellSlotUsages = currentUsages))
        }
    }

    fun useChannelDivinity() {
        val currentUsed = character.value.channelDivinityUsed
        val maxDivinity = calculatedStats.value.maxChannelDivinity
        if (currentUsed < maxDivinity) {
            repository.updateCharacter(character.value.copy(channelDivinityUsed = currentUsed + 1))
        }
    }

    fun restoreChannelDivinity() {
        val currentUsed = character.value.channelDivinityUsed
        if (currentUsed > 0) {
            repository.updateCharacter(character.value.copy(channelDivinityUsed = currentUsed - 1))
        }
    }

    fun performShortRest(hitDiceToSpend: Int, rolledHp: Int) {
        val updated = RestService.performShortRest(
            character = character.value,
            hitDiceToSpend = hitDiceToSpend,
            totalHpHealed = rolledHp,
            maxHp = calculatedStats.value.maxHp
        )
        repository.updateCharacter(updated)
    }

    fun performLongRest() {
        val updated = RestService.performLongRest(
            character = character.value,
            maxHp = calculatedStats.value.maxHp
        )
        repository.updateCharacter(updated)
    }

    fun toggleEquipItem(itemId: String) {
        val updatedInventory = character.value.inventory.map { item ->
            if (item.id == itemId) {
                item.copy(isEquipped = !item.isEquipped)
            } else item
        }
        repository.updateCharacter(character.value.copy(inventory = updatedInventory))
    }

    fun toggleAttuneItem(itemId: String) {
        val updatedInventory = character.value.inventory.map { item ->
            if (item.id == itemId) {
                item.copy(isAttuned = !item.isAttuned)
            } else item
        }
        repository.updateCharacter(character.value.copy(inventory = updatedInventory))
    }

    fun togglePrepareSpell(spellId: String) {
        val currentPrepared = character.value.preparedSpellIds.toMutableSet()
        if (spellId in currentPrepared) {
            currentPrepared.remove(spellId)
        } else {
            val maxAllowed = calculatedStats.value.maxPreparedSpells
            if (currentPrepared.size < maxAllowed) {
                currentPrepared.add(spellId)
            }
        }
        repository.updateCharacter(character.value.copy(preparedSpellIds = currentPrepared))
    }

    fun setPreparedSpells(spellIds: Set<String>) {
        repository.updateCharacter(character.value.copy(preparedSpellIds = spellIds))
    }

    fun toggleBuff(
        buffId: String,
        isConcentration: Boolean = false,
        slotLevelToConsume: Int? = null,
        consumesChannelDivinity: Boolean = false
    ) {
        val currentBuffs = character.value.activeBuffIds.toMutableSet()
        val isActivating = buffId !in currentBuffs

        if (isActivating) {
            // When concentrating on a new spell, end previous concentration spells
            if (isConcentration) {
                val concentrationBuffs = setOf(
                    "srd_bless", "srd_shield_of_faith", "srd_divine_favor",
                    "srd_heroism", "srd_compelled_duel", "srd_searing_smite",
                    "srd_wrathful_smite", "srd_thunderous_smite",
                    "srd_detect_magic", "srd_protection_from_evil_and_good"
                )
                currentBuffs.removeAll { it in concentrationBuffs }
            }

            if (slotLevelToConsume != null) {
                useSpellSlot(slotLevelToConsume)
            }

            if (consumesChannelDivinity) {
                useChannelDivinity()
            }

            currentBuffs.add(buffId)

            // Heroism gives temp HP equal to CHA mod
            if (buffId == "srd_heroism") {
                val chaMod = calculatedStats.value.modifiers[Ability.CHARISMA] ?: 0
                val heroHp = maxOf(1, chaMod)
                if (character.value.tempHp < heroHp) {
                    setTempHp(heroHp)
                }
            }
        } else {
            currentBuffs.remove(buffId)
        }

        repository.updateCharacter(character.value.copy(activeBuffIds = currentBuffs))
    }

    fun deactivateBuff(buffId: String) {
        val currentBuffs = character.value.activeBuffIds.toMutableSet()
        if (currentBuffs.remove(buffId)) {
            repository.updateCharacter(character.value.copy(activeBuffIds = currentBuffs))
        }
    }

    fun castInstantSpell(spellId: String, slotLevel: Int = 1): Int? {
        useSpellSlot(slotLevel)
        if (spellId == "srd_cure_wounds") {
            val chaMod = calculatedStats.value.modifiers[Ability.CHARISMA] ?: 0
            val rolledHeal = (1..8).random() + (1..8).random() + chaMod
            heal(rolledHeal)
            return rolledHeal
        }
        return null
    }

    fun harnessDivinePower(slotLevel: Int = 1) {
        val remainingDivinity = calculatedStats.value.remainingChannelDivinity
        if (remainingDivinity > 0) {
            useChannelDivinity()
            restoreSpellSlot(slotLevel)
        }
    }

    fun updateLevel(
        newLevel: Int,
        hpGain: Int,
        newOath: String? = null,
        newFightingStyle: String? = null,
        newFeats: List<String>? = null
    ) {
        val oldLevel = character.value.level
        val levelDiff = (newLevel - oldLevel).coerceAtLeast(0)
        val addedHp = if (hpGain > 0) hpGain else levelDiff * 6
        val updatedOath = if (newLevel >= 3) (newOath ?: character.value.oath) else character.value.oath
        val updatedFightingStyle = if (newLevel >= 2) (newFightingStyle ?: character.value.fightingStyle) else character.value.fightingStyle
        val updatedFeats = newFeats ?: character.value.feats

        val updated = character.value.copy(
            level = newLevel.coerceIn(1, 20),
            oath = updatedOath,
            fightingStyle = updatedFightingStyle,
            feats = updatedFeats,
            maxHpManualAdjustment = character.value.maxHpManualAdjustment + (if (hpGain > 0) hpGain - (levelDiff * 6) else 0),
            currentHp = character.value.currentHp + addedHp
        )
        repository.updateCharacter(updated)
    }

    fun updateDmOverrides(overrides: DmOverrides) {
        repository.updateCharacter(character.value.copy(dmOverrides = overrides))
    }

    fun addItem(item: Item) {
        repository.updateCharacter(character.value.copy(inventory = character.value.inventory + item))
    }

    fun removeItem(itemId: String) {
        repository.updateCharacter(character.value.copy(inventory = character.value.inventory.filter { it.id != itemId }))
    }

    fun addCustomSpell(spell: Spell) {
        repository.updateCharacter(character.value.copy(customSpells = character.value.customSpells + spell))
    }

    fun startNewCharacterCreation() {
        repository.startNewCharacterCreation()
    }

    fun updateFeats(feats: List<String>) {
        repository.updateCharacter(character.value.copy(feats = feats))
    }

    fun updateBaseAbilityScores(scores: AbilityScores) {
        repository.updateCharacter(character.value.copy(baseAbilityScores = scores))
    }

    fun getFormattedCurrentDate(): String {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy", java.util.Locale.GERMAN)
            java.time.LocalDate.now().format(formatter)
        } catch (e: Exception) {
            val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN)
            sdf.format(java.util.Date())
        }
    }

    fun addJournalEntry(defaultDate: String = getFormattedCurrentDate()) {
        val newEntry = JournalEntry(
            dateText = defaultDate,
            content = ""
        )
        val updated = listOf(newEntry) + character.value.journalEntries
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun updateJournalEntry(id: String, dateText: String, content: String) {
        val updated = character.value.journalEntries.map { entry ->
            if (entry.id == id) {
                entry.copy(dateText = dateText, content = content)
            } else entry
        }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun deleteJournalEntry(id: String) {
        val updated = character.value.journalEntries.filter { it.id != id }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun completeCharacterCreation(
        name: String,
        abilityScores: AbilityScores,
        skills: Set<Skill>,
        weaponMasteries: List<String>,
        manualHp: Int? = null,
        initialFeats: List<String> = listOf("Alert (Wachsam)", "Savage Attacker (Brutaler Angreifer)", "Dueling (Duellieren)")
    ) {
        val conMod = Ability.calculateModifier(abilityScores.constitution)
        val maxHp = (manualHp ?: (10 + conMod)).coerceAtLeast(1)
        val updated = character.value.copy(
            name = name.ifBlank { "Sir Valerius" },
            level = 1,
            hasCompletedCreation = true,
            baseAbilityScores = abilityScores,
            currentHp = maxHp,
            tempHp = 0,
            hitDiceUsed = 0,
            layOnHandsUsed = 0,
            channelDivinityUsed = 0,
            proficientSkills = skills,
            masteredWeaponNames = weaponMasteries,
            feats = initialFeats,
            fightingStyle = initialFeats.firstOrNull { it.contains("Dueling", ignoreCase = true) || it.contains("Defense", ignoreCase = true) },
            preparedSpellIds = setOf("srd_bless", "srd_cure_wounds", "srd_paladins_smite", "srd_shield_of_faith"),
            spellSlotUsages = emptyMap(),
            dmOverrides = DmOverrides()
        )
        repository.updateCharacter(updated)
    }

    fun exportBackupJson(): String = repository.exportCharacterToJson()

    fun importBackupJson(jsonStr: String): Boolean {
        return repository.importCharacterFromJson(jsonStr).isSuccess
    }
}
