package com.paladin.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paladin.app.data.CharacterRepository
import com.paladin.app.data.online.DndOnlineRepository
import com.paladin.app.data.online.OnlineSearchCategory
import com.paladin.app.data.online.OnlineSearchResultItem
import com.paladin.app.data.online.OnlineSearchState
import com.paladin.app.engine.CharacterStatsEngine
import com.paladin.app.engine.RestService
import com.paladin.app.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val _activeDetail = MutableStateFlow<DetailItem?>(null)
    val activeDetail: StateFlow<DetailItem?> = _activeDetail.asStateFlow()

    private val _editingItem = MutableStateFlow<Item?>(null)
    val editingItem: StateFlow<Item?> = _editingItem.asStateFlow()

    fun showDetail(item: DetailItem) {
        _activeDetail.value = item
    }

    fun dismissDetail() {
        _activeDetail.value = null
    }

    fun startEditingItem(item: Item) {
        _editingItem.value = item
    }

    fun stopEditingItem() {
        _editingItem.value = null
    }

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

    fun cureConditionLayOnHands(condition: Condition = Condition.POISONED) {
        val remaining = calculatedStats.value.remainingLayOnHands
        if (remaining >= 5) {
            val currentUsed = character.value.layOnHandsUsed
            val updatedConditions = character.value.activeConditions - condition
            repository.updateCharacter(
                character.value.copy(
                    layOnHandsUsed = currentUsed + 5,
                    activeConditions = updatedConditions
                )
            )
        }
    }

    fun toggleCondition(condition: Condition) {
        val current = character.value.activeConditions
        val updated = if (condition in current) current - condition else current + condition
        repository.updateCharacter(character.value.copy(activeConditions = updated))
    }

    fun removeCondition(condition: Condition) {
        val current = character.value.activeConditions
        repository.updateCharacter(character.value.copy(activeConditions = current - condition))
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

    fun performShortRest(hitDiceToSpend: Int, rolledHp: Int = 0) {
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

    fun modifyHeroicInspiration(delta: Int) {
        val current = character.value.heroicInspirations
        val updated = (current + delta).coerceAtLeast(0)
        repository.updateCharacter(character.value.copy(heroicInspirations = updated))
    }

    fun setHeroicInspiration(count: Int) {
        repository.updateCharacter(character.value.copy(heroicInspirations = count.coerceAtLeast(0)))
    }

    fun updateGold(amount: Double) {
        repository.updateCharacter(character.value.copy(goldPieces = maxOf(0.0, amount)))
    }

    fun addGold(delta: Double) {
        val current = character.value.goldPieces
        repository.updateCharacter(character.value.copy(goldPieces = maxOf(0.0, current + delta)))
    }

    fun updateCurrency(gold: Double, silver: Int, copper: Int) {
        repository.updateCharacter(
            character.value.copy(
                goldPieces = maxOf(0.0, gold),
                silverPieces = maxOf(0, silver),
                copperPieces = maxOf(0, copper)
            )
        )
    }

    fun updateSpecies(species: Species) {
        repository.updateCharacter(character.value.copy(species = species))
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
        newFeats: List<String>? = null,
        newAbilityScores: AbilityScores? = null
    ) {
        val oldLevel = character.value.level
        val levelDiff = (newLevel - oldLevel).coerceAtLeast(0)
        val addedHp = if (hpGain > 0) hpGain else levelDiff * 6
        val updatedOath = if (newLevel >= 3) (newOath ?: character.value.oath) else character.value.oath
        val updatedFightingStyle = if (newLevel >= 2) (newFightingStyle ?: character.value.fightingStyle) else character.value.fightingStyle
        val updatedFeats = newFeats ?: character.value.feats
        val updatedAbilities = newAbilityScores ?: character.value.baseAbilityScores

        val updated = character.value.copy(
            level = newLevel.coerceIn(1, 20),
            oath = updatedOath,
            fightingStyle = updatedFightingStyle,
            feats = updatedFeats,
            baseAbilityScores = updatedAbilities,
            maxHpManualAdjustment = character.value.maxHpManualAdjustment + (if (hpGain > 0) hpGain - (levelDiff * 6) else 0),
            currentHp = character.value.currentHp + addedHp
        )
        repository.updateCharacter(updated)
    }

    fun summonSteedFree() {
        if (!character.value.freeFindSteedUsed) {
            val maxHp = character.value.steedMaxHpOverride ?: (5 + 10 * character.value.level)
            repository.updateCharacter(
                character.value.copy(
                    freeFindSteedUsed = true,
                    isSteedSummoned = true,
                    steedCurrentHp = maxHp,
                    steedSpecialUsed = false
                )
            )
        }
    }

    fun summonSteedWithSlot(slotLevel: Int = 2) {
        val currentUsages = character.value.spellSlotUsages.toMutableMap()
        val current = currentUsages[slotLevel] ?: 0
        val maxSlots = calculatedStats.value.spellSlots.firstOrNull { it.level == slotLevel }?.maxSlots ?: 0
        if (current < maxSlots) {
            currentUsages[slotLevel] = current + 1
            val maxHp = character.value.steedMaxHpOverride ?: (5 + 10 * character.value.level)
            repository.updateCharacter(
                character.value.copy(
                    spellSlotUsages = currentUsages,
                    isSteedSummoned = true,
                    steedCurrentHp = maxHp,
                    steedSpecialUsed = false
                )
            )
        }
    }

    fun dismissSteed() {
        repository.updateCharacter(character.value.copy(isSteedSummoned = false))
    }

    fun updateSteedHp(currentHp: Int, maxHpOverride: Int? = null) {
        val sheet = character.value
        val effectiveMax = maxHpOverride ?: sheet.steedMaxHpOverride ?: (5 + 10 * sheet.level)
        repository.updateCharacter(
            sheet.copy(
                steedCurrentHp = currentHp.coerceIn(0, effectiveMax),
                steedMaxHpOverride = maxHpOverride
            )
        )
    }

    fun setSteedCreatureType(type: String) {
        repository.updateCharacter(character.value.copy(steedCreatureType = type))
    }

    fun toggleSteedSpecialUsed() {
        repository.updateCharacter(character.value.copy(steedSpecialUsed = !character.value.steedSpecialUsed))
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

    fun updateItemQuantity(itemId: String, quantity: Int) {
        val validQuantity = maxOf(1, quantity)
        val updated = character.value.inventory.map { item ->
            if (item.id == itemId) item.copy(quantity = validQuantity) else item
        }
        repository.updateCharacter(character.value.copy(inventory = updated))
    }

    fun updateItem(updatedItem: Item) {
        val updated = character.value.inventory.map { item ->
            if (item.id == updatedItem.id) updatedItem else item
        }
        repository.updateCharacter(character.value.copy(inventory = updated))

        val currentDetail = _activeDetail.value
        if (currentDetail is DetailItem.ItemInfo && currentDetail.item.id == updatedItem.id) {
            _activeDetail.value = DetailItem.ItemInfo(updatedItem)
        }
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

    fun addJournalEntry(defaultTitle: String = getFormattedCurrentDate()) {
        val newEntry = JournalEntry(
            title = defaultTitle,
            dateText = defaultTitle,
            content = ""
        )
        val updated = listOf(newEntry) + character.value.journalEntries
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun updateJournalEntry(id: String, title: String, content: String) {
        val updated = character.value.journalEntries.map { entry ->
            if (entry.id == id) {
                entry.copy(title = title, dateText = title, content = content)
            } else entry
        }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun deleteJournalEntry(id: String) {
        val target = character.value.journalEntries.find { it.id == id }
        target?.imagePaths?.forEach { path ->
            com.paladin.app.data.JournalImageManager.deleteImageLocally(path)
        }
        val updated = character.value.journalEntries.filter { it.id != id }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun addPhotoToJournalEntry(entryId: String, sourceUri: android.net.Uri, context: android.content.Context) {
        val savedPath = com.paladin.app.data.JournalImageManager.saveImageLocally(context, sourceUri) ?: return
        val updated = character.value.journalEntries.map { entry ->
            if (entry.id == entryId) {
                entry.copy(imagePaths = entry.imagePaths + savedPath)
            } else entry
        }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun removePhotoFromJournalEntry(entryId: String, imagePath: String) {
        com.paladin.app.data.JournalImageManager.deleteImageLocally(imagePath)
        val updated = character.value.journalEntries.map { entry ->
            if (entry.id == entryId) {
                entry.copy(imagePaths = entry.imagePaths.filter { it != imagePath })
            } else entry
        }
        repository.updateCharacter(character.value.copy(journalEntries = updated))
    }

    fun moveJournalEntry(fromIndex: Int, toIndex: Int) {
        val list = character.value.journalEntries.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            repository.updateCharacter(character.value.copy(journalEntries = list))
        }
    }

    fun reorderJournalEntries(newEntries: List<JournalEntry>) {
        repository.updateCharacter(character.value.copy(journalEntries = newEntries))
    }

    fun completeCharacterCreation(
        name: String,
        abilityScores: AbilityScores,
        skills: Set<Skill>,
        weaponMasteries: List<String>,
        manualHp: Int? = null,
        species: Species = Species.HUMAN,
        initialFeats: List<String> = listOf("Alert (Wachsam)", "Savage Attacker (Brutaler Angreifer)", "Dueling (Duellieren)")
    ) {
        val conMod = Ability.calculateModifier(abilityScores.constitution)
        val maxHp = (manualHp ?: (10 + conMod)).coerceAtLeast(1)
        val updated = character.value.copy(
            name = name.ifBlank { "Sir Valerius" },
            level = 1,
            hasCompletedCreation = true,
            species = species,
            heroicInspirations = species.defaultInspirationsOnLongRest,
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

    fun exportArchiveBackup(context: android.content.Context, targetUri: android.net.Uri): Result<Int> {
        return try {
            context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                repository.exportArchiveBackup(outputStream)
            } ?: Result.failure(Exception("Konnte Datei-Stream nicht öffnen"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun createShareableArchive(): Result<java.io.File> {
        return repository.createShareableArchive()
    }

    fun importArchiveBackup(context: android.content.Context, sourceUri: android.net.Uri): Result<CharacterSheet> {
        return try {
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                repository.importArchiveBackup(inputStream)
            } ?: Result.failure(Exception("Konnte Datei-Stream nicht öffnen"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // ONLINE SEARCH & DYNAMIC IMPORT
    // -------------------------------------------------------------
    private val onlineRepository = DndOnlineRepository()

    private val _searchState = MutableStateFlow<OnlineSearchState>(OnlineSearchState.Idle)
    val searchState: StateFlow<OnlineSearchState> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    fun searchOnline(query: String, category: OnlineSearchCategory = OnlineSearchCategory.ALL) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _searchState.value = OnlineSearchState.Idle
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchState.value = OnlineSearchState.Loading
            val result = onlineRepository.search(trimmed, category)
            result.fold(
                onSuccess = { items ->
                    _searchState.value = OnlineSearchState.Success(
                        results = items,
                        query = trimmed,
                        category = category
                    )
                },
                onFailure = { error ->
                    val userFriendlyMsg = when {
                        error is java.net.UnknownHostException -> "Keine Internetverbindung oder API-Server nicht erreichbar."
                        error is java.net.SocketTimeoutException -> "Zeitüberschreitung bei der Online-Anfrage."
                        else -> error.localizedMessage ?: "Fehler bei der Online-Suche."
                    }
                    _searchState.value = OnlineSearchState.Error(userFriendlyMsg)
                }
            )
        }
    }

    fun clearOnlineSearch() {
        searchJob?.cancel()
        _searchState.value = OnlineSearchState.Idle
    }

    fun importSpell(spell: Spell) {
        val currentCustom = character.value.customSpells
        if (currentCustom.none { it.id == spell.id || it.name.equals(spell.name, ignoreCase = true) }) {
            val updated = currentCustom + spell
            repository.updateCharacter(character.value.copy(customSpells = updated))
        }
    }

    fun removeCustomSpell(spellId: String) {
        val updated = character.value.customSpells.filter { it.id != spellId }
        val updatedPrepared = character.value.preparedSpellIds.filter { it != spellId }.toSet()
        repository.updateCharacter(character.value.copy(customSpells = updated, preparedSpellIds = updatedPrepared))
    }

    fun importItem(item: Item) {
        val currentInv = character.value.inventory
        val existingIndex = currentInv.indexOfFirst { it.name.equals(item.name, ignoreCase = true) }
        val updated = if (existingIndex >= 0) {
            currentInv.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(quantity = this[existingIndex].quantity + 1)
            }
        } else {
            currentInv + item
        }
        repository.updateCharacter(character.value.copy(inventory = updated))
    }

    fun importFeat(feat: FeatDefinition) {
        val currentFeats = character.value.feats
        if (currentFeats.none { it.equals(feat.name, ignoreCase = true) }) {
            val updated = currentFeats + feat.name
            repository.updateCharacter(character.value.copy(feats = updated))
        }
    }

    fun parseAndImportRawSpell(rawText: String): Spell? {
        val parsed = onlineRepository.parseRawTextToSpell(rawText)
        if (parsed != null) {
            importSpell(parsed)
        }
        return parsed
    }

    fun isSpellImported(id: String, name: String): Boolean {
        return character.value.customSpells.any { it.id == id || it.name.equals(name, ignoreCase = true) } ||
               srdSpells.value.any { it.id == id || it.name.equals(name, ignoreCase = true) }
    }

    fun isItemImported(id: String, name: String): Boolean {
        return character.value.inventory.any { it.id == id || it.name.equals(name, ignoreCase = true) }
    }

    fun isFeatImported(name: String): Boolean {
        return character.value.feats.any { it.equals(name, ignoreCase = true) }
    }

    fun updateProfileImagePath(path: String?) {
        repository.updateCharacter(character.value.copy(customProfileImagePath = path))
    }

    fun updateFullImagePath(path: String?) {
        repository.updateCharacter(character.value.copy(customFullImagePath = path))
    }
}
