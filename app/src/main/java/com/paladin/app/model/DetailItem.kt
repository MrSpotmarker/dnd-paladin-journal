package com.paladin.app.model

sealed class DetailItem {
    data class SpellInfo(
        val spell: Spell
    ) : DetailItem()

    data class FeatInfo(
        val feat: FeatDefinition
    ) : DetailItem()

    data class ItemInfo(
        val item: Item
    ) : DetailItem()

    data class FeatureInfo(
        val title: String,
        val subtitle: String? = null,
        val badge: String? = null,
        val icon: String = "✨",
        val description: String,
        val keyProperties: List<Pair<String, String>> = emptyList(),
        val mechanicalBenefits: List<String> = emptyList()
    ) : DetailItem()

    data class AbilityInfo(
        val ability: Ability,
        val score: Int,
        val modifier: Int,
        val isSaveProficient: Boolean,
        val saveBonus: Int
    ) : DetailItem()
}
