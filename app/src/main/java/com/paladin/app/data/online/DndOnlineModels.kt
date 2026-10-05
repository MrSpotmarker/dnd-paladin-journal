package com.paladin.app.data.online

import com.paladin.app.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Open5eResponse<T>(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = emptyList()
)

@Serializable
data class Open5eSpellDto(
    val slug: String = "",
    val name: String = "",
    val desc: String = "",
    val higher_level: String? = null,
    val page: String? = null,
    val range: String = "Self",
    val components: String = "V, S",
    val material: String? = null,
    val ritual: String? = "no",
    val duration: String = "Instantaneous",
    val concentration: String? = "no",
    val casting_time: String = "1 action",
    val level: String? = null,
    val level_int: Int = 1,
    val school: String? = "Evocation",
    val dnd_class: String? = null,
    val document__title: String? = "SRD 5.1"
)

@Serializable
data class Open5eMagicItemDto(
    val slug: String = "",
    val name: String = "",
    val type: String = "Wondrous item",
    val desc: String = "",
    val rarity: String = "common",
    val requires_attunement: String = "",
    val document__title: String? = "SRD 5.1"
)

@Serializable
data class Open5eWeaponDto(
    val slug: String = "",
    val name: String = "",
    val category: String = "Martial Melee Weapons",
    val cost: String = "0 gp",
    val damage_dice: String = "1d8",
    val damage_type: String = "slashing",
    val weight: String = "3 lb.",
    val properties: List<String> = emptyList(),
    val document__title: String? = "SRD 5.1"
)

@Serializable
data class Open5eArmorDto(
    val slug: String = "",
    val name: String = "",
    val category: String = "Heavy Armor",
    val ac_string: String = "16",
    val cost: String = "0 gp",
    val weight: String = "0 lb.",
    val strength_requirement: Int? = null,
    val stealth_disadvantage: Boolean? = null,
    val document__title: String? = "SRD 5.1"
)

@Serializable
data class Open5eFeatDto(
    val slug: String = "",
    val name: String = "",
    val desc: String = "",
    val prerequisite: String? = null,
    val document__title: String? = "SRD 5.1"
)

enum class OnlineSearchCategory(val displayName: String, val icon: String) {
    ALL("Alle", "🌐"),
    SPELLS("Zauber", "✨"),
    WEAPONS("Waffen", "⚔️"),
    ARMOR("Rüstung", "🛡️"),
    CONSUMABLES("Tränke & Pflanzen", "🧪"),
    MAGIC_ITEMS("Magisch", "💍"),
    FEATS("Talente", "📜")
}

sealed class OnlineSearchResultItem {
    abstract val id: String
    abstract val name: String
    abstract val subtitle: String
    abstract val description: String
    abstract val source: String
    abstract val icon: String

    data class SpellResult(
        val spell: Spell,
        override val id: String = spell.id,
        override val name: String = spell.name,
        override val subtitle: String = "Grad ${spell.level} • ${spell.school.displayName}",
        override val description: String = spell.description,
        override val source: String = spell.source,
        override val icon: String = "✨"
    ) : OnlineSearchResultItem()

    data class ItemResult(
        val item: Item,
        override val id: String = item.id,
        override val name: String = item.name,
        override val subtitle: String = when (item.type) {
            ItemType.WEAPON -> "Waffe • ${item.damageDice} ${if (item.isVersatile) "(Versatile)" else ""}"
            ItemType.ARMOR -> "Rüstung • AC ${item.baseAc}"
            ItemType.SHIELD -> "Schild • +2 AC"
            ItemType.MAGIC_ITEM -> "Magischer Gegenstand"
            ItemType.POTION -> "Zaubertrank"
            ItemType.CONSUMABLE -> "Pflanze / Verbrauchsgut"
            ItemType.GEAR -> "Ausrüstung"
        },
        override val description: String = item.description,
        override val source: String = "Open5e / SRD",
        override val icon: String = when (item.type) {
            ItemType.WEAPON -> "⚔️"
            ItemType.ARMOR -> "🛡️"
            ItemType.SHIELD -> "🛡️"
            ItemType.MAGIC_ITEM -> "💍"
            ItemType.POTION -> "🧪"
            ItemType.CONSUMABLE -> "🌿"
            ItemType.GEAR -> "🎒"
        }
    ) : OnlineSearchResultItem()

    data class FeatResult(
        val feat: FeatDefinition,
        override val id: String = "feat_${feat.name.lowercase().replace(" ", "_")}",
        override val name: String = feat.name,
        override val subtitle: String = feat.category.displayName,
        override val description: String = feat.description,
        override val source: String = "Open5e / SRD",
        override val icon: String = "📜"
    ) : OnlineSearchResultItem()
}

sealed class OnlineSearchState {
    object Idle : OnlineSearchState()
    object Loading : OnlineSearchState()
    data class Success(
        val results: List<OnlineSearchResultItem>,
        val query: String,
        val category: OnlineSearchCategory
    ) : OnlineSearchState()
    data class Error(val message: String) : OnlineSearchState()
}
