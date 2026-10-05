package com.paladin.app.data.online

import com.paladin.app.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

class DndOnlineRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val baseUrl = "https://api.open5e.com/v1"

    suspend fun search(
        query: String,
        category: OnlineSearchCategory = OnlineSearchCategory.ALL
    ): Result<List<OnlineSearchResultItem>> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            return@withContext Result.success(emptyList())
        }

        try {
            val encodedQuery = URLEncoder.encode(trimmedQuery, "UTF-8")
            val results = mutableListOf<OnlineSearchResultItem>()

            when (category) {
                OnlineSearchCategory.ALL -> {
                    // Query spells, weapons, armor, magic items, feats in parallel/sequence
                    val spells = searchSpellsInternal(encodedQuery).getOrDefault(emptyList())
                    val weapons = searchWeaponsInternal(encodedQuery).getOrDefault(emptyList())
                    val armor = searchArmorInternal(encodedQuery).getOrDefault(emptyList())
                    val magicItems = searchMagicItemsInternal(encodedQuery).getOrDefault(emptyList())
                    val feats = searchFeatsInternal(encodedQuery).getOrDefault(emptyList())

                    results.addAll(spells)
                    results.addAll(weapons)
                    results.addAll(armor)
                    results.addAll(magicItems)
                    results.addAll(feats)
                }
                OnlineSearchCategory.SPELLS -> {
                    results.addAll(searchSpellsInternal(encodedQuery).getOrThrow())
                }
                OnlineSearchCategory.WEAPONS -> {
                    results.addAll(searchWeaponsInternal(encodedQuery).getOrThrow())
                }
                OnlineSearchCategory.ARMOR -> {
                    results.addAll(searchArmorInternal(encodedQuery).getOrThrow())
                }
                OnlineSearchCategory.CONSUMABLES -> {
                    val magicItems = searchMagicItemsInternal(encodedQuery).getOrDefault(emptyList())
                    val consumables = magicItems.filter {
                        it.item.type.isConsumableOrPotion ||
                        it.name.contains("potion", ignoreCase = true) ||
                        it.name.contains("oil", ignoreCase = true) ||
                        it.name.contains("elixir", ignoreCase = true) ||
                        it.name.contains("herb", ignoreCase = true) ||
                        it.name.contains("scroll", ignoreCase = true)
                    }
                    results.addAll(if (consumables.isNotEmpty()) consumables else magicItems)
                }
                OnlineSearchCategory.MAGIC_ITEMS -> {
                    results.addAll(searchMagicItemsInternal(encodedQuery).getOrThrow())
                }
                OnlineSearchCategory.FEATS -> {
                    results.addAll(searchFeatsInternal(encodedQuery).getOrThrow())
                }
            }

            // Sort results by closest match to query
            val sorted = results.sortedByDescending { item ->
                when {
                    item.name.equals(trimmedQuery, ignoreCase = true) -> 100
                    item.name.startsWith(trimmedQuery, ignoreCase = true) -> 50
                    item.name.contains(trimmedQuery, ignoreCase = true) -> 25
                    else -> 0
                }
            }

            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchSpellsInternal(encodedQuery: String): Result<List<OnlineSearchResultItem.SpellResult>> {
        return try {
            val url = "$baseUrl/spells/?search=$encodedQuery&limit=15"
            val responseText = executeGet(url)
            val response = json.decodeFromString<Open5eResponse<Open5eSpellDto>>(responseText)
            val items = response.results.map { dto ->
                OnlineSearchResultItem.SpellResult(mapDtoToSpell(dto))
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchWeaponsInternal(encodedQuery: String): Result<List<OnlineSearchResultItem.ItemResult>> {
        return try {
            val url = "$baseUrl/weapons/?search=$encodedQuery&limit=10"
            val responseText = executeGet(url)
            val response = json.decodeFromString<Open5eResponse<Open5eWeaponDto>>(responseText)
            val items = response.results.map { dto ->
                OnlineSearchResultItem.ItemResult(mapWeaponToItem(dto))
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchArmorInternal(encodedQuery: String): Result<List<OnlineSearchResultItem.ItemResult>> {
        return try {
            val url = "$baseUrl/armor/?search=$encodedQuery&limit=10"
            val responseText = executeGet(url)
            val response = json.decodeFromString<Open5eResponse<Open5eArmorDto>>(responseText)
            val items = response.results.map { dto ->
                OnlineSearchResultItem.ItemResult(mapArmorToItem(dto))
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchMagicItemsInternal(encodedQuery: String): Result<List<OnlineSearchResultItem.ItemResult>> {
        return try {
            val url = "$baseUrl/magicitems/?search=$encodedQuery&limit=15"
            val responseText = executeGet(url)
            val response = json.decodeFromString<Open5eResponse<Open5eMagicItemDto>>(responseText)
            val items = response.results.map { dto ->
                OnlineSearchResultItem.ItemResult(mapMagicItemToItem(dto))
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchFeatsInternal(encodedQuery: String): Result<List<OnlineSearchResultItem.FeatResult>> {
        return try {
            val url = "$baseUrl/feats/?search=$encodedQuery&limit=10"
            val responseText = executeGet(url)
            val response = json.decodeFromString<Open5eResponse<Open5eFeatDto>>(responseText)
            val items = response.results.map { dto ->
                OnlineSearchResultItem.FeatResult(mapFeatToDefinition(dto))
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executeGet(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 8000
        conn.readTimeout = 10000
        conn.setRequestProperty("User-Agent", "PaladinApp-Android/1.0 (D&D 5e Character Sheet)")
        conn.setRequestProperty("Accept", "application/json")

        val responseCode = conn.responseCode
        if (responseCode !in 200..299) {
            throw IllegalStateException("API Server antwortete mit HTTP $responseCode")
        }

        return conn.inputStream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)
    }

    // ------------------------------------------------------------------------
    // MAPPERS: Open5e DTOs -> Paladin App Models
    // ------------------------------------------------------------------------

    fun mapDtoToSpell(dto: Open5eSpellDto): Spell {
        val school = parseSchool(dto.school)
        val isConc = dto.concentration?.equals("yes", ignoreCase = true) == true
        val isRit = dto.ritual?.equals("yes", ignoreCase = true) == true

        val compStr = dto.components.uppercase()
        val hasV = compStr.contains("V")
        val hasS = compStr.contains("S")
        val hasM = compStr.contains("M")

        val levelInt = dto.level_int.coerceIn(0, 9)

        return Spell(
            id = "open5e_${dto.slug.ifBlank { UUID.randomUUID().toString() }}",
            name = dto.name,
            level = levelInt,
            school = school,
            castingTime = dto.casting_time,
            range = dto.range,
            duration = dto.duration,
            isConcentration = isConc,
            isRitual = isRit,
            verbal = hasV,
            somatic = hasS,
            material = hasM,
            materialDescription = dto.material ?: "",
            description = dto.desc + (if (!dto.higher_level.isNullOrBlank()) "\n\nAuf höheren Graden:\n${dto.higher_level}" else ""),
            source = dto.document__title ?: "Open5e / SRD"
        )
    }

    fun mapWeaponToItem(dto: Open5eWeaponDto): Item {
        val rawProps = dto.properties
        var versatileDice: String? = null
        val cleanProps = rawProps.map { prop ->
            if (prop.contains("versatile", ignoreCase = true)) {
                val diceMatch = Regex("""\((.*?)\)""").find(prop)
                if (diceMatch != null) {
                    versatileDice = diceMatch.groupValues[1]
                }
                "Versatile"
            } else {
                prop.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }

        val isVersatile = cleanProps.any { it.contains("versatile", ignoreCase = true) }
        val isFinesse = cleanProps.any { it.contains("finesse", ignoreCase = true) }
        val isHeavy = cleanProps.any { it.contains("heavy", ignoreCase = true) }
        val isLight = cleanProps.any { it.contains("light", ignoreCase = true) }
        val isReach = cleanProps.any { it.contains("reach", ignoreCase = true) }
        val isTwoHanded = cleanProps.any { it.contains("two-handed", ignoreCase = true) }
        val mastery = inferWeaponMastery(dto.name)

        return Item(
            id = "weapon_${dto.slug.ifBlank { UUID.randomUUID().toString() }}",
            name = dto.name,
            type = ItemType.WEAPON,
            description = "Waffenkategorie: ${dto.category}\nSchaden: ${dto.damage_dice} ${dto.damage_type}",
            cost = dto.cost,
            weightLbs = dto.weight.replace("lb.", "").trim().toDoubleOrNull() ?: 3.0,
            quantity = 1,
            isEquipped = false,
            damageDice = dto.damage_dice.ifBlank { "1d8" },
            damageType = dto.damage_type.ifBlank { "Slashing" }.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            isFinesse = isFinesse,
            isHeavy = isHeavy,
            isLight = isLight,
            isReach = isReach,
            isTwoHanded = isTwoHanded,
            isVersatile = isVersatile,
            versatileDamageDice = versatileDice,
            mastery = mastery
        )
    }

    fun mapArmorToItem(dto: Open5eArmorDto): Item {
        val isShield = dto.name.contains("Shield", ignoreCase = true) || dto.category.contains("Shield", ignoreCase = true)
        val armorType = when {
            isShield -> ArmorType.SHIELD
            dto.category.contains("Heavy", ignoreCase = true) -> ArmorType.HEAVY
            dto.category.contains("Medium", ignoreCase = true) -> ArmorType.MEDIUM
            else -> ArmorType.LIGHT
        }

        val acNum = dto.ac_string.filter { it.isDigit() }.toIntOrNull() ?: if (isShield) 2 else 14

        val effects = mutableListOf<ItemEffect>()
        if (isShield) {
            effects.add(ItemEffect.AcBonus(2))
        } else {
            effects.add(ItemEffect.AcFlat(acNum))
        }

        return Item(
            id = "armor_${dto.slug.ifBlank { UUID.randomUUID().toString() }}",
            name = dto.name,
            type = if (isShield) ItemType.SHIELD else ItemType.ARMOR,
            description = "Rüstungskategorie: ${dto.category}\nRüstungsklasse (AC): ${dto.ac_string}" +
                    (if (dto.strength_requirement != null && dto.strength_requirement > 0) "\nStärkeanforderung: STR ${dto.strength_requirement}" else "") +
                    (if (dto.stealth_disadvantage == true) "\nNachteil auf Heimlichkeit (Stealth)" else ""),
            cost = dto.cost,
            weightLbs = dto.weight.replace("lb.", "").trim().toDoubleOrNull() ?: 10.0,
            quantity = 1,
            isEquipped = false,
            baseAc = if (isShield) 2 else acNum,
            stealthDisadvantage = dto.stealth_disadvantage == true,
            minStrength = dto.strength_requirement ?: 0,
            armorType = armorType,
            effects = effects
        )
    }

    fun mapMagicItemToItem(dto: Open5eMagicItemDto): Item {
        val isAttunement = dto.requires_attunement.isNotBlank()
        val typeLower = dto.type.lowercase()
        val nameLower = dto.name.lowercase()

        val itemType = when {
            typeLower.contains("weapon") || nameLower.contains("sword") || nameLower.contains("blade") || nameLower.contains("axe") -> ItemType.WEAPON
            typeLower.contains("armor") || typeLower.contains("shield") -> ItemType.ARMOR
            typeLower.contains("potion") || nameLower.contains("potion") || nameLower.contains("elixir") || nameLower.contains("philter") -> ItemType.POTION
            typeLower.contains("oil") || typeLower.contains("scroll") || nameLower.contains("herb") || nameLower.contains("plant") || nameLower.contains("salve") || nameLower.contains("powder") -> ItemType.CONSUMABLE
            else -> ItemType.MAGIC_ITEM
        }

        val effects = mutableListOf<ItemEffect>()
        // Infer common bonuses
        if (dto.name.contains("+1")) {
            if (itemType == ItemType.WEAPON) {
                effects.add(ItemEffect.AttackBonus(1))
                effects.add(ItemEffect.DamageBonus(1))
            } else if (itemType == ItemType.ARMOR) {
                effects.add(ItemEffect.AcBonus(1))
            }
        } else if (dto.name.contains("+2")) {
            if (itemType == ItemType.WEAPON) {
                effects.add(ItemEffect.AttackBonus(2))
                effects.add(ItemEffect.DamageBonus(2))
            } else if (itemType == ItemType.ARMOR) {
                effects.add(ItemEffect.AcBonus(2))
            }
        } else if (dto.name.contains("+3")) {
            if (itemType == ItemType.WEAPON) {
                effects.add(ItemEffect.AttackBonus(3))
                effects.add(ItemEffect.DamageBonus(3))
            } else if (itemType == ItemType.ARMOR) {
                effects.add(ItemEffect.AcBonus(3))
            }
        }

        val damageDice = if (itemType == ItemType.WEAPON) {
            when {
                nameLower.contains("greatsword") -> "2d6"
                nameLower.contains("longsword") || nameLower.contains("sun blade") -> "1d8"
                nameLower.contains("dagger") -> "1d4"
                nameLower.contains("shortsword") || nameLower.contains("scimitar") -> "1d6"
                else -> "1d8"
            }
        } else "1d8"

        val mastery = if (itemType == ItemType.WEAPON) inferWeaponMastery(dto.name) else null
        val isVersatile = damageDice == "1d8"

        return Item(
            id = "magic_${dto.slug.ifBlank { UUID.randomUUID().toString() }}",
            name = dto.name,
            type = itemType,
            description = "Seltenheit: ${dto.rarity.replaceFirstChar { it.uppercase() }}\nTyp: ${dto.type}\n\n${dto.desc}",
            cost = "Magisch",
            weightLbs = 1.0,
            quantity = 1,
            isEquipped = false,
            requiresAttunement = isAttunement,
            isAttuned = false,
            damageDice = damageDice,
            damageType = if (itemType == ItemType.WEAPON) "Radiant / Magisch" else "Slashing",
            isVersatile = isVersatile,
            versatileDamageDice = if (isVersatile) "1d10" else null,
            mastery = mastery,
            effects = effects
        )
    }

    fun mapFeatToDefinition(dto: Open5eFeatDto): FeatDefinition {
        return FeatDefinition(
            id = "feat_${dto.slug.ifBlank { UUID.randomUUID().toString() }}",
            name = dto.name,
            category = FeatCategory.GENERAL,
            description = dto.desc + (if (!dto.prerequisite.isNullOrBlank()) "\n\nVoraussetzung: ${dto.prerequisite}" else ""),
            mechanicalBenefit = "Offizielles D&D Talent (${dto.document__title ?: "SRD"}). Siehe Beschreibung für Boni."
        )
    }

    // ------------------------------------------------------------------------
    // WIKI / RAW TEXT QUICK PARSER (Säule 2)
    // ------------------------------------------------------------------------

    fun parseRawTextToSpell(rawText: String): Spell? {
        if (rawText.isBlank()) return null
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return null

        val name = lines.first().replace(Regex("""^#+\s*"""), "")
        var level = 1
        var school = SpellSchool.EVOCATION
        var castingTime = "1 Action"
        var range = "Self"
        var duration = "Instantaneous"
        var isConc = false
        var isRit = false
        var components = "V, S"

        // Search in text for keywords
        val lowerText = rawText.lowercase()

        // Level detection
        val levelRegex = Regex("""(\d+)(st|nd|rd|th)?[- ]level|level\s*(\d+)|grad\s*(\d+)""", RegexOption.IGNORE_CASE)
        val levelMatch = levelRegex.find(rawText)
        if (levelMatch != null) {
            val num = levelMatch.groupValues[1].toIntOrNull()
                ?: levelMatch.groupValues[3].toIntOrNull()
                ?: levelMatch.groupValues[4].toIntOrNull()
            if (num != null) level = num.coerceIn(0, 9)
        } else if (lowerText.contains("cantrip") || lowerText.contains("zaubertrick")) {
            level = 0
        }

        // School detection
        SpellSchool.entries.forEach { s ->
            if (lowerText.contains(s.name.lowercase()) || lowerText.contains(s.displayName.lowercase())) {
                school = s
            }
        }

        // Concentration / Ritual
        if (lowerText.contains("concentration") || lowerText.contains("konzentration")) {
            isConc = true
        }
        if (lowerText.contains("ritual")) {
            isRit = true
        }

        // Casting time extraction
        val ctRegex = Regex("""(casting time|zauberzeit):\s*([^\n\r]+)""", RegexOption.IGNORE_CASE)
        ctRegex.find(rawText)?.let { castingTime = it.groupValues[2].trim() }

        // Range extraction
        val rangeRegex = Regex("""(range|reichweite):\s*([^\n\r]+)""", RegexOption.IGNORE_CASE)
        rangeRegex.find(rawText)?.let { range = it.groupValues[2].trim() }

        // Duration extraction
        val durRegex = Regex("""(duration|wirkungsdauer|dauer):\s*([^\n\r]+)""", RegexOption.IGNORE_CASE)
        durRegex.find(rawText)?.let { duration = it.groupValues[2].trim() }

        return Spell(
            id = "custom_wiki_${UUID.randomUUID()}",
            name = name,
            level = level,
            school = school,
            castingTime = castingTime,
            range = range,
            duration = duration,
            isConcentration = isConc,
            isRitual = isRit,
            verbal = lowerText.contains("v"),
            somatic = lowerText.contains("s"),
            material = lowerText.contains("m"),
            materialDescription = "",
            description = rawText,
            source = "Wiki / Textimport"
        )
    }

    private fun parseSchool(schoolStr: String?): SpellSchool {
        if (schoolStr.isNullOrBlank()) return SpellSchool.EVOCATION
        val lower = schoolStr.lowercase()
        return when {
            lower.contains("abjur") || lower.contains("bann") -> SpellSchool.ABJURATION
            lower.contains("conj") || lower.contains("beschwör") -> SpellSchool.CONJURATION
            lower.contains("divin") || lower.contains("erkennt") -> SpellSchool.DIVINATION
            lower.contains("enchant") || lower.contains("verzaub") -> SpellSchool.ENCHANTMENT
            lower.contains("evoc") || lower.contains("hervor") -> SpellSchool.EVOCATION
            lower.contains("illus") -> SpellSchool.ILLUSION
            lower.contains("necro") || lower.contains("nekro") -> SpellSchool.NECROMANCY
            lower.contains("transmut") || lower.contains("verwand") -> SpellSchool.TRANSMUTATION
            else -> SpellSchool.EVOCATION
        }
    }

    private fun inferWeaponMastery(name: String): WeaponMastery? {
        val lower = name.lowercase()
        return when {
            lower.contains("longsword") || lower.contains("langschwert") || lower.contains("sun blade") -> WeaponMastery.SAP
            lower.contains("greatsword") || lower.contains("zweihänder") -> WeaponMastery.GRAZE
            lower.contains("halberd") || lower.contains("hellebarde") || lower.contains("glaive") || lower.contains("greataxe") -> WeaponMastery.CLEAVE
            lower.contains("shortsword") || lower.contains("kurzschwert") || lower.contains("rapier") -> WeaponMastery.VEX
            lower.contains("dagger") || lower.contains("dolch") || lower.contains("scimitar") -> WeaponMastery.NICK
            lower.contains("warhammer") || lower.contains("kriegshammer") || lower.contains("morningstar") || lower.contains("morgenstern") -> WeaponMastery.PUSH
            lower.contains("battleaxe") || lower.contains("streitaxt") || lower.contains("lance") || lower.contains("maul") -> WeaponMastery.TOPPLE
            lower.contains("flail") || lower.contains("flegel") || lower.contains("club") || lower.contains("knüppel") || lower.contains("quarterstaff") -> WeaponMastery.SLOW
            else -> null
        }
    }
}
