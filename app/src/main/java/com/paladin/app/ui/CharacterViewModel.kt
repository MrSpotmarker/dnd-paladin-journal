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

    fun useLayOnHands(amount: Int) {
        val currentUsed = character.value.layOnHandsUsed
        val maxPool = calculatedStats.value.maxLayOnHands
        val newUsed = (currentUsed + amount).coerceIn(0, maxPool)
        heal(amount)
        repository.updateCharacter(character.value.copy(layOnHandsUsed = newUsed))
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

    fun updateLevel(newLevel: Int, hpGain: Int, newOath: String? = null) {
        val oldLevel = character.value.level
        val levelDiff = (newLevel - oldLevel).coerceAtLeast(0)
        val addedHp = if (hpGain > 0) hpGain else levelDiff * 6
        val updatedOath = newOath ?: character.value.oath

        val updated = character.value.copy(
            level = newLevel.coerceIn(1, 20),
            oath = updatedOath,
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

    fun exportBackupJson(): String = repository.exportCharacterToJson()

    fun importBackupJson(jsonStr: String): Boolean {
        return repository.importCharacterFromJson(jsonStr).isSuccess
    }
}
